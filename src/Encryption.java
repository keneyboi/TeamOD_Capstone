import java.util.Arrays;
import java.util.Random;

public class Encryption {
    public static String encrypt(String s){

        Random random = new Random();
        int count = 0;
        int encryptSize = (s.length() * 3) + 1;
        char[] string = s.toCharArray();
        char[] encrypted = new char[encryptSize];
        Arrays.fill(encrypted, 'd');
        int dec, inc;
        if(random.nextInt(2) == 0){
            encrypted[encryptSize - 1] = '*';
            inc = 41; dec = 33;
        } else if(random.nextInt(2) == 1){
            encrypted[encryptSize - 1] = '&';
            inc = 24; dec = 11;
        } else {
            encrypted[encryptSize - 1] = ':';
            inc = 36; dec = 29;
        }
        for(int i = 0; i < encryptSize - 1; i++){
            if(i/3 == count){
                char c = string[i/3];
                if((int)c >= 77){
                    c -= dec;
                    encrypted[i] = c;
                    int ch = random.nextInt(126 - 77 + 1) + 77;
                    encrypted[i + 1] = (char)ch;
                    i++;
                } else {
                    c += inc;
                    encrypted[i] = c;
                    int ch = random.nextInt(76 - 33 + 1) + 33;
                    encrypted[i + 1] = (char)ch;
                    i++;
                }
                count++;
            } else {
                int ch = random.nextInt(126 - 33 + 1) + 33;
                encrypted[i] = (char)ch;
            }
        }
        return new String(encrypted);
    }

    public static String decrypt(String s){
        int count = 0;
        int decryptSize = s.length();
        char[] string = s.toCharArray();
        char[] decrypted = new char[decryptSize/3 + 1];
        char key = string[decryptSize - 1];
        int dec, inc;
        if(key == '*'){
            inc = 41; dec = 33;
        } else if(key == '&'){
            inc = 24; dec = 11;
        } else{
            inc = 36; dec = 29;
        }

        for(int i = 0; i < decryptSize - 1; i++){
            if(i/3 == count){
                int c = (int)string[i];
                if((int)string[i + 1] >= 77){
                    c += dec;
                    decrypted[count++] = (char)c;
                } else {
                    c -= inc;
                    decrypted[count++] = (char)c;
                }
            }
        }
        return new String(decrypted);
    }

}
