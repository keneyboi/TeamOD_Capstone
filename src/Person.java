import java.time.Instant;

public class Person {
    private final String name;
    private final String info;
    private Instant timeIn;

    public Person(String name, String info){
        this.name = name;
        this.info = info;
        this.timeIn = Instant.now();
    }

    public void setTimeIn(Instant timeIn) {
        this.timeIn = timeIn;
    }

    //Getters

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public Instant getTimeIn(){
        return timeIn;
    }

    public String toString(){ return name + "," + info;}
    public String getPathName(){return "out/Person Info Library/" + info + " (" + name + ").png";}
}
