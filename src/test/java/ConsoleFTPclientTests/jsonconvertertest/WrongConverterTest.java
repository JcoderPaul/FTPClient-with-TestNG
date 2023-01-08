package ConsoleFTPclientTests.jsonconvertertest;

import ConsoleFTPclient.jsonconverter.JsonConverter;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class WrongConverterTest {
    JsonConverter jc = new JsonConverter();
    String dataForTest_1, dataForTest_2, dataForTest_3,
           dataForTest_4 , dataForTest_5, dataForTest_6,
           dataForTest_7;

    @BeforeTest
    public void beforeTest() {
        dataForTest_1 = "{\"students\":[{\"id\":2,\"na1me\":\"Malkolm\"}]}";
        dataForTest_2 = "{\"students\":[" +
                "{\"id\":5,1\"na1me\":\"Sanara\"}," +
                "{\"id\":8,2\"na2me\":\"Duglas\"}]}";
        dataForTest_3 = "{\"students\":[" +
                "{\"id\":4,\"na1me\":1\"Aerdol\"}," +
                "{\"id\":8,\"na2me\":2\"Hovard\"}," +
                "{\"id\":9,\"na3me\":3\"Timus\"}]}";
        dataForTest_4 = "{\"students\":[" +
                "{\"id\":2,\"na1me\":\"Malkolm\"},1" +
                "{\"id\":4,\"na2me\":\"Sanara\"},2" +
                "{\"id\":8,\"na3me\":\"Duglas\"},3" +
                "{\"id\":12,\"na4me\":\"Shiban\"}4]}";
        dataForTest_5 = "{\"students\":[{" +
                "{\"id\":2,\"name\":\"Malkolm\"}," +
                "{\"id\":4,\"name\":\"Sanara\"}," +
                "{\"id\":8,\"name\":\"Duglas\"}," +
                "{\"id\":12,\"name\":\"Shiban\"}]}";
        dataForTest_6 = "{\"students\":[" +
                "{\"id\":2,\"name\":\"Malkolm\"}," +
                "{\"id\":4,\"name\":\"Sanara\"}," +
                "{\"id\":8,\"name\":\"Duglas\"}," +
                "{\"id\":12,\"name\":\"Shiban\"},]}";
        dataForTest_7 = "";
    }

    @Test(description = "Check readFromBase wrong data",
          dataProvider = "jsonForTest",
          expectedExceptions = RuntimeException.class,
          expectedExceptionsMessageRegExp = "File conversion is not possible\\.")
    public void testСheckNotСorrectReadFromJsonAndConvertToMap(String input) {
        jc.readFromBase(input);
    }

    @DataProvider
    public Object[][] jsonForTest() {
        return new Object[][] {
                {dataForTest_1},
                {dataForTest_2},
                {dataForTest_3},
                {dataForTest_4},
                {dataForTest_5},
                {dataForTest_6},
                {dataForTest_7},
        };
    }
}
