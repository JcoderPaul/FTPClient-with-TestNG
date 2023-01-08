package ConsoleFTPclient.services;

import ConsoleFTPclient.menuitems.*;
import ConsoleFTPclient.jsonconverter.JsonConverter;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

public class MainMenu {
    // Сканер для чтения данных с консоли
    private Scanner SCANNER = new Scanner(System.in);
    private boolean programIsWorking = true;
    // Метод применяется для ввода и передачи аутентификационных данных FTP - серверу
    public boolean getUserDataAndConnect(FTPConnector ftpConnector) {
        int countFailureConnection = 3;
        boolean connectionStatus = false;
        while (!(connectionStatus | (countFailureConnection < 1))){
        /*
        !!! Проверки на правильность введенных данных тут не проводится !!!
        Предполагается, что пользователь знает правила написания IP и т.д.
        */
        System.out.println("Enter server IP-address: ");
        String host = SCANNER.nextLine().trim().split(" ")[0];
        System.out.println("Enter login: ");
        String login = SCANNER.nextLine().trim().split(" ")[0];
        System.out.println("Enter password: ");
        String password = SCANNER.nextLine().trim().split(" ")[0];

        try {
            connectionStatus = ftpConnector.connectToFTPServer(host,21, login, password);
            if(connectionStatus == false){
            countFailureConnection--;
            System.out.print("\n(RU) Количество попыток осталось: " + countFailureConnection + " \n");
            System.out.println("(ENG) Number of attempts left: " + countFailureConnection + " \n");
            }
        } catch (IOException e) {
            countFailureConnection--;
            System.out.println("\n(RU) Превышен период ожидания ответа от сервера, \n" +
                               "возможно вы ввели не верный адрес. Повторите ввод. \nУ вас осталось "
                               + countFailureConnection + " попыток. \n");
            System.out.println("(ENG) Timed out waiting for a response from the server, \n" +
                               "you may have entered a wrong address. Try again.\n" +
                               "You have " + countFailureConnection + " attempts left. \n");
            connectionStatus = false;
        }
        }
        if (connectionStatus == false) {
            System.out.println("\n(RU) Лимит попыток входа на сервер исчерпан. Программа завершила работу. \n" +
                               "Возможно FTP сервер сейчас недоступен, повторите попытку позже.\n");
            System.out.println("(ENG) The limit of attempts to enter the server has been exceeded. \n" +
                               "The program has exited. The FTP server may not be available right now, \n" +
                               "please try again later.\n");
        }
        return connectionStatus;
    }
    /*
    Метод сводящий воедино классы и методы:
    - работающие с FTP сервером;
    - обрабатывающие JSON файл;
    - генерирующие пользовательское меню;
    */
    public void waitingMenuItemInput(BaseReadWriteWorker baseWorker, FTPConnector ftpConnector) throws IOException {
        JsonConverter jsonConverter = new JsonConverter();
        baseWorker.setStudents(jsonConverter.readFromBase(ftpConnector.getDataFromFileOnFTPServer()));
        new HelpMenu().executeMenuItem(baseWorker);
        while (programIsWorking) {
            try {
                MenuItem choiceItem = readMenuItems();
                if (choiceItem == null) {
                    System.out.println("(RU) Вы ничего не ввели. Выберите один из пунктов меню или введите HELP. \n" +
                                       "(ENG) You didn't enter anything. Select one of the menu items or enter HELP.");
                    continue;
                }
                choiceItem.executeMenuItem(baseWorker);
                boolean connectionStatus = ftpConnector.getStatusOfRealConnection();
                if (!connectionStatus) {
                    programIsWorking = false;
                    System.out.println("\n(RU) Связь с сервером потеряна.");
                    System.out.println("(ENG) The connection to the server has been lost.");
                } else {
                    ftpConnector.sendFileToFTPServer(jsonConverter.writeToBase(baseWorker.getStudents()));
                }
                if (programIsWorking == false) {
                    if (!connectionStatus){
                        System.out.println("\n(RU) Программа завершила работу в аварийном режиме, последние изменения не сохранены!");
                        System.out.println("(ENG) The program terminated in emergency mode, last changes not saved! \n");
                        throw new SocketException();
                    } else {
                        ftpConnector.sendFileToFTPServer(jsonConverter.writeToBase(baseWorker.getStudents()));
                        System.out.println("\n(RU) Данные сохраняются в файл.");
                        System.out.println("(ENG) The data is saved to a file.");
                        ftpConnector.disconnectFromFTPServer();
                        System.out.println("\n(RU) Программа завершила работу.");
                        System.out.println("(ENG) The program has completed its work. \n");
                    }
                }
            } catch (RuntimeException e) {
                    e.getMessage();
                    e.printStackTrace();
            }
        }
    }
    /*
    Метод генерирующий основное меню.
    Все классы работающие тут приведены в
    папке menuitems.
    */
    private MenuItem readMenuItems() {
        System.out.println("Выберите пункт меню (Select menu item): ");
        String[] enterItemAndParameter = SCANNER.nextLine().trim().split(" ");
        String enterItem = enterItemAndParameter[0].toLowerCase();
        String enterParameter = null;
        if (enterItemAndParameter.length > 1) {
            enterParameter = enterItemAndParameter[1].trim();
        }
        switch (enterItem) {
            case "help":
                return new HelpMenu();
            case "list":
                return new ListOfStudent();
            case "exit":
                programIsWorking = false;
                return new Exit();
            case "info":
                if (enterParameter == null) {
                    throw new IllegalArgumentException();
                }
                return new ShowStudentById(enterParameter);
            case "add":
                if (enterParameter == null) {
                    throw new IllegalArgumentException();
                }
                return new AddStudent(enterParameter);
            case "remove":
                if (enterParameter == null) {
                    throw new IllegalArgumentException();
                }
                return new RemoveStudentById(enterParameter);
            default:
                return null;
        }
    }
}