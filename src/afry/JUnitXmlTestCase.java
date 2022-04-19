package afry;

import java.util.Locale;
import org.apache.commons.text.StringEscapeUtils;

public class JUnitXmlTestCase {

    String name;
    String className = "ClassName";
    String stdOut = "";
    long duration;
    String errorType = null;
    String stdErr = null;
    String[] stackTrace;

    public JUnitXmlTestCase() {
    }

    public JUnitXmlTestCase(String className, String name, String errorText, String errorType) {
        className(className);
        name(name);
        this.stdErr = errorText;
        this.errorType = errorType;
        this.stackTrace = new String[] { errorText };
        this.duration = 0;
    }

    public JUnitXmlTestCase(String name, String errorText, String errorType) {
        this.className = "class.not.set";
        name(name);
        this.stdErr = errorText;
        this.errorType = errorType;
        this.stackTrace = new String[] { errorText };
        this.duration = 0;
    }

    public JUnitXmlTestCase(String name) {
        name(name);
        this.duration = 0;
    }

    public JUnitXmlTestCase(String className, String name) {
        className(className);
        name(name);
        this.stdErr = null;
        this.errorType = null;
        this.stackTrace = null;
        this.duration = 0;
    }

    public void name(String name) {
        name = StringEscapeUtils.escapeXml11(name);
        this.name = name;
    }

    public String name() {
        return name;
    }

    public String nameUnescape() {
        return StringEscapeUtils.unescapeXml(name);
    }

    public void className(String className) {
        className = StringEscapeUtils.escapeXml11(className);
        this.className = className;
    }

    public void duration(long duration) {
        this.duration = duration;
    }

    public long duration() {
        return duration;
    }

    public void errorType(String errorType) {
        this.errorType = StringEscapeUtils.escapeXml11(errorType);

    }

    public void stdErr(String stdErr) {
        this.stdErr = stdErr;

    }

    public void stacktrace(String[] stackTrace) {
        this.stackTrace = stackTrace;

    }

    public boolean hasErrors() {
        return errorType != null && stdErr != null;
    }

    public void stdOut(String stdOut) {
        this.stdOut = stdOut;
    }

    public String toString() {

        StringBuilder st = new StringBuilder();
        if (stackTrace != null && stackTrace.length > 0) {
            for (String s : stackTrace) {
                st.append(s);
            }
        }
        double durationSecond = duration / 1000.0;

        if (hasErrors()) {
            String x = "<testcase name=\"" + name + "\" classname=\"" + className + "\" time=\"" + String.format(Locale.UK, "%.3f", durationSecond) + "\">"
                    + "<failure type=\"" + errorType + "\"><![CDATA[" + st.toString()
                    + "]]></failure>"
                    + "<system-err><![CDATA[" + stdErr + "]]></system-err>";

            if (!stdOut.isEmpty()) {
                x += "<system-out><![CDATA[" + stdOut + "]]></system-out>";
            }

            x += "</testcase>";
            return x;
        } else {

            String x = "<testcase name=\"" + name + "\" classname=\"" + className + "\" time=\"" + String.format(Locale.UK, "%.3f", durationSecond) + "\">";
            if (!stdOut.isEmpty()) {
                x += "<system-out><![CDATA[" + stdOut + "]]></system-out>";
            }
            x += "</testcase>";
            return x;
        }
    }
}
