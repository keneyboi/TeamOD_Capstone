import com.github.eduramiba.webcamcapture.drivers.NativeDriver;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ScannerCamera {
    GUIVersion2 gui;
    Webcam webcam;
    JFrame frame;
    JPanel webPanel;
    JButton endAttendance;
    static List<Student> listStudents = new ArrayList<>();
    Thread thread;
    boolean isOpen = false;
    boolean flag = false;
    WebcamPanel wp = null;
    JButton button;
    JFrame mainFrame;

    public ScannerCamera(JButton button, JFrame mainFrame){
        this.button = button;
        this.mainFrame = mainFrame;
        Rectangle mainFrameBounds = mainFrame.getBounds();

        listStudents.add(new Student("Attendance Start: ", Instant.now() + "", "", "", ""));
        endAttendance = new JButton("End Attendance");
        Webcam.setDriver(new NativeDriver());
        webcam = Webcam.getDefault();
        if (webcam == null) {
            System.out.println("No webcam detected!");
            return;
        }
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        frame = new JFrame("Scanner");

        int x = mainFrameBounds.x;
        int y = mainFrameBounds.y;

        frame.setLocation(x + 90, y + 60);
        wp = new WebcamPanel(webcam);
        wp.setMirrored(true);
        wp.setFPSDisplayed(true);
        frame.setPreferredSize(new Dimension(510, 460));
        frame.setSize(510, 460);
        frame.add(wp, BorderLayout.CENTER);
        endAttendance = new JButton("End Attendance");
        endAttendance.setPreferredSize(new Dimension(200, 50));
        frame.add(endAttendance, BorderLayout.SOUTH);
        endAttendance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("End Button Pressed");
                button.setEnabled(true);
                frame.setVisible(false);
                thread.interrupt();
                wp.stop();
                webcam.close();
                GUIVersion2.recordAttendanceAdapter(listStudents);
            }
        });
        frame.setVisible(false);
    }

    public static List<Student> getListStudents(){
        return listStudents;
    }

    public void open() {
        button.setEnabled(false);
        listStudents.clear();
        webcam.open();

        thread = new Thread(()->{
            webcam.open();
            try {
                thread.sleep(1500);
            } catch (InterruptedException e) {}
            frame.setVisible(true);
            while (!Thread.currentThread().isInterrupted()){

                if(frame.isVisible() == false){
                    button.setEnabled(true);
                    wp.stop();
                    webcam.close();
                    thread.interrupt();
                }
                try{
                    frame.repaint();
                    if(!frame.isVisible() ||  !webcam.isOpen()){
                        System.out.println("Stopping thread");
                        break;
                    }
                    BufferedImage image = webcam.getImage();
                    if(image == null) continue;

                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Result result = new MultiFormatReader().decode(bitmap);

                    String res = Encryption.decrypt(result.getText());
                    String[] toAdd = res.split(",");
                    Student p = new Student(toAdd[0], toAdd[1], toAdd[2], toAdd[3], toAdd[4]);

                    boolean flag = false;
                    for(Student s : listStudents){
                        if(s.getID().equals(p.getID())){
                            flag = true;
                            break;
                        }
                    }

                    if(!flag){
                        listStudents.add(p);
                        System.out.println("Scanned: " + p.getName() + "," + p.getID() + "," + p.getSection() + "," + p.getCourse() + "," + p.getYear() + "," + Student.formatInstant(p.getTimeIn()));
                        SoundPlayer.playSound("assets/beep.wav");
                    }
                } catch (NotFoundException | ArrayIndexOutOfBoundsException e) {
                    continue;
                }
            }
        });

        thread.start();
    }

    public List<Student> close(){

        return listStudents;
    }

    public List<Student> getList(){
        return listStudents;
    }

    // BUG 1: When the id is invalid and goes out of bounds, the camera stutters and errors causing the
    // cam to stay open and not close its internal thread:


}
