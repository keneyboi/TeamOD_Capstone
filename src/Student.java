import java.time.Instant;

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

    public String toString(){return super.toString() + "," + section + "," + course + "," + year + "," + getTimeIn();}
    public String getPathName(){
        return "out/Student ID Library/" + getInfo() + " (" + getName() + ").png";
    }
}
