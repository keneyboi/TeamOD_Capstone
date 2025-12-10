import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Student extends Person{
    private String section;
    private String course;
    private String year;
    private String status = "On Time";

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

    public static String formatInstant(Instant instantString) {
        if (instantString == null || instantString.equals("N/A")) return "N/A";
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy hh:mm a").withZone(ZoneId.systemDefault());

            return formatter.format(instantString);
        } catch (Exception e) {
            return "N/A";
        }
    }

    public static java.time.Instant parseTime(String timeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy hh:mm a")
                    .withZone(ZoneId.systemDefault());
            return ZonedDateTime.parse(timeStr, formatter).toInstant();
        } catch (Exception e) {
            return java.time.Instant.now();
        }
    }

    public void calculateStatus(String eventLateTime) {
        if (eventLateTime == null || eventLateTime.equals("N/A") || eventLateTime.trim().isEmpty()) {
            this.status = "On Time";
            return;
        }

        try {
            String timeStr = eventLateTime;
            if (timeStr.toLowerCase().contains("late time")) {
                if (timeStr.contains(":")) {
                    timeStr = timeStr.replaceAll("(?i)Late Time:?", "").trim();
                }
            }

            timeStr = timeStr.trim();
            LocalTime lateLimit;
            try {
                DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
                lateLimit = LocalTime.parse(timeStr, dtf1);
            } catch (Exception e1) {
                try {
                    lateLimit = LocalTime.parse(timeStr);
                } catch (Exception e2) {
                    System.out.println("Could not parse late time: " + timeStr);
                    this.status = "On Time";
                    return;
                }
            }

            LocalTime arrivalTime = LocalTime.ofInstant(getTimeIn(), ZoneId.systemDefault());
            if (!arrivalTime.isBefore(lateLimit)) {
                this.status = "Late";
            } else {
                this.status = "On Time";
            }

        } catch (Exception e) {
            System.out.println("Error calculating status: " + e.getMessage());
            this.status = "On Time";
        }
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }

    public String toString(){return super.toString() + "," + section + "," + course + "," + year + "," + formatInstant(getTimeIn()) + "," + status;}
    public String getPathName(){
        return "out/Student ID Library/" + getInfo() + " (" + getName() + ").png";
    }
}
