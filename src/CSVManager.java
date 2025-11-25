import javax.swing.*;
import java.util.*;
import java.io.*;

public class CSVManager {
    private Event event;
    private List<Person> listOfAttendees;
    private String lateTime;
    private static String pathName;

    public CSVManager(Event e){
        event = e;
        listOfAttendees = new ArrayList<>(e.getListOfAttendees());
        lateTime = e.getLateTime();
        pathName = e.getPathName();
    }

    //returns csv file's path name
    public static void createCSVFile(List<Person> listOfAttendees, String pathName){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(pathName))){
            for(Person p : listOfAttendees) {
                bw.write(Encryption.encrypt(p.toString()));
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "CSV File not created!");
        }
    }
}
