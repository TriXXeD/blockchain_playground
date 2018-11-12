package Classes;

import com.google.gson.GsonBuilder;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class ScrubChain {
    public static ArrayList<Block> scrubChain = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>(); //Unspent transactions to be used as input
    public static int difficulty = 7;
    public static float minimumTransaction = 0.1f;
    public static Transaction genesisTransaction;

    public static void main(String[] args){
        //Setup bouncy castle as Security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Initialize wallets
        Wallet coins = new Wallet();
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();

        //Genesis
        genesisTransaction = new Transaction(coins.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coins.getPrivateKey());
        genesisTransaction.transactionId = "0";
        genesisTransaction.outputs.add(new TransactionOutput
                                      (genesisTransaction.receiver, genesisTransaction.value, genesisTransaction.transactionId));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Genesis is commencing");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //Interacting with the chain
        Block adam = new Block(scrubChain.get(scrubChain.size()-1).getHash());
        System.out.println("\nWallet A balance: " + walletA.getBalance() + "\nAttempting transfer to B");
        adam.addTransaction(walletA.useMoney(walletB.publicKey, 13.37f));
        addBlock(adam);
        System.out.println("\nWallet A balance: " + walletA.getBalance() + "\nWallet B balance: " + walletB.getBalance());

        Block eve = new Block(scrubChain.get(scrubChain.size()-1).getHash());
        System.out.println("\n Wallet A overspending...");
        eve.addTransaction(walletA.useMoney(walletB.publicKey, 1337f));
        addBlock(eve);
        System.out.println("\nWallet A balance: " + walletA.getBalance() + "\nWallet B balance: " + walletB.getBalance());

        Block cain = new Block(scrubChain.get(scrubChain.size()-1).getHash());
        System.out.println("\nB paying A back");
        cain.addTransaction(walletB.useMoney(walletA.publicKey, 10f));
        System.out.println(("\nTrying multiple trans in single block"));
        cain.addTransaction(walletA.useMoney(walletB.publicKey, 30f));
        System.out.println("\nWallet A balance: " + walletA.getBalance() + "\nWallet B balance: " + walletB.getBalance());

        Block abel = new Block(scrubChain.get(scrubChain.size()-1).getHash());
        System.out.println("\nA circlejerks");
        System.out.println("\nWallet A balance: " + walletA.getBalance());
        abel.addTransaction(walletA.useMoney(walletA.publicKey, 10f));
        System.out.println("\nWallet A balance: " + walletA.getBalance());
        abel.addTransaction(walletA.useMoney(walletA.publicKey, 15f));
        System.out.println("\nWallet A balance: " + walletA.getBalance());
        abel.addTransaction(walletA.useMoney(walletA.publicKey, 25f));
        System.out.println("\nWallet A balance: " + walletA.getBalance());


        //JSON output
        //String scrubChainJSON = new GsonBuilder().setPrettyPrinting().create().toJson(scrubChain);
        //System.out.println(scrubChainJSON);
        //System.out.println(isChainValid());

    }

    public static boolean isChainValid(){
        Block currBlock;
        Block prevBlock;
        String target = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        for (int i = 1; i < scrubChain.size(); i++) {
            currBlock = scrubChain.get(i);
            prevBlock = scrubChain.get(i-1);

            //Check that currBlock hash is true, that prevBlock hash is true and that block is mined
            if(!currBlock.getHash().equals(currBlock.createHash()) ||
               !prevBlock.getHash().equals(currBlock.getPrevHash()) ||
               !currBlock.getHash().substring(0, difficulty).equals(target)
            ) return false;

            //Verifying transaction criteria
            TransactionOutput tempOut;
            for (int j = 0; j < currBlock.getTransactions().size(); j++){
                Transaction currTrans = currBlock.getTransactions().get(j);
                if(!currTrans.verifySignature() ||                          //Signature invalid
                   currTrans.getInputsSum() != currTrans.getOutputsSum()    //Input of money is not equal to output
                ) return false;

                for(TransactionInput input: currTrans.inputs){
                    tempOut = tempUTXOs.get(input.transactionOutpuId);
                    if(tempOut == null ||                                   //Referenced transaction is missing
                       input.UTXO.value != tempOut.value                    //Referenced transaction is invalid
                    ) return false;

                    tempUTXOs.remove(input.transactionOutpuId);             //Remove transaction from tmp list
                }

                for(TransactionOutput output: currTrans.outputs) {
                    tempUTXOs.put(output.id, output);                       //Add outputs to tmp list
                }

                if(currTrans.outputs.get(0).reciever != currTrans.receiver || //Receiver is not intended receiver
                   currTrans.outputs.get(1).reciever != currTrans.sender      //Sender is not receiver for change
                )return false;


            }
        }
        System.out.println("\nValid Block\n");
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
