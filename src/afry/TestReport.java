package afry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestReport {

    private int numberOfTestCases = 0;
    private int failures = 0;
    private double duration = 0;
    private StringBuilder testCaseXml = null;

    private List<String> errorTestCases = new ArrayList<>();

    public TestReport(List<JUnitXmlTestCase> testCases) {
        numberOfTestCases = 0;
        failures = 0;
        duration = 0;
        testCaseXml = new StringBuilder();

        HashMap<String, Integer> duplicates = new HashMap<>();
        for (JUnitXmlTestCase tc : testCases) {
            String name = tc.name();
            int i = 1;
            if (duplicates.containsKey(name)) {
                i = duplicates.get(name);
                tc.name(name + " (duplicate: " + i + ")");
                i++;
            }
            duplicates.put(name, i);

            duration += tc.duration();
            if (tc.hasErrors()) {
                errorTestCases.add(tc.nameUnescape());
                failures++;
            }
            numberOfTestCases++;
            testCaseXml.append(tc.toString());
            tc.name(name);
        }
    }

    public void failures(int i) {
        failures = i;
    }

    public int failures() {
        return failures;
    }

    public void numberOfTestCases(int i) {
        numberOfTestCases = i;
    }

    public int numberOfTestCases() {
        return numberOfTestCases;
    }

    public void duration(double d) {
        duration = d;
    }

    public double duration() {
        return duration;
    }

    public StringBuilder getTestCasesXml() {
        return testCaseXml;
    }

    public List<String> errorTestCases() {
        return errorTestCases;
    }
}

