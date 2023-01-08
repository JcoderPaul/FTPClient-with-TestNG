package ConsoleFTPclient.menuitems;

import ConsoleFTPclient.services.BaseReadWriteWorker;

public class RemoveStudentById implements MenuItem {

    private String arg = null;
    public RemoveStudentById(String arg) {
        this.arg = arg;
    }

    @Override
    public void executeMenuItem(BaseReadWriteWorker dataBase) {
            if(dataBase.removeStudentById(Integer.parseInt(arg))){
                System.out.println("Студент с ID - " + Integer.parseInt(arg) + " успешно удален.");
                System.out.println("Student with ID - " + Integer.parseInt(arg) + " was removed. \n");
            } else {
                System.out.println("Студент с ID - " + Integer.parseInt(arg) + " в базе не найден.");
                System.out.println("Student with ID - " + Integer.parseInt(arg) + " not find. \n");
            }
    }
}
