package Helpers;

import Classes.Transaction;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class StringHelper {
    //Applies Sha256 to input
    public static String ApplySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Turn each char of string to it's byte value, then hashes
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder inputAsHex = new StringBuilder();
            for (byte aHash : hash) {
                //Ensure hash[i] is 1 byte
                String hex = Integer.toHexString(0xff & aHash);
                //Keep trailing 0s
                if (hex.length() == 1) inputAsHex.append('0');
                inputAsHex.append(hex);
            }
            return inputAsHex.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //Apply ECDSA signature
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output;
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            output = dsa.sign();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return output;
    }

    //Verify Signature
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //Merkle Root Helper
    public static String getMerkleRoot(ArrayList<Transaction> transactions){
        int layer_node_cnt = transactions.size();
        ArrayList<String> prevTreeLayer = new ArrayList<>(); //List of hashes from prev layer

        for(Transaction transaction: transactions){
            prevTreeLayer.add(transaction.transactionId);
        }
        ArrayList<String> treeLayer = prevTreeLayer; //TODO: figure this out
        while(layer_node_cnt > 1) {
            treeLayer = new ArrayList<String>();
            for(int i=1; i < prevTreeLayer.size(); i++){
                treeLayer.add(ApplySha256(prevTreeLayer.get(i-1) + prevTreeLayer.get(i))); //hashes the two child hashes
            }
            layer_node_cnt = treeLayer.size();
            prevTreeLayer = treeLayer;
        }
        return treeLayer.size() == 1 ? treeLayer.get(0) : "";

    }

    public static String getStringKeyFrom(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
