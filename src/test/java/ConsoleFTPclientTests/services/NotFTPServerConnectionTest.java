package ConsoleFTPclientTests.services;

import ConsoleFTPclient.services.FTPConnector;
import org.testng.annotations.Test;

import java.io.IOException;

public class NotFTPServerConnectionTest {

    FTPConnector ftpConnector = new FTPConnector();
    String ip = "111.111.1.11";
    int port = 21;
    String user = "YourLogin";
    String pass = "YourPass";
    StringBuilder sbToSend = new StringBuilder(
                    "{\"students\":[" +
                    "{\"id\":4,\"name\":\"Aerdol\"}," +
                    "{\"id\":8,\"name\":\"Hovard\"}," +
                    "{\"id\":9,\"name\":\"Timus\"}]}");

    @Test(description = "Check connect to FTP Server with wrong IP",
            expectedExceptions = RuntimeException.class,
            expectedExceptionsMessageRegExp = "Connection timed out: connect",
            priority = 0)
    public void testNoConnection() {
        try {
            ftpConnector.connectToFTPServer(ip, port, user, pass);
        } catch (IOException e) {
            throw new RuntimeException("Connection timed out: connect");
        }
    }

    @Test(description = "Check GetDataFromFileOnFTPServer method without connection",
          expectedExceptions = RuntimeException.class,
          expectedExceptionsMessageRegExp = "FTPClient is not connected. Receiving data is not possible.",
          priority = 1)
    public void testGetDataFromFileOnNoConnectionFTPServer(){
        try {
            ftpConnector.getDataFromFileOnFTPServer();
        } catch (IOException e) {
            throw new RuntimeException("FTPClient is not connected. Receiving data is not possible.");
        }
    }

    @Test(description = "Check testSendFileToFTPServer method without connection",
          expectedExceptions = RuntimeException.class,
          expectedExceptionsMessageRegExp = "FTPClient is not connected. Sending data is not possible.",
          priority = 2)
    public void testSendFileToNoConnectionFTPServer(){
        try {
            ftpConnector.sendFileToFTPServer(sbToSend);
        } catch (IOException e) {
            throw new RuntimeException("FTPClient is not connected. Sending data is not possible.");
        }
    }

    @Test(description = "Check DisconnectFromFTPServer method without connection",
          expectedExceptions = RuntimeException.class,
          expectedExceptionsMessageRegExp = "FTPClient is not connected",
          priority = 3)
    public void testDisconnectFromNoConnectionFTPServer() {
        try {
            ftpConnector.disconnectFromFTPServer();
        } catch (IOException e) {
            throw new RuntimeException("FTPClient is not connected");
        }
    }
}
