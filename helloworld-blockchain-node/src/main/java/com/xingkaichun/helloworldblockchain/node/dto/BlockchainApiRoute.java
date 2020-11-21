package com.xingkaichun.helloworldblockchain.node.dto;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class BlockchainApiRoute {

    public static final String PING = "/Api/Blockchain/Ping";
    public static final String QUERY_BLOCKCHAIN_HEIGHT = "/Api/Blockchain/QueryBlockchainHeight";
    public static final String GENERATE_ACCOUNT = "/Api/Blockchain/GenerateAccount";

    public static final String SUBMIT_TRANSACTION_TO_BLOCKCHIAINNEWWORK = "/Api/Blockchain/SubmitTransactionToBlockchainNetwork";

    public static final String QUERY_TRANSACTION_BY_TRANSACTION_HASH = "/Api/Blockchain/QueryTransactionByTransactionHash";
    public static final String QUERY_TRANSACTION_LIST_BY_TRANSACTION_HEIGHT = "/Api/Blockchain/QueryTransactionListByTransactionHeight";
    public static final String QUERY_TRANSACTION_LIST_BY_BLOCK_HASH_TRANSACTION_HEIGHT = "/Api/Blockchain/QueryTransactionListByBlockHashTransactionHeight";
    public static final String QUERY_TRANSACTION_LIST_BY_ADDRESS = "/Api/Blockchain/QueryTransactionListByAddress";
    public static final String QUERY_ACCOUNT_DETAIL_BY_ADDRESS = "/Api/Blockchain/QueryAccountDetailByAddress";

    public static final String QUERY_TRANSACTION_OUTPUT_LIST_BY_ADDRESS = "/Api/Blockchain/QueryTransactionOutputListByAddress";
    public static final String QUERY_UNSPEND_TRANSACTION_OUTPUT_LIST_BY_ADDRESS = "/Api/Blockchain/QueryUnspendTransactionOutputListByAddress";
    public static final String QUERY_SPEND_TRANSACTION_OUTPUT_LIST_BY_ADDRESS = "/Api/Blockchain/QuerySpendTransactionOutputListByAddress";

    //根据交易输出ID，查询[交易输出来源所在的]交易和[交易输出去向所在的]交易
    public static final String QUERY_TRANSACTION_OUTPUT_BY_TRANSACTION_OUTPUT_ID = "/Api/Blockchain/QueryTransactionOutputByTransactionOutputId";

    public static final String QUERY_MINING_TRANSACTION_LIST = "/Api/Blockchain/QueryMiningTransactionList";
    public static final String QUERY_MINING_TRANSACTION_BY_TRANSACTION_HASH = "/Api/Blockchain/QueryMiningTransactionByTransactionHash";

    public static final String QUERY_BLOCKDTO_BY_BLOCK_HEIGHT = "/Api/Blockchain/QueryBlockDtoByBlockHeight";
    public static final String QUERY_BLOCKDTO_BY_BLOCK_HASH = "/Api/Blockchain/QueryBlockDtoByBlockHash";
    public static final String QUERY_LAST10_BLOCKDTO = "/Api/Blockchain/QueryLast10BlockDto";
}
