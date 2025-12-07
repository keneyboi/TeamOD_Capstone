import java.util.*;

public class Event {
    private String name;
    private int numOfAttendees;
    private EventGroup eventGroup;
    private String lateTime;
    private List<Person> listOfAttendees;

    public Event(){
        //will remove this once Event class is properly constructed in other classes
    }

    public Event(String name, EventGroup eventGroup, String lateTime) {
        this.name = name;
        this.eventGroup = eventGroup;
        this.lateTime = lateTime;
        this.numOfAttendees = 0;
        this.listOfAttendees = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    public EventGroup getEventGroup() { return eventGroup; }
    public void setEventGroup(EventGroup eventGroup) { this.eventGroup = eventGroup; }


    public String getLateTime() { return lateTime; }
    public void setLateTime(String lateTime) { this.lateTime = lateTime; }


    public List<Person> getListOfAttendees() { return listOfAttendees; }
    public int getNumOfAttendees() { return numOfAttendees; }

    public void addAttendee(Person p) {
        listOfAttendees.add(p);
        numOfAttendees++;
    }

    public void removeAttendee(Person p) {
        listOfAttendees.remove(p);
        numOfAttendees--;
    }

    public String getPathName() {
        return eventGroup.getPathName() + "/" + name;
    }

    public void setListOfAttendees(List<Person> listOfAttendees){
        this.listOfAttendees = listOfAttendees;
    }

    public String toString() {
        //not sure if this was the intended use of toString
        //haven't included listOfAttendees and pathName
        String next = System.lineSeparator();
        String eventName = "Event: " + name;
        String eventGroupName = "Group: " + eventGroup;
        String lateTimeName = "Late Time: " + lateTime;
        String attendees = "Attendees: " + numOfAttendees;

        return eventName + next + eventGroupName + next + lateTimeName + next + attendees + next;
    }
}
