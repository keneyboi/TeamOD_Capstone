import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

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
    private JButton mainScreenBT;
    private JPanel mainScreenPane;
    private JPanel createIDPane;
    private JPanel eventGroupPane;
    private JPanel scanIDPane;
    private JPanel addEventPane;
    private JButton eventGroupBT;
    private JButton accountBT;
    private JLabel logoLabel;
    private JPanel accountPane;
    private JPanel eventDetailsPane;
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
    private JTextField eventGroupTF;
    private JButton showAccountBTN;
    private JButton createEventButton;
    private JTextField eventNameTF;
    private JTextField lateTImeTF;
    private List<Account> listOfAccounts = new ArrayList<>();
    private DefaultTableModel dm = new DefaultTableModel();
    private ScannerCamera camera;

    // added for event selection
    private JComboBox mainEventCB;
    private JButton scanLogoHereButton;
    private JButton addNewStudentBTN;
    private JButton newEventBTN;
    private JButton eventDetailsBTN;
    private JButton accountBTN;
    private JComboBox mainGroupCB;
    private JPanel TopBorderPanel;
    private JLabel usernameLabel;
    private JComboBox eventGroupCB;

    private JButton backButton;
    private JButton backButton2;
    private JButton backButton1;
    private JButton seeAttendanceButton;
    private JButton backButton3;
    private JButton LogOut;
    private JTable AccountDtTable;
    private JScrollPane AccountDtPane;
    private JLabel accountLbl;

    // event details pane
    private JScrollPane eventDetailsScrollPane;
    private JComboBox eventDetailsCB;
    private JPanel listContainerPanel;
    private Event currentSelectedEvent = null;
    private JPanel currentSelectedRow = null;

    // selected data segments
    private Account currentAccount;
    private static Event eventSelected;
    private EventGroup eventGroupSelected;
    private static List<Student> resultListStudent;

    // for filtering
    private JComboBox filterCB;
    private boolean isViewingStudents = false;

    private boolean hasLogin = false;

    CardLayout innerCardLayout = (CardLayout)InnerCardPanel.getLayout();
    CardLayout cardLayout = (CardLayout)contentPanel.getLayout();
    JButton[] IDButtons = new JButton[]{createIDBT, scanIDBT};
    JButton[] EventButtons = new JButton[]{addEventBT, checkAttendanceBT};
    Dimension small = new Dimension(300, 400);
    Dimension medium = new Dimension(700, 550);


    public GUIVersion2() throws DefaultErrorException {
        initializeDataSegments();
        setVisible(true);

        mainScreenBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                innerCardLayout.show(InnerCardPanel, e.getActionCommand());
                if(getHeight() < 530 && !addEventBT.isVisible()) setSize(getWidth(), 530);
                for(JButton b : IDButtons){
                    if(!b.isVisible()) b.setVisible(true);
                    else b.setVisible(false);
                }

            }
        });

        filterCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) filterCB.getSelectedItem();
                applyFilter(selected);
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
                            if (hasLogin == false){
                                initializeFiles();
                                hasLogin = true;
                            }
                        } catch (DefaultErrorException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                        }
                        usernameTF.setText("");
                        passwordPF.setText("");
                        JOptionPane.showMessageDialog(null, "Login Success");
                        cardLayout.show(contentPanel, "MainMenu");
                        setSize(medium);
                        setLocationRelativeTo(null);
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

            }
        });

        accountExistsBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

        eventGroupCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) eventGroupCB.getSelectedItem();
                if (selected == null) return;
                if (selected.equals("Create Event Group")) {
                    eventGroupTF.setVisible(true);
                    eventGroupTF.setText("");
                } else {
                    eventGroupTF.setVisible(false);
                    eventGroupTF.setText(selected);
                }

                if (addEventPane != null) {
                    addEventPane.revalidate();
                    addEventPane.repaint();
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

                String selectedOption = (String) eventGroupCB.getSelectedItem();
                String targetGroupName = "";

                if (selectedOption != null && selectedOption.equals("Create Event Group")) {
                    if (eventGroupTF.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter a name for the new Event Group!");
                        return;
                    }
                    targetGroupName = eventGroupTF.getText().trim();
                } else {
                    targetGroupName = selectedOption;
                }

                if (eventNameTF.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter an Event Name!");
                    return;
                }

                String targetEventName = eventNameTF.getText().trim();

                for (EventGroup eg : currentAccount.getListOfEventGroup()) {
                    if (eg.getName().equalsIgnoreCase(targetGroupName)) {
                        for (Event ev : eg.getListOfEvents()) {
                            if (ev.getName().equalsIgnoreCase(targetEventName)) {
                                JOptionPane.showMessageDialog(null, "Event already exists!");
                                return;
                            }
                        }
                    }
                }

                boolean created = false;

                for (EventGroup eventGroup : currentAccount.getListOfEventGroup()) {
                    if (eventGroup.getName().equals(targetGroupName)) {
                        try {
                            eventGroup.addEvent(createEvent(targetEventName, lateTImeTF.getText(), eventGroup));
                            created = true;
                            break;
                        } catch (DefaultErrorException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                            return;
                        }
                    }
                }

                if (!created) {
                    try {
                        EventGroup eg = createEventGroup(targetGroupName);
                        eg.addEvent(createEvent(targetEventName, lateTImeTF.getText(), eg));
                        created = true;
                    } catch (DefaultErrorException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }

                if (created) {
                    try {
                        getEventGroupFolder();
                        assignEventFileToEventGroupDirectory();
                        showAccountDetails();

                        // Update UI
                        updateDataSegments();
                        JOptionPane.showMessageDialog(null, "Successfully Added an Event");
                        eventNameTF.setText("");
                        lateTImeTF.setText("");

                    } catch (DefaultErrorException ex) {
                        JOptionPane.showMessageDialog(null, "Error refreshing data: " + ex.getMessage());
                    }
                }
            }
        });


        mainGroupCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String)mainGroupCB.getSelectedItem();

                if (selected == null || selected.equals("Choose Event Group") || selected.equals("<None>")) {
                    return;
                }

                eventSelected = null;


                mainEventCB.removeAllItems();
                mainEventCB.addItem("Choose Event");

                for(EventGroup eV : currentAccount.getListOfEventGroup()){
                    if(eV.getName().equals(selected)){
                        eventGroupSelected = eV;
                        for(Event aV : eV.getListOfEvents()){
                            mainEventCB.addItem(aV.getName());
                        }
                        return;
                    }
                }
            }
        });

        mainEventCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String)mainEventCB.getSelectedItem();


                if (selected == null || selected.equals("Choose Event")) {
                    return;
                }

                if(eventGroupSelected != null){
                    for(Event eA : eventGroupSelected.getListOfEvents()){
                        if(eA.getName().equals(selected)){
                            eventSelected = eA;
                            System.out.println("Main Menu Selected: " + eventSelected.getName());
                            return;
                        }
                    }
                }
            }
        });

        scanLogoHereButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Student> result = null;
                Student startAttendance;
                if(eventSelected != null && eventGroupSelected != null){
                    System.out.println("Selected: " + eventGroupSelected.getName() + " -> " + eventSelected.getName());
                    camera = new ScannerCamera();
                    camera.open();
                }
            }
        });
        addNewStudentBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                innerCardLayout.show(InnerCardPanel, "Create ID");
            }
        });
        eventDetailsBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("pressed");
                innerCardLayout.show(InnerCardPanel, "Event Details");
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                innerCardLayout.show(InnerCardPanel, "Main Screen");

                listContainerPanel.removeAll();
                currentSelectedEvent = null;
                currentSelectedRow = null;

                if (eventDetailsCB.getItemCount() > 0) {
                    eventDetailsCB.setSelectedIndex(0);
                }

                updateFilterOptions(false);
                refreshListUI();
            }

        });
        backButton2.addComponentListener(new ComponentAdapter() {
        });
        backButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                innerCardLayout.show(InnerCardPanel, "Main Screen");
            }
        });
        newEventBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                innerCardLayout.show(InnerCardPanel, "Add Event");
            }
        });
        backButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                innerCardLayout.show(InnerCardPanel, "Main Screen");
            }
        });
        backButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                innerCardLayout.show(InnerCardPanel, "Main Screen");
            }
        });
        accountBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAccountTable();
                innerCardLayout.show(InnerCardPanel, "Account");
            }
        });

        LogOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "LogIn");
                innerCardLayout.show(InnerCardPanel, "Main Screen");
                hasLogin = false;
                setSize(small);
                setLocationRelativeTo(null);
            }
        });

        eventDetailsCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedGroupName = (String) eventDetailsCB.getSelectedItem();
                if (selectedGroupName == null || selectedGroupName.equals("Choose Event Group")) return;

                updateFilterOptions(false);

                for(EventGroup eg : currentAccount.getListOfEventGroup()){
                    if(eg.getName().equals(selectedGroupName)){
                        loadEventsList(eg.getListOfEvents());
                        return;
                    }
                }
            }
        });

        seeAttendanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentSelectedEvent == null) {
                    JOptionPane.showMessageDialog(null, "Please select an Event first!");
                    return;
                }

                updateFilterOptions(true);
                loadStudentList(currentSelectedEvent.getListOfAttendees());
            }
        });

    }

    // this function is called after log in, when the current user is initialized.
    // it aims to initialize or update all of the UI when something is added.
    public void updateDataSegments() {
        if (mainGroupCB == null || mainEventCB == null || eventGroupCB == null) return;

        mainGroupCB.removeAllItems();
        mainEventCB.removeAllItems();
        eventGroupCB.removeAllItems();
        eventDetailsCB.removeAllItems();

        eventGroupCB.addItem("Create Event Group");

        if (currentAccount != null && currentAccount.getListOfEventGroup() != null && !currentAccount.getListOfEventGroup().isEmpty()) {

            mainGroupCB.addItem("Choose Event Group");

            for (EventGroup e : currentAccount.getListOfEventGroup()) {
                mainGroupCB.addItem(e.getName());
                eventGroupCB.addItem(e.getName());
            }
        } else {
            mainGroupCB.addItem("<None>");
        }

        mainEventCB.addItem("Choose Event");

        if (usernameLabel != null && currentAccount != null) {
            usernameLabel.setText("Welcome " + currentAccount.getName() + "!");
            accountLbl.setText("Hello " + currentAccount.getName() + "!");
        }

        eventDetailsCB.addItem("Choose Event Group");

        if (currentAccount != null && currentAccount.getListOfEventGroup() != null && !currentAccount.getListOfEventGroup().isEmpty()) {
            for (EventGroup e : currentAccount.getListOfEventGroup()) {
                eventDetailsCB.addItem(e.getName());
            }
        }
        setAccountTable();
    }

    // accepts event and person, assigns person to event and adds them also to the csv
    public static void recordAttendance(Event e, Person p) throws DefaultErrorException {
        // check if student aleary exist
        for (Person s : e.getListOfAttendees()) {
            if (s.getInfo().equals(p.getInfo())) {
                throw new DefaultErrorException("Student already exist!");
            }
        }

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(e.getPathName() + ".csv", true)))) {
            pw.println(p.toString());
            e.addAttendee(p);
            System.out.println("Adding: " + p.getName() + " -> " + e.getName());
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
    public Event getEventViaEventGroup(EventGroup eG, String eventName) {
        List<?> list = null;
        System.out.println(eG.getPathName() + "/" + eventName);
        try {
            list = CSVManager.getFromCSV(eG.getPathName() + "/" + eventName);
        } catch (DefaultErrorException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return null;
        }

        String lateTime = (String) list.get(0);
        String startTime = (String) list.get(1);
        String endTime = (String) list.get(2);
        list.remove(0);
        list.remove(0);
        list.remove(0);

        List<Person> personList = new ArrayList<>();
        for (Object o : list) {
            personList.add((Person) o);
        }

        Event event = new Event(eventName.replace(".csv", ""), eG, lateTime);
        event.setListOfAttendees(personList);
        return event;
    }

    // adds an event via name and automatically initializes it to the account
    // it also creates a Directory for it and throws an error if the event group already exists
    public EventGroup createEventGroup(String name) throws DefaultErrorException {
        if (name.isEmpty() || (name.charAt(0) == ' ') || eventNameTF.getText().isEmpty()) throw new DefaultErrorException("Fields are empty!");
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
    public Event createEvent(String name, String lateTime, EventGroup eg) throws DefaultErrorException {
        String lTime = lateTime.trim();
        if (lTime.isEmpty()) lTime = "N/A";
        else lTime = "Late Time: " + lTime;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(eg.getPathName() + "/" + name + ".csv"))) {
            bw.write("Event: " + name);
            bw.newLine();
            bw.write(lTime);
            bw.newLine();
            bw.write("Attendance started: N/A");
            bw.newLine();
            bw.write("Attendance ended: N/A");
            bw.newLine();
            bw.write("Students:");
            bw.newLine();
        } catch (IOException e) {
            throw new DefaultErrorException("File can't be open");
        }
        return new Event(name, eg, lTime);
    }


    // everything that needs to be initialized before set visible is called should all be placed here
    public void initializeDataSegments() {
        try {
            listOfAccounts = (List<Account>) CSVManager.getFromCSV("out/Account/AccountList.csv");
            for (Account a : listOfAccounts) {
                System.out.println(a);
            }
        } catch (DefaultErrorException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        updateAccountFolder();
        setTable();
        setAccountTable();
        initScrollContainer();

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
        for (JButton b : IDButtons) {
            b.addActionListener(this);
        }
        for (JButton b : EventButtons) {
            b.addActionListener(this);
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
        dm.setValueAt(info[3], 2, 1);
        dm.setValueAt(info[4], 3, 1);
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

    public void setAccountTable() {
        if (AccountDtTable == null) return;
        int totalEventGroups = 0;
        int totalEvents = 0;

        if (currentAccount != null && currentAccount.getListOfEventGroup() != null) {
            totalEventGroups = currentAccount.getListOfEventGroup().size();
            for (EventGroup eg : currentAccount.getListOfEventGroup()) {
                if (eg.getListOfEvents() != null) {
                    totalEvents += eg.getListOfEvents().size();
                }
            }
        }

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Category");
        model.addColumn("Count");
        model.addRow(new Object[]{"Event Groups", totalEventGroups});
        model.addRow(new Object[]{"Total Events", totalEvents});

        AccountDtTable.setModel(model);
        AccountDtTable.setPreferredScrollableViewportSize(new Dimension(320, 100));
        AccountDtTable.setFillsViewportHeight(true);
        AccountDtTable.setRowHeight(25);
    }


    /* public void openScanner(Consumer<String[]> callback){
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

     */

    public String generateQRCode(Student student) throws WriterException, IOException {
        String data = Encryption.encrypt(student.toString());
        BitMatrix bitMatrix = new MultiFormatWriter().encode(new String(data.getBytes(StandardCharsets.UTF_8)), BarcodeFormat.QR_CODE, 200, 200);
        Path path = Paths.get(student.getPathName());
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        return student.getPathName();
    }

    public void createAccountCSV() throws DefaultErrorException, InvalidPasswordException {
        if (createAccTF.getText().trim().isEmpty()) {
            throw new DefaultErrorException("Enter Valid Username!");
        }

        String inputEmail = createEmailTF.getText().trim();
        String emailLower = inputEmail.toLowerCase();

        if (!emailLower.endsWith("@gmail.com") && !emailLower.endsWith("@yahoo.com") && !emailLower.endsWith("@cit.edu")) {
            throw new DefaultErrorException("Enter a valid email domain!");
        }

        if (Arrays.compare(createPassPF.getPassword(), createPassConfirmPW.getPassword()) != 0) {
            throw new DefaultErrorException("Passwords don't match");
        }

        for (Account a : listOfAccounts) {
            if (a.getName().equalsIgnoreCase(createAccTF.getText().trim())) {
                throw new DefaultErrorException("Username already exists!");
            }
            if (a.getEmail().equalsIgnoreCase(inputEmail)) {
                throw new DefaultErrorException("Email already registered!");
            }
        }

        listOfAccounts.add(new Account(createAccTF.getText().trim(), inputEmail, createPassPF.getPassword()));

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

        for (Account a : listOfAccounts) {
            File file = new File("out/Account/" + a.getName());
            if (!file.exists()) {
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

    public static void recordAttendanceAdapter(List<Student> s){
        for(Student s2 : s){
            try {
                recordAttendance(eventSelected, s2);
            } catch (DefaultErrorException e) {
                System.out.println("Student Already Logged Attendance");
            }
        }
    }

    public void initializeFiles() throws DefaultErrorException {
        eventSelected = null;
        eventGroupSelected = null;
        getEventGroupFolder();
        assignEventFileToEventGroupDirectory();
        showAccountDetails();
        updateDataSegments();
    }

    public static void main(String[] args) throws DefaultErrorException {
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

    // methods for the event details pane
    private void initScrollContainer() {
        listContainerPanel = new JPanel();
        // BoxLayout.Y_AXIS makes items stack vertically
        listContainerPanel.setLayout(new BoxLayout(listContainerPanel, BoxLayout.Y_AXIS));

        // Bind the container to the scroll pane
        if (eventDetailsScrollPane != null) {
            eventDetailsScrollPane.setViewportView(listContainerPanel);
        } else {
            System.out.println("eventDetailsScrollPane is not initialized.");
        }
    }

    // for event(jpanel) lists
    private void loadEventsList(List<Event> events) {
        listContainerPanel.removeAll();
        currentSelectedEvent = null;
        currentSelectedRow = null;

        if (events == null || events.isEmpty()) {
            listContainerPanel.add(new JLabel("No events found in this group."));
        } else {
            for (Event e : events) {
                ListRowItem item = new ListRowItem(e.getName(), "Late Time: " + e.getLateTime());

                // highlight the jpanel when clicked
                MouseAdapter select = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent me) {
                        selectRow(item, e);
                    }
                };

                item.addMouseListener(select);
                // Add listener to the components inside so clicking them also selects the row
                for(Component c : item.getComponents()) {
                    c.addMouseListener(select);
                }

                listContainerPanel.add(item);
                // Add a tiny invisible gap between rows
                listContainerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        refreshListUI();
    }

    // for student(jpanel) list
    private void loadStudentList(List<Person> attendees) {
        listContainerPanel.removeAll();

        if (attendees == null || attendees.isEmpty()) {
            listContainerPanel.add(new JLabel("No attendance records found."));
        } else {
            for (Person p : attendees) {
                String subText = p.getInfo();
                if(p instanceof Student s) {
                    subText = s.toString();
                }

                ListRowItem row = new ListRowItem(p.getName(), subText);
                listContainerPanel.add(row);
                listContainerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        refreshListUI();
    }

    // highlight logic
    private void selectRow(JPanel row, Event e) {
        if (currentSelectedRow != null) {
            currentSelectedRow.setBackground(UIManager.getColor("Panel.background"));
            currentSelectedRow.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }

        currentSelectedRow = row;
        currentSelectedRow.setBackground(new Color(173, 216, 230)); // Light Blue
        currentSelectedRow.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));

        currentSelectedEvent = e;
        System.out.println("Selected: " + e.getName());
    }

    private void refreshListUI() {
        listContainerPanel.revalidate();
        listContainerPanel.repaint();
    }

    // for filtering
    private void updateFilterOptions(boolean forStudents) {
        ActionListener[] listeners = filterCB.getActionListeners();
        for (ActionListener l : listeners) filterCB.removeActionListener(l);

        filterCB.removeAllItems();
        filterCB.addItem("Sort By...");

        if (forStudents) {
            filterCB.addItem("Name (A-Z)");
            filterCB.addItem("Name (Z-A)");
            filterCB.addItem("Time (Oldest)");
            filterCB.addItem("Time (Newest)");
            filterCB.addItem("Section");
            filterCB.addItem("Course");
            filterCB.addItem("Year");
        } else {
            filterCB.addItem("Name (A-Z)");
            filterCB.addItem("Name (Z-A)");
            filterCB.addItem("Late Time");
        }

        for (ActionListener l : listeners) filterCB.addActionListener(l);
        isViewingStudents = forStudents;
    }

    private void applyFilter(String selected) {
        if (selected == null || selected.equals("Sort By...")) return;

        if (isViewingStudents) {
            if (currentSelectedEvent == null) return;

            FilterManager fm = new FilterManager(currentSelectedEvent);
            List<Person> sortedList = null;

            if (selected.contains("Name")) {
                sortedList = fm.filterList("name");
            } else if (selected.contains("Time")) {
                sortedList = fm.filterList("time");
            } else if (selected.equals("Section")) {
                sortedList = fm.filterList("section");
            } else if (selected.equals("Course")) {
                sortedList = fm.filterList("course");
            } else if (selected.equals("Year")) {
                sortedList = fm.filterList("year");
            }

            if (sortedList != null) {
                if (selected.contains("Z-A") || selected.contains("Newest")) {
                    Collections.reverse(sortedList);
                }
                loadStudentList(sortedList);
            }
        }

        else {
            String groupName = (String) eventDetailsCB.getSelectedItem();
            EventGroup eg = null;
            for (EventGroup group : currentAccount.getListOfEventGroup()) {
                if (group.getName().equals(groupName)) {
                    eg = group;
                    break;
                }
            }

            if (eg == null) return;
            List<Event> events = new ArrayList<>(eg.getListOfEvents());

            if (selected.contains("Name")) {
                events.sort(Comparator.comparing(Event::getName, String.CASE_INSENSITIVE_ORDER));
                if (selected.contains("Z-A")) Collections.reverse(events);
            } else if (selected.equals("Late Time")) {
                events.sort(Comparator.comparing(Event::getLateTime));
            }
            loadEventsList(events);
        }
    }
}
