import httputils.NetWorkHelper;

public class BlockScanner{
    final String baseURL = "https://blockstream.info";
    static final long DEFAULTBLOCKTOSCAN = 680000L;

    String[] getTopKTransactionHash(int top, long height){
        return null;
        // todo implementation
    }
    private String getBlockHash(long height) throws Exception{
        String blockHash = NetWorkHelper.getResponse(baseURL + "/api/block-height/" + height);
        return blockHash;
    }

    public static void main(String[] args) {
        BlockScanner blockScanner = new BlockScanner();
        long blockToScan = DEFAULTBLOCKTOSCAN;
        if(args.length != 0){
            blockToScan = Long.parseLong(args[0]);// assuming arg(0) is the block hieght
        }
        blockScanner.getTopKTransactionHash(10, blockToScan); // finding the top 10 transactions
    }
}