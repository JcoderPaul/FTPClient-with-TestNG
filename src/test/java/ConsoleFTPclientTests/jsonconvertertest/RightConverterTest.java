package ConsoleFTPclientTests.jsonconvertertest;

import ConsoleFTPclient.jsonconverter.JsonConverter;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.TreeMap;

public class RightConverterTest {
    JsonConverter jc = new JsonConverter();
    String dataForTest_1, dataForTest_2, dataForTest_3, dataForTest_4;
    int countOfStudent_1, countOfStudent_2, countOfStudent_3, countOfStudent_4;
    Map<Integer, String> baseOne, baseTwo, baseThree, baseFour;

    @BeforeTest
    public void beforeTest() {
        dataForTest_1 = "{\"students\":[{\"id\":2,\"name\":\"Malkolm\"}]}";
        countOfStudent_1 = 1;
        dataForTest_2 = "{\"students\":[" +
                "{\"id\":5,\"name\":\"Sanara\"}," +
                "{\"id\":8,\"name\":\"Duglas\"}]}";
        countOfStudent_2 = 2;
        dataForTest_3 = "{\"students\":[" +
                "{\"id\":4,\"name\":\"Aerdol\"}," +
                "{\"id\":8,\"name\":\"Hovard\"}," +
                "{\"id\":9,\"name\":\"Timus\"}]}";
        countOfStudent_3 = 3;
        dataForTest_4 = "{\"students\":[" +
                "{\"id\":2,\"name\":\"Malkolm\"}," +
                "{\"id\":4,\"name\":\"Sanara\"}," +
                "{\"id\":8,\"name\":\"Duglas\"}," +
                "{\"id\":12,\"name\":\"Shiban\"}]}";
        countOfStudent_4 = 4;

        baseOne = new TreeMap<>();
        baseOne.put(2,"Malkolm");

        baseTwo = new TreeMap<>();
        baseTwo.put(5,"Sanara");
        baseTwo.put(8,"Duglas");

        baseThree = new TreeMap<>();
        baseThree.put(4,"Aerdol");
        baseThree.put(8,"Hovard");
        baseThree.put(9,"Timus");

        baseFour = new TreeMap<>();
        baseFour.put(2,"Malkolm");
        baseFour.put(4,"Sanara");
        baseFour.put(8,"Duglas");
        baseFour.put(12,"Shiban");
    }

    @Test(description = "Check readFromBase right data", dataProvider = "jsonForTest")
    public void testСheckСorrectReadFromJsonAndConvertToMap(String input, int expected) {
        int result = jc.readFromBase(input).size();
        Assert.assertEquals(result, expected);
    }

    @Test(description = "Check writeToBase right data", dataProvider = "mapForTest")
    public void testСheckСorrectReadFromMapAndConvertToStringBuilder(Map input, String expected) {
        String resultOfReadFromMap = String.valueOf(jc.writeToBase(input)).replaceAll("\\s+", "");
        String expectedAfterRead = expected;
        boolean result = resultOfReadFromMap.contentEquals(expectedAfterRead);
        Assert.assertTrue(result);
    }

    @DataProvider
    public Object[][] jsonForTest() {
        return new Object[][] {
                { dataForTest_1, countOfStudent_1},
                { dataForTest_2, countOfStudent_2},
                { dataForTest_3, countOfStudent_3},
                { dataForTest_4, countOfStudent_4},
        };
    }
    @DataProvider
    public Object[][] mapForTest() {
        return new Object[][] {
                { baseOne, dataForTest_1},
                { baseTwo, dataForTest_2},
                { baseThree, dataForTest_3},
                { baseFour, dataForTest_4},
        };
    }
}
