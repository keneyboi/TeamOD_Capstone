import java.util.*;

public class Event {
    private String name;
    private int numOfAttendees;
    private EventGroup eventGroup;
    private String lateTime;
    private List<Person> listOfAttendees;
    private String pathName;

    public Event(){

    }


    public String getName() {
        return name;
    }

    public int getNumOfAttendees() {
        return numOfAttendees;
    }

    public List<Person> getListOfAttendees() {
        return listOfAttendees;
    }

    public String getPathName() {
        return pathName;
    }
}
