import java.util.ArrayList;
import java.util.Arrays; // Needed for array comparison
import java.util.List;

public class Account {
    private String name;
    private String email;
    private char[] password;
    private List<EventGroup> listOfEventGroups;


    public Account(String name, String email, String password, List<EventGroup> listOfEventGroups) {
        setName(name);
        setEmail(email);

        if (password != null) {
            setPassword(password.toCharArray());
        } else {
            throw new IllegalArgumentException("Password cannot be null");
        }

        if (listOfEventGroups != null) {
            this.listOfEventGroups = listOfEventGroups;
        } else {
            this.listOfEventGroups = new ArrayList<>();
        }
    }


    public boolean login(String inputEmail, String inputPassword) {
        if (!this.email.equals(inputEmail)) return false;
        return Arrays.equals(this.password, inputPassword.toCharArray());
    }

    public void addEventGroup(EventGroup e) {
        listOfEventGroups.add(e);
    }

    public void removeEventGroup(EventGroup e) {
        listOfEventGroups.remove(e);
    }

    //Getters & Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char[] getPassword() {
        return password;
    }


    public void setPassword(char[] password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be empty!");
        }

        if (password.length <= 8) {
            throw new IllegalArgumentException("Password must be above 8 characters.");
        }

        boolean hasSpecialOrNumber = false;
        for (char c : password) {
            if (!Character.isLetter(c)) {
                hasSpecialOrNumber = true;
                break;
            }
        }

        if (!hasSpecialOrNumber) {
            throw new IllegalArgumentException("Password must contain at least one number or special character.");
        }

        this.password = password;
    }

    public List<Event> getListOfEventGroup() {
        return listOfEventGroups;
    }

}