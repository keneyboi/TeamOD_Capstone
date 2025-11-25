import com.github.eduramiba.webcamcapture.drivers.NativeDriver;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JButton toAdd2;
    private JButton toAdd1;
    private JButton scanIDBT;
    private JButton createIDBT;
    private JButton createEventBT;
    private JButton createBT;
    private JPanel createEventPane;
    private JPanel createIDPane;
    private JPanel eventGroupPane;
    private JPanel scanIDPane;
    private JPanel hypergeometricPane;
    private JButton eventGroupBT;
    private JButton accountBT;
    private JLabel logoLabel;
    private JPanel accountPane;
    private JPanel historyPane;
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
    private List<Account> listOfAccounts = new ArrayList<>();
    private DefaultTableModel dm = new DefaultTableModel();

    private Account currentAccount;


    CardLayout innerCardLayout = (CardLayout)InnerCardPanel.getLayout();
    CardLayout cardLayout = (CardLayout)contentPanel.getLayout();
    JButton[] solveButtons = new JButton[]{createEventBT, createIDBT, scanIDBT, toAdd1, toAdd2};
    Dimension small = new Dimension(300, 400);
    Dimension medium = new Dimension(700, 550);

    public GUIVersion2(){

        // Preparation Functions Here --> initializes app's resources
        getAccountList();
        createAccountFolders();
        setTable();

        setSize(small);
        ImageIcon logo = new ImageIcon("assets/test.png");
        Image newLogo = logo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(newLogo));
        setTitle("Attendo");
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(250, 380));
        setResizable(true);
        add(contentPanel);

        createBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(getHeight() < 530 && !createEventBT.isVisible()) setSize(getWidth(), 530);
                for(JButton b : solveButtons){
                    if(!b.isVisible()) b.setVisible(true);
                    else b.setVisible(false);
                }

            }
        });

        accountBT.addActionListener(this);
        eventGroupBT.addActionListener(this);
        for(JButton b : solveButtons){
            b.addActionListener(this);
        }

        setVisible(true);
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
                    if(createAccTF.getText().isEmpty()) throw new DefaultErrorException("Enter Valid Username!");
                    if(!createEmailTF.getText().endsWith("@gmail.com")) throw new DefaultErrorException("Enter Valid Email!");
                    if(Arrays.compare(createPassPF.getPassword(), createPassConfirmPW.getPassword()) != 0) throw new DefaultErrorException("Passwords don't match");
                    listOfAccounts.add(new Account(createAccTF.getText(), createEmailTF.getText(), createPassPF.getPassword()));
                    createAccountCSV();
                    createAccTF.setText("");
                    createEmailTF.setText("");
                    createPassPF.setText("");
                    createPassConfirmPW.setText("");
                    JOptionPane.showMessageDialog(null, "Account successfully created! Please log in.");
                    setSize(small);
                    cardLayout.show(contentPanel, "LogIn");
                } catch (DefaultErrorException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }catch (InvalidPasswordException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    createPassPF.setText("");
                    createPassConfirmPW.setText("");
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAccountList();
                for(Account a : listOfAccounts){
                    if((a.getEmail().equals(usernameTF.getText()) || a.getName().equals(usernameTF.getText())) && Arrays.compare(a.getPassword(), passwordPF.getPassword()) == 0){
                        currentAccount = a;
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
                openScanner(result->{
                    setStudentFields(result);
                });
            }
        });
        accountExistsBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "LogIn");
            }
        });
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
        System.out.println("called set table");
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
            try {
                System.out.println("in");
                Webcam.setDriver(new NativeDriver());

                Webcam webcam = Webcam.getDefault();
                if (webcam == null) {
                    System.out.println("No webcam detected!");
                    return;
                }
                webcam.setViewSize(WebcamResolution.VGA.getSize());

                webcam.open();

                WebcamPanel wp = new WebcamPanel(webcam);
                wp.setMirrored(true);
                wp.setFPSDisplayed(true);
                JFrame frame = new JFrame("test");
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
                        System.out.println("Result: " + result.getText());
                        callback.accept(result.getText().split(","));

                        webcam.close();
                        frame.dispose();
                        break;
                    } catch (NotFoundException e) {
                        System.out.println("not found");
                        Thread.sleep(0);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public String generateQRCode(Student student) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(new String(student.toString().getBytes(StandardCharsets.UTF_8)), BarcodeFormat.QR_CODE, 200, 200);
        Path path = Paths.get(student.getPathName());
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        return student.getPathName();
    }

    public void createAccountCSV(){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("out/Account/AccountList.csv"))){
            for(Account a : listOfAccounts){
                System.out.println("Adding: " + a);
                bw.write(a.getName() + "," + a.getEmail() + "," + new String(a.getPassword()));
                bw.newLine();

                File file = new File("out/Account/" + a.getName());
                if (file != null && !file.exists()) {
                    file.mkdirs();
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Unable to create account.csv");
        }
    }

    public void createAccountFolders(){
        for(Account a : listOfAccounts){
            File file = new File("out/Account/" + a.getName());
            if (file != null && !file.exists()) {
                file.mkdirs();
            }
        }
    }

    public void getAccountList(){
        try(BufferedReader br = new BufferedReader(new FileReader("out/Account/AccountList.csv"))){
            String line;
            while((line = br.readLine()) != null){
                String[] tokens = line.split(",");
                listOfAccounts.add(new Account(tokens[0], tokens[1], tokens[2].toCharArray()));
            }
        } catch (InvalidPasswordException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,"No Accounts Available");
            cardLayout.show(contentPanel, "CreateAccount");
            setSize(small);
        } catch (IOException e) {

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for(JButton b : solveButtons){
            if(((JButton)(e.getSource())).equals(b)){
                innerCardLayout.show(InnerCardPanel, b.getActionCommand());
            }
        }
        if(e.getSource() == accountBT) innerCardLayout.show(InnerCardPanel, "Account");
        if(e.getSource() == eventGroupBT) innerCardLayout.show(InnerCardPanel, "History");
    }

    public static void main(String[] args) {
        new GUIVersion2();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
