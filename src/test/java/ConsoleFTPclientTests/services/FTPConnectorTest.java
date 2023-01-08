package ConsoleFTPclientTests.services;

import ConsoleFTPclient.services.FTPConnector;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

public class FTPConnectorTest {
    String wrongIp = "123.128.0.13";
    String wrongUser = "wrongadmin";
    String wrongPass = "wrongenter";

    FTPConnector ftpConnector = new FTPConnector();


    @Test(description = "Check connect to FTP server with right IP, Port, Pass and Login", priority = 0)
    @Parameters({ "ip", "user", "pass" })
    public void testConnectToFTPServerWithRightVerificationData(@Optional("191.168.0.1") String ip,
                                                                @Optional("myuser") String user,
                                                                @Optional("mypass") String pass){
        try {
            Assert.assertTrue(ftpConnector.connectToFTPServer( ip, 21, user, pass));
            ftpConnector.disconnectFromFTPServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(description = "Check connect to FTP Server with wrong IP",
          expectedExceptions = RuntimeException.class,
          expectedExceptionsMessageRegExp = "java.net.ConnectException: Connection timed out: connect",
          priority = 1)
    public void testConnectToFTPServerWithWrongIP(){
        try {
            ftpConnector.connectToFTPServer(wrongIp,21,wrongUser,wrongPass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test (description = "Check connect to FTP server with right IP and Port, but wrong Pass or Login",
           priority = 2)
    @Parameters({"ip"})
    public void testConnectToFTPServerWithWrongPassOrLogin(@Optional("191.168.0.1") String ip){
        try {
            Assert.assertFalse(ftpConnector.connectToFTPServer(ip, 21, wrongUser, wrongPass));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
