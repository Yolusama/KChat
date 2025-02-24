package KChat.Utils;

import java.security.MessageDigest;
import java.util.Base64;

public class StringEncrypt {
    private StringEncrypt(){}
    public static String Encrypt(String pwd){
        String res="";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(pwd.getBytes());
            res = Base64.getEncoder().encodeToString(encodedHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    public static Boolean Compare(String pwd,String userPwd){
        return pwd.equals(Encrypt(userPwd));
    }
}
