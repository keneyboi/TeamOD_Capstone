import javax.swing.*;
import java.util.*;
import java.io.*;

public class CSVManager {
    private Event event;
    private List<Person> listOfAttendees;
    private String lateTime;
    private static String pathName;

    public CSVManager(Event e){
        event = e;
        listOfAttendees = new ArrayList<>(e.getListOfAttendees());
        lateTime = e.getLateTime();
        pathName = e.getPathName();
    }

    //returns csv file's path name
    public static void createCSVFile(List<?> list, String pathName, String label){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(pathName))){
            bw.write(label);
            bw.newLine();
            for(int i = 0; i < list.size(); i++){
                bw.write(list.get(i).toString());
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "CSV File not created!");
        }
    }

    public static List<?> getFromCSV(String pathname) throws DefaultErrorException {
        List<Account> accounts = new ArrayList<>();
        List<Person> persons = new ArrayList<>();
        String label = null;

        try(BufferedReader br = new BufferedReader(new FileReader(pathname))){
            label = br.readLine();
            String line;
            String[] tokens;

            while((line = br.readLine()) != null){
                switch(label){
                    case "Account":
                        if (line.trim().isEmpty()) {
                            continue;
                        }
                        tokens = line.split(",");
                        accounts.add(new Account(tokens[0], tokens[1], tokens[2].toCharArray()));
                        break;
                    case "Event":
                        if (line.trim().isEmpty()) {
                            continue;
                        }
                        tokens = line.split(",");
                        persons.add(new Student(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4]));
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid File");
                        throw new IOException();
                }
            }
        } catch (InvalidPasswordException e) {
            throw new DefaultErrorException(e.getMessage());
        } catch (FileNotFoundException e) {
            throw new DefaultErrorException("File not found");
        } catch (IOException e) {
            throw new DefaultErrorException("File access problem");
        }
        if(label == null) throw new DefaultErrorException("No labeling in CSV");
        if(label.equals("Account")){
            return accounts;
        } else {
            return persons;
        }

    }
}
