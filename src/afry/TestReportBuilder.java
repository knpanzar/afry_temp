package afry;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
//import java.nio.file.Path;
import java.nio.file.Paths;
//import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
//import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.json.JSONArray;
//import org.json.JSONObject;

public class TestReportBuilder {

    private final static Logger logger = LogManager.getLogger(TestReportBuilder.class.getName());

    public static final String TEST_REPORT_FILE_NAME = "testReport.xml";

    public static JUnitXmlTestCase buildJUnitTestReport(String testCaseName, String className, long time) {
        JUnitXmlTestCase juxtc = new JUnitXmlTestCase();
        juxtc.name(testCaseName);
        juxtc.className(className);
        juxtc.duration(time);
        return juxtc;
    }

    public static JUnitXmlTestCase buildJUnitTestReport(String testCaseName, String className, long time, String stdOut) {
        JUnitXmlTestCase juxtc = new JUnitXmlTestCase();
        juxtc.name(testCaseName);
        juxtc.className(className);
        juxtc.duration(time);
        juxtc.stdOut(stdOut);
        return juxtc;
    }

    public static JUnitXmlTestCase buildJUnitErrorTestReport(String testCaseName, String className, long duration, String errorType, String stdErr,
            String[] stackTrace) {
        JUnitXmlTestCase juxtc = new JUnitXmlTestCase();
        juxtc.name(testCaseName);
        juxtc.className(className);
        juxtc.duration(duration);
        juxtc.errorType(errorType);
        juxtc.stdErr(stdErr);
        juxtc.stacktrace(stackTrace);
        return juxtc;
    }

    public static JUnitXmlTestCase buildJUnitErrorTestReport(String testCaseName, String className, long duration, String errorType, String stdErr, String stdOut,
            String[] stackTrace) {
        JUnitXmlTestCase juxtc = new JUnitXmlTestCase();
        juxtc.name(testCaseName);
        juxtc.className(className);
        juxtc.duration(duration);
        juxtc.errorType(errorType);
        juxtc.stdErr(stdErr);
        juxtc.stdOut(stdOut);
        juxtc.stacktrace(stackTrace);
        return juxtc;
    }

    public static void writeJUnitTestReport(List<JUnitXmlTestCase> testCases) {
        int skipped = 0, errors = 0;

        if (testCases.size() > 0) {

            TestReport testReport = new TestReport(testCases);

            double durationSecond = testReport.duration() / 1000.0;

            String report = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<testsuite xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://maven.apache.org/surefire/maven-surefire-plugin/xsd/surefire-test-report.xsd\" name=\"se.lf.testframework.Main\" "
                    + "time=\"" + String.format(Locale.UK, "%.3f%n", durationSecond) + "\" tests=\"" + testReport.numberOfTestCases() + "\" errors=\"" + errors
                    + "\" skipped=\"" + skipped + "\" failures=\"" + testReport.failures() + "\">"
                    + "<properties>"
                    + "<property name=\"os.name\" value=\"Windows 10\"/>"
                    + "</properties>"
                    + testReport.getTestCasesXml().toString()
                    + "</testsuite>";

           
            try (FileOutputStream fos = new FileOutputStream(Paths.get(TEST_REPORT_FILE_NAME).toFile(), false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter out = new PrintWriter(bw)) {
    
                out.print(XmlHelper.pretty(XmlHelper.createDocumentFromString(report)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

/*    public static void writeCompareHtml(Path pathToconfig, List<CompareBody> responses) {
        XmlParser.Node html = XmlParser.buildXml(null, "html/head/title", "compare");

        StringBuilder sb = new StringBuilder();
        sb.append("body {font-family: Arial, Helvetica, sans-serif;}");
        sb.append("table {background-color: efebff;}");
        sb.append("tr td {vertical-align: top;}");
        sb.append("tr:nth-child(even) {background-color: #d5caff}");

        sb.append("div.scroll {width: 410px; height: 110px; overflow: scroll;}");

        html = XmlParser.buildXml(html, "html/head/style", sb.toString());
        html = XmlParser.buildXml(html, "html/body/h2", "compare report");
        html = XmlParser.buildXml(html, "html/body[2]/div/span", "file: " + pathToconfig.toString().replace("/", "&#47;").replace("\\", "&#92;"));

        List<String> jsonCompares = new ArrayList<>();
        if (responses.size() == 2) {
            String previously = "";
            for (CompareBody compareBody : responses) {
                if (JsonHelper.isJsonObject(compareBody.response()) || JsonHelper.isJsonArray(compareBody.response())) {
                    if (!previously.isEmpty()) {
                        jsonCompares.add(JsonCompare.compareToHtmlTable(previously, compareBody.response()));
                        previously = compareBody.response();
                    } else {
                        previously = compareBody.response();
                    }
                }
            }
        }

        String lastCompareName = "";
        int count = 0;
        int jsonCompareCount = 0;
        for (CompareBody compareBody : responses) {

            if (!lastCompareName.equals(compareBody.compareName())) {
                count++;
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count++ + "]/span", "compare name: " + compareBody.compareName());
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[1]/tr/td", "test name");
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[2]/tr/td", "file");
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[3]/tr/td", "api");
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[4]/tr/td", "jsonResponseDiff");
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[5]/tr/td", "request");
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[6]/tr/td", "response");
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[7]/tr/td", "requestPaths");
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[8]/tr/td", "responsePaths");
            }

            html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[1]/tr/td", compareBody.name());
            html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[2]/tr/td", compareBody.file());
            html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[3]/tr/td", compareBody.api());

            if (JsonHelper.isJsonObject(compareBody.request()) && JsonHelper.isJsonObject(compareBody.response())) {

                if (jsonCompareCount < jsonCompares.size()) {
                    html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[4]/tr[]/td/pre", jsonCompares.get(jsonCompareCount++));
                } else {
                    html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[4]/tr[]/td/pre", "");
                }

                JSONObject joRequest = new JSONObject(compareBody.request());
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[5]/tr[]/td/pre", joRequest.toString(4));
                JSONObject joResponse = new JSONObject(compareBody.response());
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[6]/tr[]/td/pre", joResponse.toString(4));

                final StringBuilder sbRequest = getPathAndValues(joRequest);
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[7]/tr[]/td/pre", sbRequest.toString());

                final StringBuilder sbResponse = getPathAndValues(joResponse);
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[8]/tr[]/td/pre", sbResponse.toString());

            } else if (!JsonHelper.isJsonObject(compareBody.request()) && JsonHelper.isJsonArray(compareBody.response())) {

                if (jsonCompareCount < jsonCompares.size()) {
                    html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[4]/tr[]/td/pre", jsonCompares.get(jsonCompareCount++));
                } else {
                    html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[4]/tr[]/td/pre", "");
                }

                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[5]/tr[]/td/div/pre", compareBody.request());
                html = XmlParser.buildXmlAttribute(html, "html/body[3]/div[" + count + "]/table[4]/tr/td[2]/div", "class=scroll");

                JSONArray joResponse = new JSONArray(compareBody.response());
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[6]/tr[]/td/pre", joResponse.toString(4));

                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[7]/tr[]/td/div/pre", compareBody.request());
                html = XmlParser.buildXmlAttribute(html, "html/body[3]/div[" + count + "]/table[6]/tr/td[2]/div", "class=scroll");

                final StringBuilder sbResponse = getPathAndValues(joResponse);
                html = XmlParser.buildXml(html, "html/body[3]/div[" + count + "]/table[8]/tr[]/td/pre", sbResponse.toString());
            }

            lastCompareName = compareBody.compareName();
        }

        FileHandler.writeToFile(Paths.get("compareReport.html"), html.toString());
        html.destroy();
    }*/
/*
    private static StringBuilder getPathAndValues(JSONObject joRequest) {
        List<String> requestPaths = JSONBuilder.getPaths(joRequest).stream().sorted().collect(Collectors.toList());
        final StringBuilder sbRequest = new StringBuilder();
        requestPaths.forEach(s -> sbRequest.append(s + ": " + JSONBuilder.getValueFromPath(joRequest, s) + "\n"));
        return sbRequest;
    }

    private static StringBuilder getPathAndValues(JSONArray joRequest) {
        List<String> requestPaths = JSONBuilder.getPaths(joRequest).stream().sorted().collect(Collectors.toList());
        final StringBuilder sbRequest = new StringBuilder();
        requestPaths.forEach(s -> sbRequest.append(s + ": " + JSONBuilder.getValueFromPath(joRequest, s) + "\n"));
        return sbRequest;
    }*/
}