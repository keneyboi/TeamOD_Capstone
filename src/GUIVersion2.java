import com.github.eduramiba.webcamcapture.drivers.NativeDriver;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class GUIVersion2 extends JFrame implements ActionListener {
    private JPanel contentPanel;
    private JPanel OpeningScreen;
    private JPanel MainMenu;
    private JButton solveButton;
    private JButton SETTINGSButton;
    private JPanel InnerCardPanel;
    private JPanel DashBoard;
    private JButton DashboardButton;
    private JButton checkAttendanceBT;
    private JButton scanIDBT;
    private JButton createIDBT;
    private JButton addEventBT;
    private JButton IDBT;
    private JPanel createEventPane;
    private JPanel createIDPane;
    private JPanel eventGroupPane;
    private JPanel scanIDPane;
    private JPanel addEventPane;
    private JButton eventGroupBT;
    private JButton accountBT;
    private JLabel logoLabel;
    private JPanel accountPane;
    private JPanel checkAttendancePane;
    private JTextField usernameTF;
    private JPasswordField passwordPF;
    private JButton loginButton;
    private JButton createNewAccountButton;
    private JPanel CreateAccount;
    private JTextField createAccTF;
    private JPasswordField createPassPF;
    private JButton accountCreationBt;
    private JTextField createEmailTF;
    private JPasswordField createPassConfirmPW;
    private JLabel emailLane;
    private JTextField nameTF;
    private JButton GENERATEIDButton;
    private JLabel generateQRImageLabel;
    private JPanel containerQRPanel;
    private JTextField IDTF;
    private JTextField courseTF;
    private JTextField yearTF;
    private JTextField sectionTF;
    private JButton clearBT;
    private JTable scanDisplayTable;
    private JButton scanIDButton;
    private JScrollPane scanScrollPane;
    private JButton accountExistsBt;
    private JButton logOutButton;
    private JButton debugButtonButton;
    private JButton scanAttendanceButton;
    private JButton stopButton;
    private JTextField eventGroupTF;
    private JButton showAccountBTN;
    private JButton createEventButton;
    private JTextField eventNameTF;
    private JTextField lateTImeTF;
    private List<Account> listOfAccounts = new ArrayList<>();
    private DefaultTableModel dm = new DefaultTableModel();

    // added for event selection
    private JComboBox<String> eventSelectionCB;
    private Event selectedEvent;

    private Account currentAccount;


    CardLayout innerCardLayout = (CardLayout)InnerCardPanel.getLayout();
    CardLayout cardLayout = (CardLayout)contentPanel.getLayout();
    JButton[] IDButtons = new JButton[]{createIDBT, scanIDBT};
    JButton[] EventButtons = new JButton[]{addEventBT, checkAttendanceBT};
    Dimension small = new Dimension(300, 400);
    Dimension medium = new Dimension(700, 550);

    public GUIVersion2(){
        initializeDataSegments();
        setVisible(true);

        IDBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(getHeight() < 530 && !addEventBT.isVisible()) setSize(getWidth(), 530);
                for(JButton b : IDButtons){
                    if(!b.isVisible()) b.setVisible(true);
                    else b.setVisible(false);
                }

            }
        });

        DashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(DashBoard.isVisible()) DashBoard.setVisible(false);
                else DashBoard.setVisible(true);
            }
        });

        createNewAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "CreateAccount");
                setSize(small);
            }
        });

        accountCreationBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    createAccountCSV();
                } catch (DefaultErrorException error) {
                    JOptionPane.showMessageDialog(null, error.getMessage());
                } catch (InvalidPasswordException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    createPassPF.setText("");
                    createPassConfirmPW.setText("");
                }

            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Account a : listOfAccounts){
                    if((a.getEmail().equals(usernameTF.getText()) || a.getName().equals(usernameTF.getText())) && Arrays.compare(a.getPassword(), passwordPF.getPassword()) == 0){
                        currentAccount = a;
                        try {
                            getEventGroupFolder();
                            assignEventFileToEventGroupDirectory();
                            showAccountDetails();
                            addEventCB();
                        } catch (DefaultErrorException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                        }
                        JOptionPane.showMessageDialog(null, "Login Success");
                        cardLayout.show(contentPanel, "MainMenu");
                        setLocationRelativeTo(null);
                        setSize(medium);
                        return;
                    }
                }
                JOptionPane.showMessageDialog(null, "Invalid username / password");
            }
        });

        GENERATEIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Student s = new Student(nameTF.getText(), IDTF.getText(), sectionTF.getText(), courseTF.getText(), yearTF.getText());
                try {
                    ImageIcon img = new ImageIcon(generateQRCode(s));
                    generateQRImageLabel.setIcon(img);
                } catch (WriterException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        clearBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameTF.setText("");
                IDTF.setText("");
                courseTF.setText("");
                yearTF.setText("");
                sectionTF.setText("");
                generateQRImageLabel.setIcon(null);
            }
        });

        scanIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("pressed");
                clearTable();
                openScanner(result->{
                    setStudentFields(result);
                    playSound("assets/beep.wav");
                });
            }
        });

        accountExistsBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "LogIn");
            }
        });

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSize(small);
                cardLayout.show(contentPanel, "LogIn");
            }
        });

        debugButtonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSize(medium);
                cardLayout.show(contentPanel, "MainMenu");
            }
        });

        eventGroupBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(JButton b : EventButtons){
                    if(b.isVisible()){
                        b.setVisible(false);
                    } else {
                        b.setVisible(true);
                    }
                }
            }
        });

        showAccountBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAccountDetails();
            }
        });

        addEventBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                innerCardLayout.show(InnerCardPanel, "Add Event");
            }
        });

        // creates an event and adds it to its desired eventgroup as well as calls the create event to make an empty .csv file
        createEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean created = false;
                for(EventGroup eventGroup : currentAccount.getListOfEventGroup()){
                    if(eventGroup.getName().equals(eventGroupTF.getText())){
                        try {
                            eventGroup.addEvent(createEvent(eventNameTF.getText(), lateTImeTF.getText(), eventGroup));
                            getEventGroupFolder();
                            assignEventFileToEventGroupDirectory();
                            showAccountDetails();
                            created = true;
                        } catch (DefaultErrorException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                        }
                    }
                }
                if(!created){
                    try {
                        EventGroup eg = createEventGroup(eventGroupTF.getText());
                        currentAccount.addEventGroup(eg);
                        eg.addEvent(createEvent(eventNameTF.getText(), lateTImeTF.getText(), eg));
                        getEventGroupFolder();
                        assignEventFileToEventGroupDirectory();
                        showAccountDetails();
                    } catch (DefaultErrorException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }
                addEventCB();
            }
        });

        scanAttendanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedEvent == null) {
                    JOptionPane.showMessageDialog(null, "Please select an event first!");
                    return;
                }

                System.out.println("pressed");
                openScanner(result->{
                    try {
                        recordAttendance(selectedEvent, new Student(result[0], result[1], result[2], result[3], result[4]));
                    } catch (DefaultErrorException ex) {
                        JOptionPane.showMessageDialog(null, "Unable to record attendance");
                    }
                    setStudentFields(result);
                    playSound("assets/beep.wav");
                });
            }
        });

        eventSelectionCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) eventSelectionCB.getSelectedItem();
                if(selected != null) {
                    String[] parts = selected.split(" - ");
                    String eventGroupName = parts[0];
                    String eventName = parts[1];

                    for(EventGroup eg : currentAccount.getListOfEventGroup()) {
                        if(eg.getName().equals(eventGroupName)) {
                            for(Event event : eg.getListOfEvents()) {
                                if(event.getName().equals(eventName)) {
                                    selectedEvent = event;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    // accepts event and person, assigns person to event and adds them also to the csv
    public void recordAttendance(Event e, Person p) throws DefaultErrorException{

        // check if student aleary exist
        // joption will display that student aleary exist
        for(Person s : e.getListOfAttendees()) {
            if(s.getInfo().equals(p.getInfo())) {
                //JOptionPane.showMessageDialog(null, "You already scanned your attendance!");
                throw new DefaultErrorException("Student already exist!");
            }
        }

        // instead of printing, successfully recording an attendance
        // will be shown using joption
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(e.getPathName() + ".csv", true)))){
            pw.println(p.toString());
            System.out.println(p.toString());
            e.addAttendee(p);
            JOptionPane.showMessageDialog(null, "Attendance recorded successfully!");
        } catch (IOException ex) {
            throw new DefaultErrorException("Unable to access file");
        }
    }

    // this checks the event group directories under the account
    // it then assigns it to the list of event groups in the account
    public void getEventGroupFolder() throws DefaultErrorException {
        currentAccount.getListOfEventGroup().clear();
        File file = new File(currentAccount.getPathname());
        File[] listFiles = file.listFiles();
        if(listFiles == null) throw new DefaultErrorException("Empty Event Group");
        for(File f : listFiles){
            System.out.println("Added Event Group: " + f.getName());
            currentAccount.addEventGroup(new EventGroup(f.getName(), currentAccount));
        }
        for(EventGroup eG : currentAccount.getListOfEventGroup()){
            System.out.println(eG.getName());
        }
    }

    // It initializes the event groups with its events via file handling
    // it makes use of the getEventViaEventGroup function to get the event and assign it
    // to the actual event group.
    public void assignEventFileToEventGroupDirectory(){
        for(EventGroup e : currentAccount.getListOfEventGroup()){
            File eventG = new File(e.getPathName());
            File[] events = eventG.listFiles();
            for(File f : events){
                System.out.println(f.getName());
                if(!f.isDirectory()){
                    System.out.println(f.getName());
                    e.addEvent(getEventViaEventGroup(e, f.getName()));
                }
            }
        }
    }

    public void showAccountDetails(){
        System.out.println("Account Name: " + currentAccount.getName());
        for(EventGroup eG : currentAccount.getListOfEventGroup()){
            System.out.println("Event Group: " + eG.getName());
            for(Event e : eG.getListOfEvents()){
                System.out.println("----> Event: " + e.getName() + " Late Time: " + e.getLateTime());
                for(Person p : e.getListOfAttendees()){
                    System.out.println("---------> " + p.toString());
                }
            }
        }
    }

    // uses the CSVManager.getFromCsv to pull a list of people and assigns it to an event
    // note! the first person in the peoples has the name of the late time.
    // this event is then added to its parameterized Event Group and also initialized it properly
    public Event getEventViaEventGroup(EventGroup eG, String eventName){
        List<?> list = null;
        System.out.println(eG.getPathName() + "/" + eventName);
        try{
            list = CSVManager.getFromCSV(eG.getPathName() + "/" + eventName);
        } catch (DefaultErrorException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        String lateTime = (String) list.get(0);
        list.remove(0);

        List<Person> personList = new ArrayList<>();
        for(Object o : list){
            personList.add((Person) o);
        }

        Event event = new Event(eventName.replace(".csv", ""), eG, lateTime);
        event.setListOfAttendees(personList);
        return event;
    }

    // adds an event via name and automatically initializes it to the account
    // it also creates a Directory for it and throws an error if the event group already exists
    public EventGroup createEventGroup(String name) throws DefaultErrorException {
        File file = new File(currentAccount.getPathname() + "/" + name);
        if(file.exists()) throw new DefaultErrorException("Event Group already exists");
        file.mkdir();
        EventGroup eventGroup = new EventGroup(name, currentAccount);
        currentAccount.addEventGroup(eventGroup);
        for(EventGroup e : currentAccount.getListOfEventGroup()){
            System.out.println(e.getPathName());
        }
        return eventGroup;
    }

    // creates an empty event and adds it to the desired folder
    public Event createEvent (String name, String lateTime, EventGroup eg) throws DefaultErrorException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(eg.getPathName() + "/" + name + ".csv"))){
            bw.write("Event\n");
            bw.write(lateTime + "\n");
        } catch (IOException e) {
            throw new DefaultErrorException("File can't be open");
        }
        return new Event(name, eg, lateTime);
    }


    // everything that needs to be initialized before set visible is called should all be placed here
    public void initializeDataSegments(){
        try {
            listOfAccounts = (List<Account>)CSVManager.getFromCSV("out/Account/AccountList.csv");
            for(Account a : listOfAccounts){
                System.out.println(a);
            }
        } catch (DefaultErrorException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        updateAccountFolder();
        setTable();

        for(Account a : listOfAccounts){
            System.out.println(a);
        }

        setSize(small);
        ImageIcon logo = new ImageIcon("assets/test.png");
        Image newLogo = logo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(newLogo));
        setTitle("Attendo");
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(250, 380));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        add(contentPanel);

        //Disable Space
        usernameTF.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(' '), "none");
        passwordPF.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(' '), "none");
        createAccTF.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(' '), "none");
        createPassPF.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(' '), "none");
        createEmailTF.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(' '), "none");
        createPassConfirmPW.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(' '), "none");

        // adding of action listeners
        accountBT.addActionListener(this);
        eventGroupBT.addActionListener(this);
        for(JButton b : IDButtons){
            b.addActionListener(this);
        }
        for(JButton b : EventButtons){
            b.addActionListener(this);
        }
    }

    private void addEventCB() {
        eventSelectionCB.removeAllItems();
        for(EventGroup eg : currentAccount.getListOfEventGroup()) {
            for(Event e : eg.getListOfEvents()) {
                eventSelectionCB.addItem(eg.getName() + " - " + e.getName());
            }
        }
    }

    public void clearTable(){
        dm.setValueAt("", 0, 1);
        dm.setValueAt("", 1, 1);
        dm.setValueAt("", 2, 1);
        dm.setValueAt("", 3, 1);
        dm.setValueAt("", 4, 1);
        scanScrollPane.repaint();
        scanScrollPane.revalidate();
    }

    public void setStudentFields(String[] info){
        dm.setValueAt(info[0], 0, 1);
        dm.setValueAt(info[1], 1, 1);
        dm.setValueAt(info[4], 2, 1);
        dm.setValueAt(info[3], 3, 1);
        dm.setValueAt(info[2], 4, 1);
        scanScrollPane.repaint();
        scanScrollPane.revalidate();
    }

    public void setTable(){
        scanDisplayTable.setModel(dm);
        dm.addColumn("Label");
        dm.addColumn("Value");

        dm.addRow(new Object[]{"Name: ", ""});
        dm.addRow(new Object[]{"ID: ", ""});
        dm.addRow(new Object[]{"Course: ", ""});
        dm.addRow(new Object[]{"Year: ", ""});
        dm.addRow(new Object[]{"Section: ", ""});
        scanScrollPane.repaint();
        scanScrollPane.revalidate();
    }

    public void openScanner(Consumer<String[]> callback){
        new Thread(()->{
            Webcam webcam = null;
            JFrame frame = new JFrame("test");
            try {
                System.out.println("in");
                Webcam.setDriver(new NativeDriver());

                webcam = Webcam.getDefault();
                if (webcam == null) {
                    System.out.println("No webcam detected!");
                    return;
                }
                webcam.setViewSize(WebcamResolution.VGA.getSize());

                webcam.open();

                WebcamPanel wp = new WebcamPanel(webcam);
                wp.setMirrored(true);
                wp.setFPSDisplayed(true);
                frame.setSize(webcam.getViewSize());
                frame.setLocation(this.getX() + 500, this.getY());
                frame.add(wp);
                frame.setVisible(true);

                while (true){
                    try{
                        frame.repaint();
                        BufferedImage image = webcam.getImage();
                        if(image == null) continue;
                        if(!frame.isVisible()){
                            webcam.close();
                            return;
                        }
                        LuminanceSource source = new BufferedImageLuminanceSource(image);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                        Result result = new MultiFormatReader().decode(bitmap);

                        String res = Encryption.decrypt(result.getText());
                        callback.accept(res.split(","));
                        webcam.close();
                        frame.dispose();

                        break;
                    } catch (NotFoundException e) {
                        System.out.println("not found");
                        Thread.sleep(0);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid ID");
                frame.dispose();
                webcam.close();
            }
        }).start();
    }

    public String generateQRCode(Student student) throws WriterException, IOException {
        String data = Encryption.encrypt(student.toString());
        BitMatrix bitMatrix = new MultiFormatWriter().encode(new String(data.getBytes(StandardCharsets.UTF_8)), BarcodeFormat.QR_CODE, 200, 200);
        Path path = Paths.get(student.getPathName());
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        return student.getPathName();
    }

    public void createAccountCSV() throws DefaultErrorException, InvalidPasswordException {
        if(createAccTF.getText().isEmpty()) throw new DefaultErrorException("Enter Valid Username!");
        if(!createEmailTF.getText().endsWith("@gmail.com")) throw new DefaultErrorException("Enter Valid Email!");
        if(Arrays.compare(createPassPF.getPassword(), createPassConfirmPW.getPassword()) != 0) throw new DefaultErrorException("Passwords don't match");

        //check for duplicate username
        for(Account a : listOfAccounts) {
            if(a.getName().equals(createAccTF.getText())) {
                throw new DefaultErrorException("Username already exists!");
            }
            if(a.getEmail().equals(createEmailTF.getText())) {
                throw new DefaultErrorException("Email already registered!");
            }
        }

        listOfAccounts.add(new Account(createAccTF.getText(), createEmailTF.getText(), createPassPF.getPassword()));
        createAccTF.setText("");
        createEmailTF.setText("");
        createPassPF.setText("");
        createPassConfirmPW.setText("");
        JOptionPane.showMessageDialog(null, "Account successfully created! Please log in.");
        setSize(small);
        cardLayout.show(contentPanel, "LogIn");

        CSVManager.createCSVFile(listOfAccounts, "out/Account/AccountList.csv", "Account");

        try {
            listOfAccounts = (List<Account>) CSVManager.getFromCSV("out/Account/AccountList.csv");
        } catch (DefaultErrorException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        for(Account a : listOfAccounts){
            File file = new File("out/Account/" + a.getName());
            if (file != null && !file.exists()) {
                file.mkdirs();
            }
        }
    }

    public void updateAccountFolder(){
        File accountsRoot = new File("out/Account");

        if (!accountsRoot.exists()) {
            accountsRoot.mkdirs();
        }

        ArrayList<String> validAccount = new ArrayList<>();
        for (Account a : listOfAccounts) {
            validAccount.add(a.getName());
            File file = new File("out/Account/" + a.getName());
            if (file != null && !file.exists()) {
                file.mkdirs();
            }
        }

        File[] existingFiles = accountsRoot.listFiles();

        if (existingFiles != null) {
            for (File file : existingFiles) {
                if (file.isDirectory()) {
                    String folderName = file.getName();

                    if (!validAccount.contains(folderName)) {
                        System.out.println("Deleting account folder: " + folderName);
                        deleteFile(file);
                    }
                }
            }
        }


        for(Account a : listOfAccounts){
            File file = new File("out/Account/" + a.getName());
            if (file != null && !file.exists()) {
                file.mkdirs();
            }
        }
    }

    private void deleteFile(File toDel) {
        if (toDel.isDirectory()) {
            File[] entries = toDel.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    deleteFile(entry);
                }
            }
        }
        // Delete the file or empty directory
        if (!toDel.delete()) {
            System.err.println("Failed to delete: " + toDel.getAbsolutePath());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for(JButton b : IDButtons){
            if(((JButton)(e.getSource())).equals(b)){
                innerCardLayout.show(InnerCardPanel, b.getActionCommand());
            }
        }
        if(e.getSource() == accountBT) innerCardLayout.show(InnerCardPanel, "Account");
        if(e.getSource() == eventGroupBT) innerCardLayout.show(InnerCardPanel, "History");
    }

    public static void playSound(String pathname) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(pathname));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException var3) {
            System.out.println("Unable to play sound");
        }

    }

    public static void main(String[] args) {
        try {
            Account test = new Account("Ken", "ken@gmail.com", new char[]{'1', '1', '1','1', '1', '1', 's', '1', '-'});
            EventGroup eventGroup = new EventGroup("testingEventGroup", test);
            Event event = new Event("testEvent", eventGroup, "9:20");
            test.addEventGroup(eventGroup);
            eventGroup.addEvent(event);
            System.out.println(event.getPathName());

        } catch (InvalidPasswordException e) {
            System.out.println("error");
        }

        new GUIVersion2();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
