import java.util.ArrayList;
import java.util.Arrays; // Needed for array comparison
import java.util.List;

public class Account {
    private String name;
    private String email;
    private char[] password;
    private List<EventGroup> listOfEventGroups;

    public Account(String name, String email, char[] password) throws InvalidPasswordException {
        setName(name);
        setEmail(email);
        setPassword(password);
        listOfEventGroups = new ArrayList<>();
    }


    public boolean login(String inputEmail, String inputPassword) {
        if(inputEmail == null || inputPassword == null) return false;
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


    public void setPassword(char[] password) throws InvalidPasswordException{
        if (password == null) {
            throw new InvalidPasswordException("Password cannot be empty!");
        }

        if (password.length <= 8) {
            throw new InvalidPasswordException("Password must be above 8 characters.");
        }

        boolean hasSpecialOrNumber = false;
        for (char c : password) {
            if (!Character.isLetter(c)) {
                hasSpecialOrNumber = true;
                break;
            }
        }

        if (!hasSpecialOrNumber) {
            throw new InvalidPasswordException("Password must contain at least one number or special character.");
        }

        String temp = new String(password);
        temp.replaceAll("\u0000", "");
        this.password = temp.toCharArray();
    }

    public List<EventGroup> getListOfEventGroup() {
        return listOfEventGroups;
    }

    public String csvFormat(){
        return name + "," + email + "," + new String(password);
    }

    public String getPathname(){
        return "out/Account/" + name;
    }

    public String toString(){
        return name + "," + email + "," + new String(password);
    }

}