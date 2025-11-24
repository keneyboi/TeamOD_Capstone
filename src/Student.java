public class Student {
    private final String name;
    private final String ID;
    private String section;
    private String courseYear;
    public Student(String name, String ID, String section, String course, String year) {
        this.name = name;
        this.ID = ID;
        this.section = section;
        courseYear = course + year;
    }

    public String toString(){
        return name + "," + ID + "," + section + "," + courseYear;
    }
    public String getPathName(){
        return "out/Student ID Library/" + ID + " (" + name + ").png";
    }
}
