import java.util.Comparator;

public class Filters {
    public static class FilterByName implements Comparator<Person> {
        @Override
        public int compare(Person p1, Person p2) {
            return p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
        }

    }

    public static class FilterByTime implements Comparator<Person> {
        @Override
        public int compare(Person p1, Person p2) {
            return p1.getTimeIn().compareTo(p2.getTimeIn());
        }
    }

    public static class FilterByYear implements Comparator<Person> {
        @Override
        public int compare(Person p1, Person p2) {
            if(p1 instanceof Student s1 && p2 instanceof Student s2){
                return Integer.compare(Integer.parseInt(s1.getYear()), Integer.parseInt(s2.getYear()));
            }
            return 0;
        }
    }

    public static class FilterBySection implements Comparator<Person> {
        @Override
        public int compare(Person p1, Person p2) {
            if(p1 instanceof Student s1 && p2 instanceof Student s2){
                return s1.getSection().compareToIgnoreCase(s2.getSection());
            }
            return 0;
        }
    }

    public static class FilterByCourse implements Comparator<Person> {
        @Override
        public int compare(Person p1, Person p2) {
            if(p1 instanceof Student s1 && p2 instanceof Student s2){
                return s1.getCourse().compareToIgnoreCase(s2.getCourse());
            }
            return 0;
        }
    }
}
