import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
    private JButton hypergeometricButton;
    private JButton binomialButton;
    private JButton bernoulliButton;
    private JButton permutationButton;
    private JButton combinationButton;
    private JButton solveBtn;
    private JPanel combinationPane;
    private JPanel permutationPane;
    private JPanel bernoulliPane;
    private JPanel binomialPane;
    private JPanel hypergeometricPane;
    private JButton historyBtn;
    private JButton accountBtn;
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
    private List<Account> listOfAccounts = new ArrayList<>();

    CardLayout innerCardLayout = (CardLayout)InnerCardPanel.getLayout();
    CardLayout cardLayout = (CardLayout)contentPanel.getLayout();
    JButton[] solveButtons = new JButton[]{combinationButton, permutationButton, bernoulliButton, binomialButton, hypergeometricButton};

    public GUIVersion2(){
        getAccountList();




        ImageIcon logo = new ImageIcon("assets/test.png");
        Image newLogo = logo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(newLogo));
        setTitle("Discrete Calculator");
        setSize(650, 380);
        setMinimumSize(new Dimension(250, 380));
        setResizable(true);
        add(contentPanel);

        solveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(getHeight() < 530 && !combinationButton.isVisible()) setSize(getWidth(), 530);
                for(JButton b : solveButtons){
                    if(!b.isVisible()) b.setVisible(true);
                    else b.setVisible(false);
                }

            }
        });

        accountBtn.addActionListener(this);
        historyBtn.addActionListener(this);
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
                } catch (DefaultErrorException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }catch (InvalidPasswordException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    createPassPF.setText("");
                    createPassConfirmPW.setText("");
                }
            }
        });
    }

    public void createAccountCSV(){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("out/Account/accounts.csv"))){
            for(Account a : listOfAccounts){
                System.out.println("Adding: " + a);
                bw.write(a.getName() + "," + a.getEmail() + "," + new String(a.getPassword()));
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Unable to create account.csv");
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
        if(e.getSource() == accountBtn) innerCardLayout.show(InnerCardPanel, "Account");
        if(e.getSource() == historyBtn) innerCardLayout.show(InnerCardPanel, "History");
    }

    public static void main(String[] args) {
        new GUIVersion2();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
