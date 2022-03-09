import httputils.NetWorkHelper;
import transactions.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;
import transactions.TransactionMetadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class BlockScanner{
    final String baseURL = "https://blockstream.info";
    static final long DEFAULTBLOCKTOSCAN = 680000L;

    TransactionMetadata[] getTopKTransactionHash(int top, long height)throws Exception{
        String blockHash = getBlockHash(height);// eg. block 680000 is 000000000000000000076c036ff5119e5a5a74df77abf64203473364509f7732
        List<Transaction> transactionInCurrentBlock = getBlockTransactions(blockHash);// it will give list of txn in current block
        // since we are only considering current block, we can do recursive DFS to identify ancestryCount
        TransactionMetadata[] transactionMetadata = new TransactionMetadata[transactionInCurrentBlock.size()];
        int tranSactionIndex = -1;
        for(Transaction currentTransaction: transactionInCurrentBlock){
            transactionMetadata[++tranSactionIndex] = new TransactionMetadata(currentTransaction.txid, 0);
        }
        HashSet<String> visitedNodes = new HashSet<>();
        int transactionPos = -1;
        for(TransactionMetadata currentTransactionMetadata: transactionMetadata){
            if(!visitedNodes.contains(currentTransactionMetadata.transactionHash)){
                int prevTransactionIndex = -1;
                for(int pos = 0; pos <  transactionInCurrentBlock.size(); pos++){
                    if(transactionInCurrentBlock.get(pos).txid.equals(currentTransactionMetadata.transactionHash)){
                        prevTransactionIndex = pos;
                        break;
                    }
                }
                currentTransactionMetadata.ancestryCount =  graphSearch(visitedNodes, ++transactionPos, transactionInCurrentBlock, transactionMetadata);
            }
        }
        Arrays.sort(transactionMetadata, (TransactionMetadata p, TransactionMetadata q)-> -(p.ancestryCount - q.ancestryCount));
        // sorting decending order
        assert (transactionInCurrentBlock.size() <= top);
        return Arrays.copyOfRange(transactionMetadata, 0, top);
    }
    // DFS for finding the ancestryCount
    int graphSearch(HashSet<String> visitedNodes, int transactionIndex, List<Transaction> transactionInCurrentBlock, TransactionMetadata[] transactionMetadata) throws Exception{
        Transaction currentTransaction = transactionInCurrentBlock.get(transactionIndex);
        if(visitedNodes.contains(currentTransaction.txid)){
            return transactionMetadata[transactionIndex].ancestryCount;
        }
        visitedNodes.add(currentTransaction.txid);
        int ancestryCount = 1;
        for(Transaction.TransactionInput input : currentTransaction.vin){
            if(!visitedNodes.contains(input.txid)){
                ancestryCount += graphSearch(visitedNodes, transactionIndex, transactionInCurrentBlock,transactionMetadata);
            }
        }
        return transactionMetadata[transactionIndex].ancestryCount = ancestryCount;
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
            for(int inputPos = 0; inputPos < txnInputs.length(); inputPos++){
                currentTransaction.vin[inputPos].txid = ((JSONObject)txnInputs.get(inputPos)).get("txid").toString();
            }
            for(int outputPos = 0; outputPos < txnOutPuts.length(); outputPos++){
                currentTransaction.vout[outputPos].txid = ((JSONObject)txnOutPuts.get(outputPos)).get("scriptpubkey").toString();
            }
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