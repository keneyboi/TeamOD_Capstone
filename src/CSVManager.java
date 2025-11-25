import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.*;
import java.util.*;

public class CSVManager {
    private Event event;
    private List<Person> listOfAttendees;
    private String lateTime;
    private String pathName;

    public CSVManager(Event event){
        this.event = event;
        listOfAttendees = new ArrayList<>(event.getListOfAttendees());
        lateTime = event.getLateTime();
        pathName = event.getPathName();
    }

    //Not sure on what this method should specifically return
    public String createCSVFile(){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(pathName))){
            for(Person p : listOfAttendees){
                bw.write(p.toString());
                bw.newLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return pathName;
    }



}
