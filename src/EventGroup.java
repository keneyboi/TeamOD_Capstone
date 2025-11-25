import java.util.ArrayList;
import java.util.List;

public class EventGroup {
    private String name;
    private String pathName;
    private List<Event> listOfEvents;
    private int numOfEvents;

    public EventGroup(){
        //will remove this once EventGroup class is properly constructed in other classes
    }

    public EventGroup(String name, String pathName){
        this.name = name;
        this.pathName = pathName;
        this.listOfEvents = new ArrayList<Event>();
        this.numOfEvents = 0;
    }

    public EventGroup(String name, String pathName, List<Event> listOfEvents){
        this.name = name;
        this.pathName = pathName;
        this.listOfEvents = listOfEvents;
        this.numOfEvents = listOfEvents.size();
    }

    public void addEvent(Event e) {
        listOfEvents.add(e);
        numOfEvents++;
    }

    public void removeEvent(Event e) {
        listOfEvents.remove(e);
        numOfEvents--;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathName() {
        return pathName;
    }

    public List<Event> getListOfEvents() {
        return listOfEvents;
    }

    public int getNumOfEvents() {
        return numOfEvents;
    }

}
