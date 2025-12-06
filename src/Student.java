import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Student extends Person{
    private String section;
    private String course;
    private String year;
    public Student(String name, String ID, String section, String course, String year) {
        super(name,ID);
        this.section = section;
        this.course = course;
        this.year = year;

    }

    public String getID() { return getInfo(); }
    public String getSection() { return section; }
    public String getCourse() {return course;}
    public String getYear() {return year;}

    public static String formatInstant(String instantString) {
        if (instantString == null || instantString.equals("N/A")) return "N/A";
        try {
            java.time.Instant instant = java.time.Instant.parse(instantString);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd,yyyy hh:mm a").withZone(ZoneId.systemDefault());

            return formatter.format(instant);
        } catch (Exception e) {
            return instantString;
        }
    }

    public String toString(){return super.toString() + " | " + section + " | " + course + "-" + year + " | " + formatInstant(getTimeIn().toString());}
    public String getPathName(){
        return "out/Student ID Library/" + getInfo() + " (" + getName() + ").png";
    }
}
