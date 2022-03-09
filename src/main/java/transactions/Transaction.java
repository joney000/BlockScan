package transactions;

public class Transaction {
    public class TransactionInput{
        public String txid;
        public double amount;
        public TransactionInput(String txid, double amount){
            this.txid = txid;
            this.amount = amount;
        }
    }
    public class TransactionOutPut{
        public String txid;
        public double amount;
        public TransactionOutPut(String txid, double amount){
            this.txid = txid;
            this.amount = amount;
        }
    }
    public String txid;
    public int version;
    public long lockTime;
    public Transaction.TransactionInput[] vin;
    public Transaction.TransactionOutPut[] vout;
}


