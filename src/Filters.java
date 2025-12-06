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
            int res = p1.getTimeIn().compareTo(p2.getTimeIn());
            if(res == 0) return new FilterByYear().compare(p1, p2);
            return res;
        }

    }

    public static class FilterByYear implements Comparator<Person> {
        @Override
        public int compare(Person p1, Person p2) {
            int res = 0;
            if(p1 instanceof Student s1 && p2 instanceof Student s2){
                res = Integer.compare(Integer.parseInt(s1.getYear()), Integer.parseInt(s2.getYear()));
                if(res == 0) return new FilterByCourse().compare(s1, s2);
            }
            return res;
        }
    }

    public static class FilterBySection implements Comparator<Person> {
        @Override
        public int compare(Person p1, Person p2) {
            int res = 0;
            if(p1 instanceof Student s1 && p2 instanceof Student s2){
                res = s1.getSection().compareToIgnoreCase(s2.getSection());
                if(res == 0) return new FilterByName().compare(s1, s2);
            }
            return res;
        }
    }

    public static class FilterByCourse implements Comparator<Person> {
        @Override
        public int compare(Person p1, Person p2) {
            int res = 0;
            if(p1 instanceof Student s1 && p2 instanceof Student s2){
                res = s1.getCourse().compareToIgnoreCase(s2.getCourse());
                if(res == 0) return new FilterBySection().compare(s1, s2);
            }
            return res;
        }
    }
}
