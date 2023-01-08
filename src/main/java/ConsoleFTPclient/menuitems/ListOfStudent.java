package ConsoleFTPclient.menuitems;

import ConsoleFTPclient.services.BaseReadWriteWorker;

import java.util.Map;


public class ListOfStudent implements MenuItem {

    @Override
    public void executeMenuItem(BaseReadWriteWorker dataBase) {
        Map <Integer, String> mapForSorted = dataBase.getStudents();
        mapForSorted.entrySet().stream()
                    .sorted(Map.Entry.<Integer, String>comparingByValue())
                    .forEach(student -> System.out.println("name: " + student.getValue() +
                                                           " - ID: " + student.getKey()));
    }
}

