package Classes;

import Helpers.StringHelper;

import java.util.Date;

public class Block {
    private String hash;
    private String prevHash;
    private String data;
    private long timeStamp;
    private int nonce;

    //Constructor
    public Block(String data, String prevHash){
        this.data = data;
        this.prevHash = prevHash;
        this.timeStamp = new Date().getTime();
        this.hash = createHash();
    }

    public static void main(String[] args){
        Block og = new Block("I'm the OG", "0");
        System.out.println("Hash for OG: " + og.getHash());

        Block firstborn = new Block("First in line", og.getHash());
        System.out.println("Hash for firstborn: " + firstborn.getHash());

        Block theThird = new Block ("Small chain", firstborn.getHash());
        System.out.println("Hash for third block: " + theThird.getHash());
    }

    public String createHash(){
        return StringHelper.ApplySha256(prevHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
    }

    //Difficulty defines number of 0's to solve for
    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        //Check for substring of difficulty 0s at beginning, rehash until one is found
        while (!getHash().substring(0, difficulty).equals(target)){
            nonce ++;
            setHash(createHash());
        }
        System.out.println("Mining Successful: " + getHash());
    }

    public String getHash() {
        return hash;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public String getData() {
        return data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
