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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    public ScannerCamera(){
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
        WebcamPanel wp = new WebcamPanel(webcam);
        wp.setMirrored(true);
        wp.setFPSDisplayed(true);
        frame.setSize(webcam.getViewSize());
        frame.add(wp, BorderLayout.CENTER);
        endAttendance = new JButton("End Attendance");
        endAttendance.setPreferredSize(new Dimension(200, 50));
        frame.add(endAttendance, BorderLayout.SOUTH);
        endAttendance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("End Button Pressed");
                frame.setVisible(false);
                thread.interrupt();
                wp.stop();
                webcam.close();
                GUIVersion2.recordAttendanceAdapter(listStudents);
            }
        });
    }

    public static List<Student> getListStudents(){
        return listStudents;
    }

    public void open() {
        listStudents.clear();
        isOpen = true;
        webcam.open();
        frame.setVisible(true);
        thread = new Thread(()->{

            while (!Thread.currentThread().isInterrupted()){
                try{
                    frame.repaint();
                    if(!frame.isVisible() || !webcam.isOpen()){
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
                        System.out.println("Added: " + p.toString());
                        SoundPlayer.playSound("assets/beep.wav");
                    }
                } catch (NotFoundException e) {

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
    public boolean isOpen(){ return isOpen; }

}
