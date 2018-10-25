package Classes;

import Helpers.StringHelper;

import javax.script.ScriptContext;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    public String transactionId;
    public PublicKey sender;
    public PublicKey receiver;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    public static int sequence = 0; //Rough transaction estimator

    //Constructor
    public Transaction(PublicKey from, PublicKey to, float val, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.receiver = to;
        this.value = val;
        this.inputs = inputs;
    }

    private String createHash(){
        sequence++;
        return StringHelper.ApplySha256(StringHelper.getStringKeyFrom(sender) +
                                        StringHelper.getStringKeyFrom(receiver) +
                                        Float.toString(value) +
                                        sequence
        );
    }
    //Signs all the data we dont want tampered with
    public void generateSignature(PrivateKey privateKey) {
        String data = StringHelper.getStringKeyFrom(sender) +
                      StringHelper.getStringKeyFrom(receiver) +
                      Float.toString(value);
        signature = StringHelper.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature(){
        String data = StringHelper.getStringKeyFrom(sender) +
                      StringHelper.getStringKeyFrom(receiver) +
                      Float.toString(value);
        return StringHelper.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("Signature not verified");
            return false;
        }

        //Collect inputs(ensure unspent)
        for (TransactionInput i: inputs) {
            i.UTXO = ScrubChain.UTXOs.get(i.transactionOutpuId);
        }

        if(getInputsValue() < ScrubChain.minimumTransaction) {
            System.out.println("Transaction too small");
            return false;
        }

        //Generate transaction output
        float leftover = getInputsValue() - value; //get value of inputs, then the left over change
        transactionId = createHash();
        outputs.add(new TransactionOutput(this.receiver, value, transactionId));
        outputs.add(new TransactionOutput(this.sender, leftover, transactionId));

        //add output to Unspent
        for (TransactionOutput o: outputs) {
            ScrubChain.UTXOs.put(o.id, o);
        }

        //remove transaction inputs from UTXO lists as spent
        for(TransactionInput i : inputs){
            if(i.UTXO == null) continue;
            ScrubChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    //returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            total += i.UTXO.value;
        }
        return total;
    }

    //returns sum of outputs:
    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }
}
