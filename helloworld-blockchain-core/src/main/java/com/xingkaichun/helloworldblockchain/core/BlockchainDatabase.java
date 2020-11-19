package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutputId;

import java.util.List;


/**
 * 区块链数据库：该类用于区块链数据的持久化。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public abstract class BlockchainDatabase {

    //region 变量与构造函数
    //区块共识
    protected Consensus consensus ;
    //矿工激励
    protected Incentive incentive ;

    public BlockchainDatabase(Consensus consensus, Incentive incentive) {
        this.consensus = consensus;
        this.incentive = incentive;
    }
    //endregion



    //region 区块增加与删除
    /**
     * 将一个区块添加到区块链的尾部。
     * 这是一个有些复杂的操作，需要考虑如下几点:
     * 新增区块本身的数据的正确性;
     * 新增的区块是否能够正确衔接到区块链的尾部;
     */
    public abstract boolean addBlock(Block block) ;
    /**
     * 删除区块链的尾巴区块(最后一个区块)
     */
    public abstract void deleteTailBlock() ;
    /**
     * 删除区块高度大于等于@blockHeight@的区块
     * //TODO 换个更合适的方法名？例如：deleteBlockHeightGreatEqualThan
     */
    public abstract void deleteBlocksUtilBlockHeightLessThan(long blockHeight) ;
    //endregion



    //region 校验区块、交易
    /**
     * 检测区块是否可以被添加到区块链上
     * 只有一种情况，区块可以被添加到区块链，即: 区块是区块链上的下一个区块
     */
    public abstract boolean isBlockCanAddToBlockchain(Block block) ;
    /**
     * 校验交易是否可以被添加进下一个区块之中。
     * 如果校验的是奖励交易，则需要整个区块的信息，因此这个函数包含了两个参数：交易所在的区块、交易
     */
    public abstract boolean isTransactionCanAddToNextBlock(Block block, Transaction transaction) ;
    //endregion



    //region 普通查询
    /**
     * 查询区块链的长度
     */
    public abstract long queryBlockchainHeight() ;
    /**
     * 查询区块链中总的交易数量
     */
    public abstract long queryTransactionCount() ;
    /**
     * 根据区块哈希查找区块高度
     */
    public abstract long queryBlockHeightByBlockHash(String blockHash) ;
    /**
     * 根据已花费的交易输出ID查询花费去向所在的交易的哈希
     */
    public abstract String queryToTransactionHashByTransactionOutputId(TransactionOutputId transactionOutputId) ;
    //endregion



    //region 区块查询
    /**
     * 查询区块链上的最后一个区块
     */
    public abstract Block queryTailBlock() ;
    /**
     * 在区块链中根据区块高度查找区块
     */
    public abstract Block queryBlockByBlockHeight(long blockHeight) ;
    /**
     * 在区块链中根据区块哈希查找区块
     */
    public abstract Block queryBlockByBlockHash(String blockHash) ;
    //endregion



    //region 交易查询
    /**
     * 在区块链中根据交易哈希查找交易
     */
    public abstract Transaction queryTransactionByTransactionHash(String transactionHash) ;
    /**
     * 根据交易高度查询交易。交易高度从1开始。
     */
    public abstract List<Transaction> queryTransactionListByTransactionHeight(long from,long size) ;
    /**
     * 根据地址查询交易列表。from从0开始。
     */
    public abstract List<Transaction> queryTransactionListByAddress(String address,long from,long size) ;
    //endregion



    //region 交易输出查询
    /**
     * 在区块链中根据 交易输出哈希 查找交易输出
     */
    public abstract TransactionOutput queryTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId) ;
    /**
     * 在区块链中根据 交易输出哈希 查找未花费交易输出
     */
    public abstract TransactionOutput queryUnspendTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId) ;
    /**
     * 根据地址查询交易输出。from从0开始。
     */
    public abstract List<TransactionOutput> queryTransactionOutputListByAddress(String address,long from,long size) ;
    /**
     * 根据地址查询未花费交易输出。from从0开始。
     */
    public abstract List<TransactionOutput> queryUnspendTransactionOutputListByAddress(String address,long from,long size) ;
    /**
     * 根据地址查询已花费交易输出。from从0开始。
     */
    public abstract List<TransactionOutput> querySpendTransactionOutputListByAddress(String address, long from, long size);
    //endregion



    //region get set
    public Incentive getIncentive() {
        return incentive;
    }

    public Consensus getConsensus() {
        return consensus;
    }
    //endregion
}