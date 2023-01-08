package ConsoleFTPclient.menuitems;

import ConsoleFTPclient.services.BaseReadWriteWorker;

public class ShowStudentById implements MenuItem {
    private String arg = null;
    public ShowStudentById(String arg) {
        this.arg = arg;
    }
    @Override
    public void executeMenuItem(BaseReadWriteWorker dataBase) {
        if(dataBase.getStudentById(Integer.parseInt(arg))){
           System.out.println("ID - " + Integer.parseInt(arg) +
                              ", name - " + dataBase.getStudents().get(Integer.parseInt(arg)) +
                              " \n");
        } else {
            System.out.println("Студент с ID - " + Integer.parseInt(arg) + " в базе не найден.");
            System.out.println("Student with ID - " + Integer.parseInt(arg) + " not find. \n");
        }
    }
}