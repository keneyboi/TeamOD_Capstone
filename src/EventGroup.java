import java.util.ArrayList;
import java.util.List;

public class EventGroup {
    private String name;
    private Account account;
    private List<Event> listOfEvents;
    private int numOfEvents;


    public EventGroup(String name, Account account){
        this.name = name;
        this.listOfEvents = new ArrayList<>();
        this.numOfEvents = 0;
        this.account = account;
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
        return account.getPathname() + "/" + name;
    }

    public List<Event> getListOfEvents() {
        return listOfEvents;
    }

}
