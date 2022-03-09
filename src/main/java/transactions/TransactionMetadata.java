package transactions;

public class TransactionMetadata {
    public String transactionHash;
    public int ancestryCount;

    public TransactionMetadata(String transactionHash, int ancestryCount){
        this.transactionHash = transactionHash;
        this.ancestryCount = ancestryCount;
    }
}
