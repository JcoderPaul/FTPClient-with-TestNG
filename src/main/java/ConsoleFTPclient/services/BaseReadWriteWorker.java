package ConsoleFTPclient.services;
/*
Модуль работы с базой данных, согласно задания:
- Получение информации по ID;
- Добавление студента с оригинальным ID;
- Удаление студента по ID;
- Получение списка студентов (всей базы);
*/
import java.util.Comparator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseReadWriteWorker {
    private Map<Integer, String> students;
    /*
    Получение студента по ID, отображение информации происходит
    при помощи метода *.executeMenuItem() класса ShowStudentById.
    */
    public boolean getStudentById(int id) {
        boolean studentIs = true;
        if(!students.containsKey(id)){
            studentIs = false;
        }
        return studentIs;
    }
    /*
    Удаление студента по ID, отображение информации о
    действиях и получение аргумента происходит при
    помощи метода *.executeMenuItem() класса
    RemoveStudentById.
    */
    public boolean removeStudentById(int id) {
        boolean studentIs = true;
        if(students.containsKey(id)){
        students.remove(id);
        } else {
            studentIs = false;
        }
        return studentIs;
    }
    /*
    Добавление студента в базу, отображение информации о
    действиях и получение аргумента происходит при
    помощи метода *.executeMenuItem() класса AddStudent.
    */
    public int addStudent(String student_name) {
        /*
        Чистим полученную строку от лишних пробелов по краям
        и берем первую строку перед вероятным пробелом между
        сдвоенным или строенным именем (например, 'Гоша шляпник'
        используем для базы Гоша). Эти ограничения накладывает
        структура JSON файла, где имя должно быть одной строкой
        без пробелов.
        */
        String studentToBase = student_name.trim().split(" ")[0];
        // Проверяем введенные данные на правильность
        String rightNameRegEx =
                "^([a-zA-Z0-9]+|[а-яА-Я0-9]+|[a-zA-Z0-9а-яА-Я0-9]+)$";
        Pattern patternForRightName = Pattern.compile(rightNameRegEx);
        Matcher matcher = patternForRightName.matcher(studentToBase);
        // Обрабатываем ошибку ввода
        if(!matcher.find()) {
            throw new RuntimeException("Invalid student name entered.");
        } else {
        // Если все верно находим максимальный ID в базе
        Integer maxId = students.entrySet().stream()
                                           .map(element -> element.getKey())
                                           .max(Comparator.comparingInt(key -> key))
                                           .orElse(null);
        // Если максимальный ID найден увеличиваем на +1
        if(!(maxId == null)) {
            int newId = maxId + 1;
            // Присваиваем новый ID новому студенту и помещаем в базу
            students.put(newId, student_name);
            return newId;
        } else {
            throw new IllegalArgumentException();
        }
        }
    }

    public Map<Integer, String> getStudents() {
        return students;
    }

    public void setStudents(Map<Integer, String> students) {
        this.students = students;
    }
}