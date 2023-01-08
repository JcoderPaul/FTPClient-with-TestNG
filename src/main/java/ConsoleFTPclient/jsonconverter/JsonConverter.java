package ConsoleFTPclient.jsonconverter;
/*
Простенький JSON конвертер для чтения и записи
единственного варианта JSON файла (см. документацию
к данной программе)
*/
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonConverter {
    /*
    Чтение данных из JSON файла, происходят в методе
    *.getDataFromFileOnFTPServer() класса FTPConnector,
    он возвращает текстовую строку, которая в классе
    MainMenu, через метод *.waitingMenuItemInput()
    попадает в данный метод.
    */
    public Map<Integer, String> readFromBase(String data) {
        // Создаем карту будущих объектов
        Map<Integer, String> students = new HashMap<>();
        // Проверяем структуру полученной строки (JSON файла)
        String regexCorrectFile =
                "(\\{\"students\":\\[)" +
                "(((\\{\"id\":)(\\d+)(,\"name\":\")([a-zA-Z0-9]+|[а-яА-Я0-9]+|[a-zA-Z0-9а-яА-Я0-9]+)(\"})),)*?" +
                "(((\\{\"id\":)(\\d+)(,\"name\":\")([a-zA-Z0-9]+|[а-яА-Я0-9]+|[a-zA-Z0-9а-яА-Я0-9]+)" +
                "(\"})))(]})";
        // Вычленяем объект из JSON файла
        String regexFindObject =
                "(\\{\"id\":)(\\d+)(,\"name\":\")" +
                "([a-zA-Z0-9]+|[а-яА-Я0-9]+|[a-zA-Z0-9а-яА-Я0-9]+)" +
                "(\"})";
        // Удаляем все пробелы из полученной строки
        data = data.replaceAll("\\s+", "");
        /*
        Используя классы Pattern, Matcher и их методы
        определяем правильность структуры полученных
        данных, т.е. верность полученного JSON файла.
        */
        Pattern jsonFileIsCorrect = Pattern.compile(regexCorrectFile);
        Matcher matcherForCorrectLoadFile = jsonFileIsCorrect.matcher(data);
        // Обрабатываем не верный формат
        if(!matcherForCorrectLoadFile.find()){
            System.out.println("Загружаемый файл не является JSON файлом или он поврежден!");
            System.out.println("The file you are uploading is not a JSON file or it is corrupted! \n");
            throw new RuntimeException("File conversion is not possible.");
        }
        // Если все верно, то извлекаем все JSON объекты из строки (файла)
        Pattern patternForFindJsonObject = Pattern.compile(regexFindObject);
        Matcher matcherForFindStudent = patternForFindJsonObject.matcher(data);
        while (matcherForFindStudent.find()) {
            // Читаем объекты и размещаем в коллекцию-карту студентов
                int id = Integer.parseInt(matcherForFindStudent.group(2));
                String name = matcherForFindStudent.group(4);
                students.put(id, name);
        }
        // Возвращаем коллекцию в основной код
        return students;
    }
    /*
    Метод обратного преобразования данных из коллекции в текст.
    Метод возвращает StringBuilder, который попадет в метод
    *.sendFileToFTPServer() класса FTPConnector.

    При этом строковый билдер имеет исходную структуру JSON файла.
    */
    public StringBuilder writeToBase(Map<Integer, String> students){
        StringBuilder prepareDataToBase = new StringBuilder();
        prepareDataToBase.append("{\n");
        prepareDataToBase.append("\"students\": [");
        for (Map.Entry student: students.entrySet()) {
            prepareDataToBase.append("\n{\n" +
                                     "\"id\": " + student.getKey() + ",\n" +
                                     "\"name\": \"" + student.getValue() + "\"\n" +
                                     "},");
        }
        prepareDataToBase.deleteCharAt(prepareDataToBase.lastIndexOf(","));
        prepareDataToBase.append("\n]\n}");
        return prepareDataToBase;
    }
}
