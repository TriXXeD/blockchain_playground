package Classes;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PublicKey publicKey;
    private PrivateKey privateKey;

    public HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    public Wallet(){
        generateKeyPair();
    }

    private void generateKeyPair(){
        try {
            //Elliptic-curve crypto setup
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            //Init & gen
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    //Calculates owners balance based on unspent transactions
    public float getBalance() {
        float total = 0;

        for(Map.Entry<String, TransactionOutput> item: ScrubChain.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.id, UTXO);  //Add transaction to my list of unspent transactions
                total += UTXO.value;
            }
        }
        return total;

    }

    //Create a new transaction with this wallet as sender, i.e. spend some coin
    public Transaction useMoney(PublicKey recipient, float value){
        if (getBalance() < value) {
            System.out.println("Too poor");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<>();
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break; //Exact transactions are rare, hence the change mechanism in transactions
        }

        Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input : inputs) {
            UTXOs.remove(input.transactionOutpuId);
        }

        return newTransaction;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
