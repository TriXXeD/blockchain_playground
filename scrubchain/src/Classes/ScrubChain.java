package Classes;

import Helpers.StringHelper;
import com.google.gson.GsonBuilder;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class ScrubChain {
    public static ArrayList<Block> scrubChain = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>(); //Unspent transactions to be used as input
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;

    public static void main(String[] args){
        //Setup bouncy castle as Security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //Initialize wallets
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        //Test keys
        System.out.println("Private and Public Key");
        System.out.println(StringHelper.getStringKeyFrom(walletA.getPrivateKey()));
        System.out.println(StringHelper.getStringKeyFrom(walletA.publicKey));
        //Creating a transaction from A to B
        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.getPrivateKey());
        //Verify the signature
        System.out.println("Is signature verified?: " + transaction.verifySignature());

        //Making a chain
        addBlock(new Block("OG block", "0"));
        addBlock(new Block("Firstborn", scrubChain.get(scrubChain.size()-1).getHash()));
        addBlock(new Block("Second in line", scrubChain.get(scrubChain.size()-1).getHash()));
        //JSON output
        String scrubChainJSON = new GsonBuilder().setPrettyPrinting().create().toJson(scrubChain);
        System.out.println(scrubChainJSON);
        System.out.println(isChainValid());
    }

    public static boolean isChainValid(){
        Block curr;
        Block prev;
        String target = new String(new char[difficulty]).replace('\0', '0');

        for (int i = 1; i < scrubChain.size(); i++) {
            curr = scrubChain.get(i);
            prev = scrubChain.get(i-1);

            //Check that curr hash is true, that prev hash is true and that block is mined
            if(!curr.getHash().equals(curr.createHash()) ||
               !prev.getHash().equals(curr.getPrevHash()) ||
               !curr.getHash().substring(0, difficulty).equals(target)){
                return false;
            }
        }
        return true;
    }

    //add function to encapsulate mining
    public static void addBlock(Block newBlock){
        int mine_index = scrubChain.size();
        scrubChain.add(newBlock);
        System.out.println("Starting to mine block " + mine_index + " with difficulty: " + difficulty);
        scrubChain.get(mine_index).mineBlock(difficulty);

    }
}
