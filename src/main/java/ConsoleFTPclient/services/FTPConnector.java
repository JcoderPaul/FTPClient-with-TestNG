package ConsoleFTPclient.services;
/*
Функционал данного FTPClient - а ограничен
задачей и не имеет, например функционала,
позволяющего выбирать необходимый для чтения
файл из меню. Но работа в активном и пассивном
режиме работы реализована в полной мере.
*/
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class FTPConnector {
    /*
    Первоначальная инициализация всех важных переменных.
    Командный сокет в данном случае может быть только один,
    в отличие от сокета данных, используемый в методах:
    - generatingSocketForActiveFTPMode();
    - switchingFTPClientToPassiveMode();
    */
    private Socket commandSocket = null;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;
    private String ftpHost = null;
    /*
    В данной задаче имя файла с данными, фиксировано,
    лежит в корне, имя мы задаем сами, а не выбираем
    из списка возможных.
    */
    private final String filename = "students.json";
    // Отображение обмена информацией сервер-клиент на экран - наглядность
    private static boolean SCREEN_MESSAGES = false;
    public FTPConnector() {
    }
    /*
    Connects to the default port of an FTP server
    (Login/Pass - anonymous/anonymous@anonymous.com)

    Соединение с сервером только по хосту, порт
    командного канала стандартный 21
    (Пароль/Логин - anonymous/anonymous@anonymous.com)
    */
    public boolean connectToFTPServer(String host) throws IOException {
        return connectToFTPServer(host, 21);
    }
    public boolean connectToFTPServer(String host, int port) throws IOException {
        return connectToFTPServer(host, port, "anonymous", "anonymous@anonymous.com");
    }
    /*
    Connect to an FTP server using a user-specified host, port, password, and login.

    Соединение с FTP сервером по заданному пользователем хосту, порту, паролю и логину.
    */
    public boolean connectToFTPServer(String host, int port, String user,
                                                String pass) throws IOException {
        ftpHost = host;
        boolean isConnected = true;

        if (commandSocket != null) {
            System.out.println("(RU) Программа уже установила связь, принудительный разрыв связи. \n" +
                               "(ENG) FTPClient is already connected. Forced disconnection. \n");
            disconnectFromFTPServer();
        }
        /*
        Создаем командный сокет, на FTP сервере он находится
        на 21 порту, IP адрес сервера должен быть нам заранее
        известен, как пароль и логин доступа.

        Далее создаем буферы чтения - записи данных.
        */
        commandSocket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(
                commandSocket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(
                commandSocket.getOutputStream()));
        // Слушаем ответ сервера на запрос
        String responseFromServer = readLine();
        // Обрабатываем ответ сервера
        if (!responseFromServer.startsWith("220 ")) {
            System.out.println("(RU) Программа получила неизвестный ответ при обращении к FTP серверу. \n" +
                               "(ENG) FTPClient received an unknown response when connecting to the FTP server: "
                               + responseFromServer);
            isConnected = false;
        }
        // В случае удачного подключения к серверу отправляем логин
        sendLine("USER " + user);
        // Слушаем ответ сервера
        responseFromServer = readLine();
        // Обрабатываем ответ сервера
        if (!responseFromServer.startsWith("331 ")) {
            System.out.println("(RU) Программа получила неизвестный ответ от FTP сервера после отправки логина. \n" +
                               "(ENG) FTPClient received an unknown response after sending the user: "
                               + responseFromServer);
            isConnected = false;
        }
        // Передаем данные пароля (по данному блоку см. документацию по FTP командам)
        sendLine("PASS " + pass);

        responseFromServer = readLine();
        if (!responseFromServer.startsWith("230 ")) {
            System.out.println("\n(RU) FTP-сервер не принял введенные пароль и логин \n" +
                               "(повторите попытку/или проверьте введенные данные). \n" +
                               "(ENG) FTP Client has failed to log in with the entering \n" +
                               "password and login (try again/or check entered data): "
                               + responseFromServer);
            isConnected = false;
        }
        return isConnected;
    }

    /*
    Disconnects from the FTP server.
    Разрыв соединения с FTP сервером
    */
    public boolean disconnectFromFTPServer() throws IOException {
        try {
            // Команда на сервер об отключении
            sendLine("QUIT");
            // Слушаем ответ сервера
            String response = readLine();
            // Обрабатываем ответ сервера
            if (!response.startsWith("221 ")) {
                throw new IOException(
                        "\n(RU) Программа не может разорвать соединение с сервером. \n" +
                        "(ENG) FTPClient could not disconnected from FTP Server: "
                        + response);
            }
            return response.startsWith("221 ");
        } finally {
            commandSocket = null;
        }
    }
    /*
    Метод чтения данных из файла на FTP сервере
    */
    public String getDataFromFileOnFTPServer() throws IOException {
        /*
        Блок определяющий режим работы FTP клиента
        (активный, пассивный). По умолчанию стартует
        активный режим, когда инициатором становится
        клиент, в случае неудачи, преходим в пассивный
        режим и ждем канала данных от сервера.
        */
        Socket dataSocket = generatingSocketForActiveFTPMode();
        if (dataSocket == null) {
            dataSocket = switchingFTPClientToPassiveMode();
        }
        /*
        RETR - команда скачать файл, далее имя файл
        */
        sendLine("RETR " + filename);
        // Слушаем ответ сервера
        String responseFTPServer = readLine();
        // Обрабатываем ответ сервера
        if (responseFTPServer.startsWith("550")) {
            throw new IOException("\n(RU) FTPClient не нашел необходимый файл. \n" +
                                  "(ENG) FTPClient did not find the required file: "
                                  + responseFTPServer);
        }
        // Создаем буферезированный входящий поток из сокета данных
        BufferedReader inputReader =
                new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
        /*
        Блок чтения данных из входящего поток данных,
        до тех пор пока данные в файле есть или до
        момента закрытия сокета данных.
        */
        StringBuilder stringForReadFileData = new StringBuilder();
        String line;
        while ((line = inputReader.readLine()) != null && !dataSocket.isClosed()){
            stringForReadFileData.append(line).append('\n');
        }
        // Формируем исходящую строку данных (т.к. JSON файл это обычный текст)
        String completeDataFromFile = stringForReadFileData.toString();
        // Закрываем входящий поток данных
        inputReader.close();
        // Читаем ответ сервера
        readLine();
        /*
        Возвращаем полученные данные (они уйдут в метод
        *.readFromBase(String str_data) класса JsonConverter)
        */
        return completeDataFromFile;
    }

    /*
    Отправляем файл на FTP-сервер (сохраняем данные). Метод возвращает true,
    если файл передан успешно. Файл отправляется сначала в активном режиме,
    затем, при "провале" в пассивном режиме, чтобы избежать проблем с NAT или
    брандмауэром на нашей стороне.

    We send а file to the FTP server (save the data). The method returns true
    if the file was uploaded successfully. The file is sent first in active mode,
    then, on "failure" in passive mode, to avoid problems with NAT or firewall
    on our side.
    */
    public boolean sendFileToFTPServer(StringBuilder sbInput) throws IOException {
        /*
        Данные в этот метод прилетают из метода
        *.writeToBase() класса JsonConverter.
        И сначала нам их нужно прочитать. Формируем
        входящий буферизированный поток данных.
        */
        BufferedInputStream input =
                new BufferedInputStream(
                        new ByteArrayInputStream(sbInput.toString().getBytes(StandardCharsets.UTF_8)));
        /*
        Блок формирующий канал данных с FTP сервером,
        как и в методе чтения данных сначала идет попытка
        активного соединения, затем пассивный режим.
        */
        Socket dataSocket = generatingSocketForActiveFTPMode();
        if (dataSocket == null) {
            dataSocket = switchingFTPClientToPassiveMode();
        }
        /*
        STOR - команда закачать файл на сервер, и имя файла
        */
        sendLine("STOR " + filename);
        // Слушаем ответ сервера
        String responseFromServer = readLine();
        // Обрабатываем ответ сервера
        if (!(responseFromServer.startsWith("150 ") |
                responseFromServer.startsWith("125 "))) {
            throw new IOException("\n(RU) Программа не может отправить файл. \n" +
                                  "(ENG) FTPClient was not allowed to send the file: "
                                  + responseFromServer);
        }
        /*
        Полученные из программы данные нужно
        перезалить на FTP сервер в виде файла.
        Формируем исходящий поток данных.
        */
        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        // Читаем данные из входящего потока, до последнего байта
        while ((bytesRead = input.read(buffer)) != -1) {
            // И тут же пишем данные в исходящий поток
            output.write(buffer, 0, bytesRead);
        }
        /*
        Сбрасываем последние данные из буфера
        в файл и закрываем все потоки данных
        */
        output.flush();
        output.close();
        input.close();
        // Слушаем ответ сервера
        responseFromServer = readLine();
        // Если ответ начинается с 226 - Закрытие канала, обмен данными успешно завершен.
        return responseFromServer.startsWith("226 ");
    }
    // Метод генерирующий канал данных для 'активного' соединения с сервером
    private Socket generatingSocketForActiveFTPMode() throws IOException {
        // Создаем серверный сокет
        ServerSocket serverSocket = new ServerSocket(0);
        // Нам нужен свободный порт для связи с сервером
        int port = serverSocket.getLocalPort();
        int p1 = (port & 0xFF00) >> 8;
        int p2 = port & 0x00FF;
        // IP адрес сервера не меняется, преобразуем его в понятный для сервера же
        String ipToSendOnFTPServer = ftpHost.replaceAll("\\.",",");
        /*
        PORT — команда войти в активный режим. Например PORT 111,111,1,11,78,89.
        В отличие от пассивного режима для передачи данных сервер сам подключается
        к клиенту, по указанному порту.
        */
        sendLine("PORT " + ipToSendOnFTPServer + "," + p1 + "," + p2);
        // Слушаем ответ сервера
        String responseFromServer = readLine();
        // Обрабатываем ответ сервера
        if (!(responseFromServer.startsWith("125 ") | responseFromServer.startsWith("200 "))) {
            System.out.println("\n(RU) FTP-клиент не может работать в \"активном\" режиме \n" +
                               "повторяем попытку в \"пассивном\" режиме передачи данных: \n" +
                               "(ENG) FTP client cannot working in \"active\" mode \n" +
                               "retrying in \"passive\" data transfer mode: "
                               + responseFromServer + "\n");
            return null;
        }
        /*
        Но мы не сервер, а клиент, мы получили свободный порт
        на нашей машине, сформировали команду для FTP сервера
        и переслали ему.

        А теперь создаем сокет данных с сервером, который нужно
        вернуть в основной код.
        */
        Socket dataSocket = serverSocket.accept();
        // Возвращаем сокет данных
        return dataSocket;
    }
    // Метод пассивной связи с FTP сервером
    private Socket switchingFTPClientToPassiveMode() throws IOException {
        /*
        PASV - команда войти в пассивный режим. Сервер
        вернёт адрес и порт, к которому нужно подключиться,
        чтобы забрать данные.
        */
        sendLine("PASV");
        // Читаем ответ сервера (те самые IP и PORT для связи или нечто другое)
        String responseFTPServer = readLine();
        // Обрабатываем ответ сервера
        if (!(responseFTPServer.startsWith("227 ") | responseFTPServer.startsWith("200 "))) {
            throw new IOException("\n(RU) Переход в \"пассивный\" режим не возможен. \n" +
                                  "(ENG) Switching to \"passive\" mode is not possible: "
                                  + responseFTPServer);
        }
        // Парсим ответ сервера и вычленяем IP и PORT
        String ip = null;
        int port = -1;
        int opening = responseFTPServer.indexOf('(');
        int closing = responseFTPServer.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = responseFTPServer.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                        + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = Integer.parseInt(tokenizer.nextToken()) * 256
                        + Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new IOException("\n(RU) Программа получила неверную информацию о канале передачи данных. \n" +
                                      "(ENG) FTPClient received bad data link information: "
                                      + responseFTPServer);
            }
        }
        // И снова создаем сокет данных, но уж в пассивном режиме
        Socket dataSocket = new Socket(ip, port);
        // Возвращаем сокет в основной код
        return dataSocket;
    }
    /*
    Sends a command to the FTP server.
    Отправка команд на FTP сервер.

    Метод нужен для того, чтобы избежать
    дублирования кода, а самое главное,
    для отображения информации в режиме
    отладки и мониторинга.
    */
    private void sendLine(String line) throws IOException {
        if (commandSocket == null) {
            throw new IOException("(RU) Связь с FTP сервером отсутствует. \n" +
                                  "(ENG) FTPClient is not connected. \n");
        }
        try {
            /*
            !!! Не знаю, как реагирует FTP сервер на других ОС
            на отправленные команды (RETR, PASV, PORT и т.д.), но
            стандартный FTP сервер windows 7-10, отказывался
            читать команды не переведенные в начало строки (\r)!!!
            Чего-то я не догоняю.
            */
            writer.write(line + "\r\n");
            writer.flush();

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (SCREEN_MESSAGES) {
                System.out.println("--> " + line);
            }
        } catch (IOException e) {
            commandSocket = null;
            throw e;
        }
    }
    /*
    Getting response from FTP server.
    Получение ответа от FTP сервера.

    Метод нужен для того, чтобы избежать
    дублирования кода, а самое главное,
    для отображения информации в режиме
    отладки и мониторинга.
    */
    private String readLine() throws IOException {
        String line = reader.readLine();

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (SCREEN_MESSAGES) {
            System.out.println("<-- " + line);
        }
        return line;
    }
    /*
    При длительном бездействии FTP сервер может
    разорвать соединение и его нужно, периодически
    'возбуждать'. Для этого существует команда NOOP.

    Данный метод проверяет есть ли вообще соединение
    с сервером и возвращает результат в метод
    *.waitingMenuItemInput() класса MainMenu.
    */
    public boolean getStatusOfRealConnection() throws IOException {
        boolean statusOfConnection = true;
        try {
            sendLine("NOOP");
            readLine();
        } catch (SocketException ex){
            statusOfConnection = false;
        }
        return statusOfConnection;
    }
}