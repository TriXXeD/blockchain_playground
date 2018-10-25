package Helpers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringHelper {
    //Applies Sha256 to input
    public static String ApplySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Turn each char of string to it's byte value, then hashes
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer inputAsHex = new StringBuffer();
            for (int i = 0; i < hash.length; i++){
                //Ensure hash[i] is 1 byte
                String hex = Integer.toHexString(0xff & hash[i]);
                //Keep trailing 0s
                if(hex.length() == 1) inputAsHex.append('0');
                inputAsHex.append(hex);
            }
            return inputAsHex.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
