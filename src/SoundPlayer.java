import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer {
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
}
