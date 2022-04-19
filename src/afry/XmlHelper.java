package afry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlHelper {
    private final static Logger log = LogManager.getLogger(XmlHelper.class.getName());
    private Document xmlDocument;
    private static String lastErrorMessage;
    private boolean nameSpaceAware = false;

    public void nameSpaceAware(boolean nameSpaceAware) {
        this.nameSpaceAware = nameSpaceAware;
    }

    public static String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public static boolean isXml(String xmlString) {
        return xmlString != null && xmlString.trim().startsWith("<");
    }

    public boolean createFromString(String xmlString) {
        Pattern pFind = Pattern.compile("<!DOCTYPE.*\".*\">");
        Matcher matcher = pFind.matcher(xmlString);

        if (matcher.find()) {
            xmlString = matcher.replaceAll("");
        }
        DocumentBuilder builder;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(nameSpaceAware);
        try {
            builder = factory.newDocumentBuilder();
            xmlDocument = builder.parse(new InputSource(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))));
        } catch (ParserConfigurationException | IOException | SAXException exception) {
            log.error("Unable to parse XML. " + exception.getMessage());
            lastErrorMessage = exception.getMessage();
            return false;
        }
        return true;
    }

    public static Document createDocumentFromString(String xmlString) {
        DocumentBuilder builder;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            log.error("Unable to parse XML. " + e.getMessage());
            return null;
        }
    }

    public List<Node> query(String expression) {
        return query(xmlDocument, expression);
    }

    public List<Node> query(Document xmlDocument, String expression) {

        if ("".equals(expression)) {
            log.error("Empty XPath expression.");
            return new ArrayList<>();
        }
        try {
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(new UniversalNamespaceResolver(xmlDocument));
            XPathExpression expr = xpath.compile(expression);
            NodeList result = (NodeList) expr.evaluate(xmlDocument, XPathConstants.NODESET);
            return IntStream.range(0, result.getLength())
                    .mapToObj(result::item)
                    .collect(Collectors.toList());
        } catch (XPathExpressionException e) {
            log.error("Invalid XPath expression : '" + expression + "'");
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static String pretty(Document document) {
        try {
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(document), xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception exception) {
            throw new RuntimeException("Error converting to String", exception);
        }
    }

    public void loadFromFile(String sourceFullPath) {
        try {
            createFromString(new String(Files.readAllBytes(Paths.get(sourceFullPath)), StandardCharsets.UTF_8));
        } catch (IOException exception) {
            log.error("Unable to load XML file: '" + sourceFullPath + "'");
            exception.printStackTrace();
        }
    }

    private static class UniversalNamespaceResolver implements NamespaceContext {
        private final Document sourceDocument;

        public UniversalNamespaceResolver(Document document) {
            sourceDocument = document;
        }

        public String getNamespaceURI(String prefix) {
            if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                return sourceDocument.lookupNamespaceURI(null);
            } else {
                return sourceDocument.lookupNamespaceURI(prefix);
            }
        }

        public String getPrefix(String namespaceURI) {
            return sourceDocument.lookupPrefix(namespaceURI);
        }

        public Iterator<String> getPrefixes(String namespaceURI) {
            return null;
        }

    }

}
