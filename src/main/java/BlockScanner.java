import httputils.NetWorkHelper;
import transactions.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class BlockScanner{
    final String baseURL = "https://blockstream.info";
    static final long DEFAULTBLOCKTOSCAN = 680000L;

    String[] getTopKTransactionHash(int top, long height)throws Exception{
        String blockHash = getBlockHash(height);// eg. block 680000 is 000000000000000000076c036ff5119e5a5a74df77abf64203473364509f7732
        List<Transaction> transactionInCurrentBlock = getBlockTransactions(blockHash);// it will give list of txn in current block
        return null;
        // todo implementation
    }
    private String getBlockHash(long height) throws Exception{
        String blockHash = NetWorkHelper.getResponse(baseURL + "/api/block-height/" + height);
        return blockHash;
    }

    private List<Transaction> getBlockTransactions(String blockHash) throws Exception{
        String rowResponse = NetWorkHelper.getResponse(baseURL + "/api/block/" + blockHash + "/txs");
        // parse the json into list of transaction entities
        JSONArray jsonResponse = new JSONArray(rowResponse);
        LinkedList<Transaction> transactions = new LinkedList<>();

        for(int pos = 0; pos < jsonResponse.length(); pos++){
            JSONObject transactionJson = (JSONObject)jsonResponse.get(pos);
            Transaction currentTransaction = new Transaction();
            currentTransaction.txid = transactionJson.get("txid").toString();
            JSONArray txnInputs = (JSONArray)transactionJson.get("vin");
            JSONArray txnOutPuts = (JSONArray)transactionJson.get("vout");
            currentTransaction.vin = new Transaction.TransactionInput[txnInputs.length()];
            currentTransaction.vout = new Transaction.TransactionOutPut[txnOutPuts.length()];
        }
        return transactions;
    }

    public static void main(String[] args) throws Exception{
        BlockScanner blockScanner = new BlockScanner();
        long blockToScan = DEFAULTBLOCKTOSCAN;
        if(args.length != 0){
            blockToScan = Long.parseLong(args[0]);// assuming arg(0) is the block hieght
        }
        blockScanner.getTopKTransactionHash(10, blockToScan); // finding the top 10 transactions
    }
}