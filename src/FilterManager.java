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
            default:
                System.out.println("Invalid order");

        }
        return filteredList;
    }

    public List<Person> filterList(String[] orders, String filter){
        List<Person> result = new ArrayList<>();

        for(String key : orders){
            Iterator<Person> iterator = filteredList.iterator();
            while(iterator.hasNext()){
                Person p = iterator.next();
                Student s = (Student) p;
                String value = switch(filter.toLowerCase()){
                    case "course" -> s.getCourse();
                    case "year"  -> s.getYear();
                    case "section"  -> s.getSection();
                    default -> "";
                };

                if(value.equals(key)){
                    result.add(p);
                    iterator.remove();
                }
            }
        }
        result.addAll(filteredList);
        filteredList.clear();
        filteredList.addAll(result);
        return result;
    }

    public void filterCSV(String order){
        filterList(order);
        CSVManager.createCSVFile(filteredList, event.getPathName(), "Event");
    }

    public void filterCSV(String[] orders, String filter){
        filterList(orders, filter);
        CSVManager.createCSVFile(filteredList, event.getPathName(), "Event");

    }

    public String[] getPrintableList(){

        String[] result = new String[filteredList.size()];
        for (int i = 0; i < filteredList.size(); i++) {
            result[i] = filteredList.get(i).toString();
        }
        return result;
    }

}
