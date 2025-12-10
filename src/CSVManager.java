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
            if (label.equals("Event")) {
                bw.write("Name,ID,Section,Course,Year,Time In");
                bw.newLine();
            }
            for(int i = 0; i < list.size(); i++){
                if(label.equals("Account")){
                    bw.write(Encryption.encrypt(list.get(i).toString()));
                }else{
                    bw.write(list.get(i).toString());
                }
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "CSV File not created!");
        }
    }

    public static List<?> getFromCSV(String pathname) throws DefaultErrorException {
        List<Account> accounts = new ArrayList<>();
        List<Object> eventInfo = new ArrayList<>();
        String label = null;

        try (BufferedReader br = new BufferedReader(new FileReader(pathname))) {
            label = br.readLine();


            if (label != null && label.startsWith("Event")) {

                String lTimeLine = br.readLine();
                String lTime = (lTimeLine != null && lTimeLine.contains(":"))
                        ? lTimeLine.substring(lTimeLine.indexOf(":") + 1).trim()
                        : "N/A";

                String startLine = br.readLine();
                String startTime = (startLine != null && startLine.contains("attendance started:"))
                        ? startLine.substring(startLine.indexOf(":") + 1).trim()
                        : "N/A";

                String endLine = br.readLine();
                String endTime = (endLine != null && endLine.contains("attendance ended:"))
                        ? endLine.substring(endLine.indexOf(":") + 1).trim()
                        : "N/A";

                //for the studenst header
                br.readLine();

                eventInfo.add(lTime);
                eventInfo.add(startTime);
                eventInfo.add(endTime);


                label = "Event";
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                switch (label) {
                    case "Account":
                        line = Encryption.decrypt(line);
                        String[] tokens = line.split(",");
                        accounts.add(new Account(tokens[0], tokens[1], tokens[2].toCharArray()));
                        break;
                    case "Event":
                        String[] studentInfo = line.split(",");
                        if (studentInfo.length >= 6) {
                            String name = studentInfo[0];
                            String id = studentInfo[1];
                            String section = studentInfo[2];
                            String course = studentInfo[3];
                            String year = studentInfo[4];
                            String time = studentInfo[5];
                            Student s = new Student(name, id, section, course, year);
                            if (!time.equals("N/A")) {
                                s.setTimeIn(Student.parseTime(time));
                            }

                            if (studentInfo.length >= 7) {
                                s.setStatus(studentInfo[6].trim());
                            } else {
                                s.setStatus("On Time");
                            }

                            eventInfo.add(s);
                        }
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

        if (label != null && label.equals("Event")) {
            return eventInfo;
        } else {
            return accounts;
        }
    }
}
