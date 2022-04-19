package afry;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;  

public class MyTest 
{ 

    static List<JUnitXmlTestCase> testReports = null;
    @BeforeClass
    public static void setup() {
         testReports = new ArrayList<>();
    }
    @Test
    public void test1() {
        assertTrue(true);
        testReports.add(TestReportBuilder.buildJUnitTestReport("test1", "MyTest",100));
    }
    @Test
    public void test2() {
        assertTrue(true);
        testReports.add(TestReportBuilder.buildJUnitTestReport("test2", "MyTest",200));
    }
    @Test
    public void test3() {
        assertTrue(true);
        testReports.add(TestReportBuilder.buildJUnitTestReport("test3", "MyTest",400));
    }
    @AfterClass
    public static void teardown() {
        TestReportBuilder.writeJUnitTestReport(testReports);
    }
        
}
