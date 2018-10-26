package Classes;

import Helpers.StringHelper;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Date;

public class Block {
    private String hash;
    private String prevHash;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private String merkleRoot;
    private long timeStamp;
    private int nonce;

    //Constructor
    public Block(String prevHash){
        this.prevHash = prevHash;
        this.timeStamp = new Date().getTime();
        this.hash = createHash();
    }

    public static void main(String[] args){
        Block og = new Block("0");
        System.out.println("Hash for OG: " + og.getHash());

        Block firstborn = new Block(og.getHash());
        System.out.println("Hash for firstborn: " + firstborn.getHash());

        Block theThird = new Block (firstborn.getHash());
        System.out.println("Hash for third block: " + theThird.getHash());
    }

    public String createHash(){
        return StringHelper.ApplySha256(prevHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
    }

    //Difficulty defines number of 0's to solve for
    public void mineBlock(int difficulty) {
        merkleRoot = StringHelper.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');
        //Check for substring of difficulty 0s at beginning, rehash until one is found
        while (!getHash().substring(0, difficulty).equals(target)){
            nonce ++;
            setHash(createHash());
        }
        System.out.println("Mining Successful: " + getHash());
    }

    public boolean addTransaction(Transaction transaction) {
        //Checking that everything is ok with transaction
        if (transaction == null) return false;
        if (!prevHash.equals("0")) {
            if(!transaction.processTransaction()) {
                System.out.println("Transaction failed");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction added to block");
        return true;
    }

    public String getHash() {
        return hash;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }
}
