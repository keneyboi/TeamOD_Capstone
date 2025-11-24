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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Atendo extends JFrame{

    private Account account;
    private String pathname;
    private JPanel mainFrame;
    private JTextField nameTF;
    private JButton generateStudentQRButton;
    private JLabel image;
    private JTextField IDTF;
    private JTextField courseTF;
    private JTextField yearTF;
    private JTextField sectionTF;
    private JButton clearButton;
    private JPanel webCamPanel;
    private JButton readQRButton;
    private List<Student> list = new ArrayList<>();



    public Atendo() throws IOException, WriterException {
        /*this.account = account;
        pathname = "out/" + account.getName();
        File accountFile = new File(pathname);
        if(!accountFile.exists()){
            accountFile.mkdir();
            System.out.println("Created Account Directory");
        }*/

        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Attendance Checker");
        add(mainFrame);
        setLocationRelativeTo(null);
        setVisible(true);

        generateStudentQRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Student s = new Student(nameTF.getText(), IDTF.getText(), sectionTF.getText(), courseTF.getText(), yearTF.getText());
                list.add(s);
                try {
                    ImageIcon img = new ImageIcon(generateQRCode(s));
                    image.setIcon(img);
                } catch (WriterException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameTF.setText("");
                IDTF.setText("");
                courseTF.setText("");
                yearTF.setText("");
                sectionTF.setText("");
                image.setIcon(null);
            }
        });
        readQRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("pressed");
                openScanner(result->{
                    setStudentFields(result);
                });

            }
        });
    }

    public void setStudentFields(String[] info){
        nameTF.setText(info[0]);
        IDTF.setText(info[1]);
        courseTF.setText(info[3]);
        yearTF.setText(info[3]);
        sectionTF.setText(info[2]);
    }

    public String generateQRCode(Student student) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(new String(student.toString().getBytes(StandardCharsets.UTF_8)), BarcodeFormat.QR_CODE, 200, 200);
        Path path = Paths.get(student.getPathName());
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        return student.getPathName();
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




    public static void main(String[] args) {
        try{
            new Atendo();
        } catch (WriterException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
