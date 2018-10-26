package Classes;

public class TransactionInput {
    public String transactionOutpuId;
    public  TransactionOutput UTXO; //Bitcoin convention, UTXO is unspent transaction outputs

    public TransactionInput(String transactionOutpuId){
        this.transactionOutpuId = transactionOutpuId;
    }
}
