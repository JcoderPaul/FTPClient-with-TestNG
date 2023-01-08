package ConsoleFTPclientTests.services;

import ConsoleFTPclient.services.BaseReadWriteWorker;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BaseReadWriteTest {
    BaseReadWriteWorker bwr;
    Map<Integer, String> modelBase, baseOne, baseTwo, baseThree, baseFour;
    int originalSizeOne, originalSizeTwo, originalSizeThree, originalSizeFour;

    @BeforeTest
    public void beforeTest() {
        modelBase = new HashMap<>();
        bwr = new BaseReadWriteWorker();

        baseOne = new TreeMap<>();
        baseOne.put(2,"Malkolm");
        originalSizeOne = baseOne.size();

        baseTwo = new TreeMap<>();
        baseTwo.put(5,"Sanara");
        baseTwo.put(8,"Duglas");
        originalSizeTwo = baseTwo.size();

        baseThree = new TreeMap<>();
        baseThree.put(4,"Aerdol");
        baseThree.put(8,"Hovard");
        baseThree.put(9,"Timus");
        originalSizeThree = baseThree.size();

        baseFour = new TreeMap<>();
        baseFour.put(2,"Malkolm");
        baseFour.put(4,"Sanara");
        baseFour.put(8,"Duglas");
        baseFour.put(12,"Shiban");
        originalSizeFour = baseFour.size();
    }

    @Test(description = "Check getStudentById if ID existing",
          dataProvider = "mapForTest",
          priority = 0)
    public void testIfRequestTheIDExistingStudentMAPSizeNotChangeMethodReturnTrue(Map input,
                                                                                  int getStudentById,
                                                                                  int mapSize) {
        bwr.setStudents(input);
        Assert.assertTrue(bwr.getStudentById(getStudentById), "Student NOT FOUND in database");
        int result = bwr.getStudents().size();
        Assert.assertEquals(result, mapSize);
    }

    @Test(description = "Check getStudentById if ID NOT existing",
          dataProvider = "mapForBadRemoveTest",
          priority = 1)
    public void testIfRequestNonExistingStudentFromMAPSizeCollectionNotChangeMethodReturnFalse (Map input,
                                                                                    int getStudentById,
                                                                                    int mapSize){
        bwr.setStudents(input);
        Assert.assertFalse(bwr.getStudentById(getStudentById), "Student FOUND in database");
        int result = bwr.getStudents().size();
        Assert.assertEquals(result, mapSize);
    }

    @Test(description = "Check badRemoveStudentById",
            dataProvider = "mapForBadRemoveTest",
            priority = 2)
    public void testIfRemoveNonExistingStudentFromMAPSizeCollectionNotChangeMethodReturnFalse (Map input,
                                                                                               int getStudentById,
                                                                                               int mapSize){
        bwr.setStudents(input);
        Assert.assertFalse(bwr.removeStudentById(getStudentById), "Student FOUND in database");
        int result = bwr.getStudents().size();
        Assert.assertEquals(result, mapSize);
    }

    @Test(description = "Check goodRemoveStudentById",
          dataProvider = "mapForTest",
          priority = 3)
    public void testIfRemoveExistingStudentFromMAPSizeOfCollectionChangeMethodReturnTrue (Map input,
                                                                                          int getStudentById,
                                                                                          int mapSize) {
        bwr.setStudents(input);
        Assert.assertTrue(bwr.removeStudentById(getStudentById), "Student NOT FOUND in database");
        int result = bwr.getStudents().size();
        Assert.assertEquals(result, mapSize - 1);
    }

    @Test(description = "Check right addStudent name",
            dataProvider = "mapForAddStudentToBase",
            priority = 4)
    public void testIfAddStudentSizeBaseWillIncreaseMethodReturnStudentSerialNumber (Map baseInput,
                                                                                     String inputName) {
        bwr.setStudents(baseInput);
        int startBaseSize = bwr.getStudents().size();
        int generateID = bwr.addStudent(inputName);
        int result = bwr.getStudents().size();
        Assert.assertEquals(result, startBaseSize + 1);
        Assert.assertEquals(inputName, bwr.getStudents().get(generateID));
    }

    @Test(description = "Check wrong addStudent name",
            dataProvider = "mapForAddIllegalStudentNameToBase",
            expectedExceptions = RuntimeException.class,
            expectedExceptionsMessageRegExp = "Invalid student name entered\\.",
            priority = 5)
    public void testIfAddInvalidNameSizeDatabaseNotIncreaseMethodReturnException(Map baseInput,
                                                                                 String inputName){
        bwr.setStudents(baseInput);
        int startBaseSize = bwr.getStudents().size();
        bwr.addStudent(inputName);
        int sizeAfterAdded = bwr.getStudents().size();
        Assert.assertEquals(startBaseSize, sizeAfterAdded);
    }

    @DataProvider
    public Object[][] mapForTest() {
        return new Object[][] {
                {baseOne, 2, originalSizeOne},
                {baseTwo, 5, originalSizeTwo},
                {baseThree, 8, originalSizeThree},
                {baseFour, 12, originalSizeFour},
        };
    }
    @DataProvider
    public Object[][] mapForBadRemoveTest() {
        return new Object[][] {
                {baseOne, 22, originalSizeOne},
                {baseTwo, 55, originalSizeTwo},
                {baseThree, 88, originalSizeThree},
                {baseFour, 112, originalSizeFour},
        };
    }

    @DataProvider
    public Object[][] mapForAddStudentToBase() {
        return new Object[][] {
                {baseFour, "Victor1"},
                {baseFour, "Vlad3"},
                {baseFour, "7emen"},
                {baseFour, "6estiglav"},
        };
    }

    @DataProvider
    public Object[][] mapForAddIllegalStudentNameToBase() {
        return new Object[][] {
                {baseFour, "^&Victor1"},
                {baseFour, "Вл&*ад3"},
                {baseFour, "7ем)(&ен"},
                {baseFour, "6ести@$(глав"},
        };
    }
}
