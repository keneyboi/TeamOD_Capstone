import java.util.ArrayList;
import java.util.List;

public class TestFile {
    public static List<?> makeList(){
        List<Account> persons = new ArrayList<>();
        return  persons;
    }

    public static void main(String[] args) {
        List<Person> persons = (List<Person>)makeList();
    }
}

