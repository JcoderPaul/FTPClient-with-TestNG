package ConsoleFTPclientTests.services;

import ConsoleFTPclient.services.FTPConnector;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

public class FTPConnectorMethodsTest {
    FTPConnector ftpConnector = new FTPConnector();
    StringBuilder sbToSend = new StringBuilder(
                    "{\"students\":[" +
                    "{\"id\":4,\"name\":\"Aerdol\"}," +
                    "{\"id\":8,\"name\":\"Hovard\"}," +
                    "{\"id\":9,\"name\":\"Timus\"}]}");

    @BeforeTest
    @Parameters({ "ip", "user", "pass" })
    public void beforeTest(@Optional("191.168.0.1") String ip,
                           @Optional("myuser") String user,
                           @Optional("mypass") String pass) {
        try {
            ftpConnector.connectToFTPServer(ip, 21, user, pass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(description = "Check GetDataFromFileOnFTPServer method",
          priority = 0)
    public void testGetDataFromFileOnFTPServer(){
        try {
            String strFromFile = ftpConnector.getDataFromFileOnFTPServer();
            boolean notNull;
            if(strFromFile.length() > 0){
              notNull = true;
            } else {
              notNull = false;
            }
            Assert.assertTrue(notNull,"File is empty!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(description = "Check testSendFileToFTPServer method",
          priority = 1)
    public void testSendFileToFTPServer(){
        try {
            Assert.assertTrue(ftpConnector.sendFileToFTPServer(sbToSend), "File not transfer!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(description = "Check DisconnectFromFTPServer method",
          priority = 2)
    public void testDisconnectFromFTPServer() {
        try {
            Assert.assertTrue(ftpConnector.disconnectFromFTPServer());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
