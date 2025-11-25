import java.util.*;
import java.io.*;

public class CSVManager {
    private Event event;
    private List<Person> listOfAttendees;
    private String lateTime;
    private String pathName;

    public CSVManager(Event e){
        event = e;
        listOfAttendees = new ArrayList<>(e.getListOfAttendees());
        lateTime = e.getLateTime();
        pathName = e.getPathName();
    }

    //returns csv file's path name
    public String createCSVFile(){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(pathName))){
            for(Person p : listOfAttendees) {
                bw.write(p.toString());
                bw.newLine();
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathName;
    }
}
