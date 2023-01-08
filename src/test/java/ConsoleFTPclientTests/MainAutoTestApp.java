package ConsoleFTPclientTests;
/*
Смотреть папку с документацией.
View documentation folder.
*/
import org.testng.TestNG;
import org.testng.collections.Lists;

import java.util.List;

public class MainAutoTestApp {
    public static void main(String[] args) {
        TestNG testNG = new TestNG();
        /*
        Для запуска тестов из JAR файла в консоле, данную строку
        необходимо раскомментировать, а FTPСlientTest.xml передать
        в качестве параметров при запуске.

        To run tests from a jar file in the console, this line must
        be uncommented, and FTPСlientTest.xml must be passed as
        parameters at startup.

        String suiteFileName = args[0];
        */

        List<String> suites = Lists.newArrayList();
        /*
        For testing from console:
        suites.add(suiteFileName);

        For local testing:
        */
        suites.add("src/test/resources/FTPСlientTest.xml");

        testNG.setTestSuites(suites);
        testNG.run();
    }
}
