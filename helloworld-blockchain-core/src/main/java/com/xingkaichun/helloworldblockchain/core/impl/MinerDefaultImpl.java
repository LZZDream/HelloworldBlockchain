package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.Miner;
import com.xingkaichun.helloworldblockchain.core.MinerTransactionDtoDatabase;
import com.xingkaichun.helloworldblockchain.core.Wallet;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.core.tools.Dto2ModelTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 默认实现
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class MinerDefaultImpl extends Miner {

    private static final Logger logger = LoggerFactory.getLogger(MinerDefaultImpl.class);

    //region 属性与构造函数
    //挖矿开关:默认打开挖矿的开关
    private boolean mineOption = true;

    public MinerDefaultImpl(Wallet wallet, BlockchainDatabase blockchainDataBase, MinerTransactionDtoDatabase minerTransactionDtoDataBase) {
        super(wallet,blockchainDataBase,minerTransactionDtoDataBase);
    }
    //endregion


    @Override
    public void start() {
        while(true){
            ThreadUtil.sleep(10);
            if(!mineOption){
                continue;
            }
            Account minerAccount = wallet.createAccount();
            Block block = obtainMiningBlock(minerAccount);
            //随机nonce。好处：不同实例，从不同的nonce开始尝试计算符合要求的nonce。
            long nonce = new Random(Long.MAX_VALUE).nextLong();
            long startTimestamp = System.currentTimeMillis();
            while(true){
                if(!mineOption){
                    break;
                }
                //在挖矿的期间，可能收集到新的交易。每隔一定的时间，重新组装挖矿中的block，组装新的挖矿中的block的时候，可以考虑将新收集到交易放进挖矿中的block。
                if(System.currentTimeMillis()-startTimestamp > GlobalSetting.MinerConstant.MINE_TIMESTAMP_PER_ROUND){
                    break;
                }

                block.setNonce(nonce);
                block.setHash(BlockTool.calculateBlockHash(block));
                //挖矿成功
                if(blockchainDataBase.getConsensus().isReachConsensus(blockchainDataBase,block)){
                    logger.info("祝贺您！挖矿成功！！！区块高度:"+block.getHeight()+",区块哈希:"+block.getHash());
                    //将矿放入区块链
                    boolean isAddBlockToBlockchainSuccess = blockchainDataBase.addBlock(block);
                    if(!isAddBlockToBlockchainSuccess){
                        logger.error("挖矿成功，但是放入区块链失败。请检查异常。");
                        continue;
                    }
                    //将账户放入钱包
                    wallet.addAccount(minerAccount);
                    break;
                }
                nonce++;
            }
        }
    }

    @Override
    public void deactive() {
        mineOption = false;
    }

    @Override
    public void active() {
        mineOption = true;
    }

    @Override
    public boolean isActive() {
        return mineOption;
    }

    /**
     * 获取挖矿中的区块对象
     */
    private Block obtainMiningBlock(Account minerAccount) {
        List<TransactionDTO> forMineBlockTransactionDtoList = minerTransactionDtoDataBase.selectTransactionDtoList(1,10000);
        List<Transaction> forMineBlockTransactionList = new ArrayList<>();
        if(forMineBlockTransactionDtoList != null){
            for(TransactionDTO transactionDTO:forMineBlockTransactionDtoList){
                try {
                    Transaction transaction = Dto2ModelTool.transactionDto2Transaction(blockchainDataBase,transactionDTO);
                    forMineBlockTransactionList.add(transaction);
                } catch (Exception e) {
                    String transactionHash = TransactionTool.calculateTransactionHash(transactionDTO);
                    logger.info("类型转换异常,将从挖矿交易数据库中删除该交易。交易哈希"+transactionHash,e);
                    minerTransactionDtoDataBase.deleteByTransactionHash(transactionHash);
                }
            }
        }
        deleteExceptionTransaction_PointOfBlockView(forMineBlockTransactionList);
        Block nextMineBlock = buildNextMineBlock(forMineBlockTransactionList,minerAccount);
        return nextMineBlock;
    }

    /**
     * 打包处理过程: 将异常的交易丢弃掉【站在区块的角度校验交易】
     */
    private void deleteExceptionTransaction_PointOfBlockView(List<Transaction> packingTransactionList) {
        if(packingTransactionList==null || packingTransactionList.size()==0){
            return;
        }
        deleteExceptionTransaction_PointOfTransactionView(packingTransactionList);

        Set<String> idSet = new HashSet<>();
        Iterator<Transaction> iterator = packingTransactionList.iterator();
        while (iterator.hasNext()){
            Transaction transaction = iterator.next();
            List<TransactionInput> inputs = transaction.getInputs();
            boolean isError = false;
            //校验双花：同一张钱不能被两次交易同时使用【同一个UTXO不允许出现在不同的交易中】
            for(TransactionInput input:inputs){
                String unspendTransactionOutputId = input.getUnspendTransactionOutput().getTransactionOutputId();
                if(idSet.contains(unspendTransactionOutputId)){
                    isError = true;
                    break;
                }else {
                    idSet.add(unspendTransactionOutputId);
                }
            }
            if(isError){
                iterator.remove();
                minerTransactionDtoDataBase.deleteByTransactionHash(transaction.getTransactionHash());
                logger.debug("交易校验失败：交易的输入中同一个UTXO被多次使用。不合法的交易。");
            }
        }
    }

    /**
     * 打包处理过程: 将异常的交易丢弃掉【站在单笔交易的角度校验交易】
     */
    private void deleteExceptionTransaction_PointOfTransactionView(List<Transaction> transactionList) {
        if(transactionList==null || transactionList.size()==0){
            return;
        }
        Iterator<Transaction> iterator = transactionList.iterator();
        while (iterator.hasNext()){
            Transaction transaction = iterator.next();
            boolean transactionCanAddToNextBlock = blockchainDataBase.isTransactionCanAddToNextBlock(null,transaction);
            if(!transactionCanAddToNextBlock){
                iterator.remove();
                minerTransactionDtoDataBase.deleteByTransactionHash(transaction.getTransactionHash());
                logger.debug("交易校验失败：丢弃交易。交易哈希"+transaction.getTransactionHash());
            }
        }
    }

    /**
     * 构建区块的挖矿奖励交易，这里可以实现挖矿奖励的分配。
     */
    private Transaction buildMineAwardTransaction(Account minerAccount, Block block) {
        String address = minerAccount.getAddress();

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.COINBASE);

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        TransactionOutput output = new TransactionOutput();
        output.setAddress(address);
        output.setValue(blockchainDataBase.getIncentive().mineAward(block));
        output.setOutputScript(StackBasedVirtualMachine.createPayToPublicKeyHashOutputScript(address));
        outputs.add(output);

        transaction.setOutputs(outputs);
        transaction.setTransactionHash(TransactionTool.calculateTransactionHash(transaction));
        return transaction;
    }

    /**
     * 构建挖矿区块
     */
    private Block buildNextMineBlock(List<Transaction> packingTransactionList, Account minerAcount) {
        long timestamp = System.currentTimeMillis();

        Block tailBlock = blockchainDataBase.queryTailBlock();
        Block nonNonceBlock = new Block();
        //这个挖矿时间不需要特别精确，没必要非要挖出矿的前一霎那时间。省去了挖矿时实时更新这个时间的繁琐。
        nonNonceBlock.setTimestamp(timestamp);

        if(tailBlock == null){
            nonNonceBlock.setHeight(GlobalSetting.GenesisBlock.HEIGHT +1);
            nonNonceBlock.setPreviousBlockHash(GlobalSetting.GenesisBlock.HASH);
        } else {
            nonNonceBlock.setHeight(tailBlock.getHeight()+1);
            nonNonceBlock.setPreviousBlockHash(tailBlock.getHash());
        }
        nonNonceBlock.setTransactions(packingTransactionList);

        //创建挖矿奖励交易
        Transaction mineAwardTransaction =  buildMineAwardTransaction(minerAcount,nonNonceBlock);
        packingTransactionList.add(0,mineAwardTransaction);


        String merkleTreeRoot = BlockTool.calculateBlockMerkleTreeRoot(nonNonceBlock);
        nonNonceBlock.setMerkleTreeRoot(merkleTreeRoot);
        return nonNonceBlock;
    }
}
