package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutputId;
import com.xingkaichun.helloworldblockchain.node.dto.transaction.TransactionOutputDetailView;
import com.xingkaichun.helloworldblockchain.node.dto.transaction.TransactionView;

import java.util.List;

/**
 * 区块链浏览器service
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public interface BlockchainBrowserService {

    TransactionOutputDetailView queryTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId);
    List<TransactionOutputDetailView> queryTransactionOutputListByAddress(String address, long from, long size);
    List<TransactionOutputDetailView> queryUnspendTransactionOutputListByAddress(String address, long from, long size);
    List<TransactionOutputDetailView> querySpendTransactionOutputListByAddress(String address, long from, long size);
    TransactionView queryTransactionByTransactionHash(String transactionHash);
    List<TransactionView> queryTransactionListByAddress(String address, long from, long size);
    List<TransactionView> queryTransactionListByBlockHashTransactionHeight(String blockHash, long from, long size);
}
