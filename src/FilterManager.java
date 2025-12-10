import java.util.*;
import java.io.*;

public class FilterManager {
    private Event event;
    private List<Person> filteredList;

    public FilterManager(Event event) {
        this.event = event;
        this.filteredList = new ArrayList<>(event.getListOfAttendees());
    }

    public List<Person> filterList(String order){
        switch(order.toLowerCase()){
            case "name":
                filteredList.sort(new Filters.FilterByName());
                break;
            case "time":
                filteredList.sort(new Filters.FilterByTime());
                break;
            case "year":
                filteredList.sort(new Filters.FilterByYear());
                break;
            case "section":
                filteredList.sort(new Filters.FilterBySection());
                break;
            case "course":
                filteredList.sort(new Filters.FilterByCourse());
                break;
            case "late":
                filteredList.removeIf(p -> p instanceof Student s && s.getStatus().equalsIgnoreCase("on time"));
                break;
            case "on time":
                filteredList.removeIf(p -> p instanceof Student s && s.getStatus().equalsIgnoreCase("late"));
                break;
            default:
                System.out.println("Invalid order");

        }
        return filteredList;
    }
}
