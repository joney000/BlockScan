package transactions;

public class Transaction {
    public class TransactionInput{
        String txid;
        double amount;
    }
    public class TransactionOutPut{
        String txid;
        double amount;
    }
    public String txid;
    public int version;
    public long lockTime;
    public Transaction.TransactionInput[] vin;
    public Transaction.TransactionOutPut[] vout;
}


