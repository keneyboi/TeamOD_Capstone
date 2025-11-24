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
        List<Person> copy = new ArrayList<>(filteredList);
        return singleFilter(copy, order);
    }

    public List<Person> filterList(String[] order, String filter){
        List<Person> copy = new ArrayList<>(filteredList);
        return multiFilter(copy, order, filter);
    }

    public void filterCSV(String order){
        List<Person> copy = new ArrayList<>(filteredList);
        updateCSVFile(singleFilter(copy, order));
    }

    public void filterCSV(CSVManager csv, String[] order, String filter){
        List<Person> copy = new ArrayList<>(filteredList);
        updateCSVFile(multiFilter(copy, order, filter));

    }

    public String[] getPrintableList(){

        String[] result = new String[filteredList.size()];
        for (int i = 0; i < filteredList.size(); i++) {
            result[i] = filteredList.get(i).toString();
        }
        return result;
    }

    private void updateCSVFile(List<Person> copy){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(event.getPathName()))){
            for(Person p : copy){
                bw.write(p.toString());
                bw.newLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private List<Person> singleFilter(List<Person> toFilter, String order) {
        switch(order.toLowerCase()){
            case "name":
                toFilter.sort(new Filters.FilterByName());
                break;
            case "time":
                toFilter.sort(new Filters.FilterByTime());
                break;
            case "year":
                toFilter.sort(new Filters.FilterByYear());
                break;
            default:
                System.out.println("Invalid order");

        }
        return toFilter;
    }

    private List<Person> multiFilter(List<Person> toFilter, String[] order, String filter){
        List<Person> result = new ArrayList<>();

        for(String key : order){
            Iterator<Person> iterator = toFilter.iterator();
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
        result.addAll(toFilter);
        return result;
    }






    



}
