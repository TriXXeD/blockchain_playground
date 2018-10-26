package Classes;

import Helpers.StringHelper;

import java.security.PublicKey;

public class TransactionOutput {
    public String id;
    public PublicKey reciever; //owner of the coin
    public float value;
    public String parentTransactionId;

    //Constructor
    public TransactionOutput(PublicKey reciever, float value, String parentTransactionId){
        this.reciever = reciever;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringHelper.ApplySha256(StringHelper.getStringKeyFrom(reciever) +
                                                                                Float.toString(value) +
                                                                                parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey){
        return publicKey.equals(reciever);
    }
}
