package ConsoleFTPclient.menuitems;

import ConsoleFTPclient.services.BaseReadWriteWorker;

public class AddStudent implements MenuItem {

    private String arg = null;
    public AddStudent(String arg) {
        this.arg = arg;
    }

    @Override
    public void executeMenuItem(BaseReadWriteWorker dataBase) {
        int newId = dataBase.addStudent(arg);
        System.out.println("В базу добавлен студент: " + arg + " с ID - " + newId);
        System.out.println("Student: " + arg + " with ID - " + newId + " was added. \n");

    }
}
