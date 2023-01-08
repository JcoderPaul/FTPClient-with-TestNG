package ConsoleFTPclient;
/*
Программа имеет четыре основных блока (класса):
1. FTPConnector - блок отвечающий за связь с FTP
                  cервером и обмен данными;
2. BaseReadWriteWorker - блок отвечающий за работу с данными
                         полученными из JSON файла;
3. JsonConverter - JSON туда-сюда конвертор;
4. MainMenu - блок меню;
*/
import ConsoleFTPclient.services.BaseReadWriteWorker;
import ConsoleFTPclient.services.FTPConnector;
import ConsoleFTPclient.services.MainMenu;


public class MainApp {
    public static void main(String[] args) {
        FTPConnector ftpConnector = new FTPConnector();
        BaseReadWriteWorker db = new BaseReadWriteWorker();
        MainMenu menu = new MainMenu();
        try {
           if(menu.getUserDataAndConnect(ftpConnector)) {
               menu.waitingMenuItemInput(db, ftpConnector);
           }
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }
}