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
    Webcam webcam;
    JFrame frame;
    JPanel webPanel;
    JButton endAttendance;
    List<Student> listStudents = new ArrayList<>();
    Thread thread;
    boolean isOpen = false;
    boolean flag = false;


    public ScannerCamera(){

        listStudents.add(new Student("Attendance Start: ", Instant.now() + "", "", "", ""));

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
        webcam.close();
        endAttendance = new JButton("End Attendance");
        endAttendance.setPreferredSize(new Dimension(200, 50));
        frame.add(endAttendance, BorderLayout.SOUTH);
        endAttendance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

    }



    public void open() {
        isOpen = true;
        webcam.open();
        thread = new Thread(()->{
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
                    String[] toAdd = res.split(",");
                    Student p = new Student(toAdd[0], toAdd[1], toAdd[2], toAdd[3], toAdd[4]);
                    boolean flag = false;
                    for(Student s : listStudents){
                        if(s.getID().equals(p.getID()) && s.getName().equals(p.getName())) flag = true;
                    }
                    if(!flag){
                        listStudents.add(p);
                        System.out.println("Added: " + p.toString());
                        SoundPlayer.playSound("assets/beep.wav");
                    }
                } catch (NotFoundException e) {
                    try {
                        thread.sleep(100);
                    } catch (InterruptedException ex) {
                        System.out.println("Interrupted exception was called");
                    }
                }
            }
        });

        thread.start();
    }

    public List<Student> close(){
        webcam.close();
        frame.dispose();
        thread.interrupt();
        isOpen = false;
        return listStudents;
    }

    public List<Student> getList(){
        return listStudents;
    }
    public boolean isOpen(){ return isOpen; }

}
