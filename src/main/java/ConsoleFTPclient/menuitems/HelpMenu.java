package ConsoleFTPclient.menuitems;

import ConsoleFTPclient.services.BaseReadWriteWorker;

public class HelpMenu implements MenuItem {
    @Override
    public void executeMenuItem(BaseReadWriteWorker dataBase) {
        System.out.println("\n(RU) Меню доступных команд (для повторного вывода меню, наберите - HELP): \n"
                + "list - показать список студентов отсортированных по имени; \n"
                + "info id - показать информацию о студенте по его ID; \n"
                + "add name - добавить студента в базу данных; \n"
                + "remove id - удалить студента из базы данных по его ID; \n"
                + "exit - завершить работу программы и сохранить все изменения. \n");
        System.out.println("(ENG) Main menu (to display the menu again, enter - HELP): \n"
                + "list - show students list sorted by name; \n"
                + "info id - show info about student by id; \n"
                + "add name - add student in database; \n"
                + "remove id - delete student from database by id; \n"
                + "exit - end program, auto save all changes.");
    }
}
