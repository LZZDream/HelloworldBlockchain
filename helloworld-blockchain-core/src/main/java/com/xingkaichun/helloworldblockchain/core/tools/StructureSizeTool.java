package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptKey;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptLock;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 存放有关存储容量有关的常量，例如区块最大的存储容量，交易最大的存储容量
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class StructureSizeTool {

    private static final Logger logger = LoggerFactory.getLogger(StructureSizeTool.class);

    //region 校验存储容量
    /**
     * 校验区块的存储容量是否合法：用来限制区块所占存储空间的大小。
     */
    public static boolean isBlockStorageCapacityLegal(Block block) {
        //校验时间戳占用存储空间
        long timestamp = block.getTimestamp();
        //校验时间的长度
        if(String.valueOf(timestamp).length() < GlobalSetting.BlockConstant.BLOCK_TEXT_TIMESTAMP_MIN_SIZE){
            logger.debug("区块校验失败：区块时间戳所占存储空间过小。");
            return false;
        }
        if(String.valueOf(timestamp).length() > GlobalSetting.BlockConstant.BLOCK_TEXT_TIMESTAMP_MAX_SIZE){
            logger.debug("区块校验失败：区块时间戳所占存储空间过大。");
            return false;
        }

        //校验共识占用存储空间
        long nonce = block.getNonce();
        if(LongUtil.isLessThan(nonce, GlobalSetting.BlockConstant.MIN_NONCE)){
            return false;
        }
        if(LongUtil.isGreatThan(nonce, GlobalSetting.BlockConstant.MAX_NONCE)){
            return false;
        }

        //校验区块中的交易占用的存储空间
        long blockTextSize = calculateBlockTextSize(block);
        if(blockTextSize > GlobalSetting.BlockConstant.BLOCK_TEXT_MAX_SIZE){
            logger.debug(String.format("区块数据异常，区块容量超过限制。"));
            return false;
        }

        List<Transaction> transactions = block.getTransactions();
        //校验交易的大小
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(!isTransactionStorageCapacityLegal(transaction)){
                    logger.debug("交易数据异常，交易的容量非法。");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 校验交易的存储容量是否合法：用来限制交易的所占存储空间的大小。
     */
    public static boolean isTransactionStorageCapacityLegal(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        List<TransactionOutput> outputs = transaction.getOutputs();

        //校验交易输入
        if(inputs != null){
            for(TransactionInput transactionInput:inputs){
                ScriptKey scriptKey = transactionInput.getScriptKey();
                if(calculateScriptTextSize(scriptKey) > GlobalSetting.ScriptConstant.SCRIPT_INPUT_TEXT_MAX_SIZE){
                    logger.debug("交易校验失败：交易输入脚本所占存储空间超出限制。");
                    return false;
                }
            }
        }

        //校验交易输出
        if(outputs != null){
            for(TransactionOutput transactionOutput:outputs){
                String address = transactionOutput.getAddress();
                if(address.length() < GlobalSetting.TransactionConstant.TRANSACTION_TEXT_ADDRESS_MIN_SIZE){
                    logger.debug("账户地址长度过短");
                    return false;
                }
                if(address.length() > GlobalSetting.TransactionConstant.TRANSACTION_TEXT_ADDRESS_MAX_SIZE){
                    logger.debug("账户地址长度过长");
                    return false;
                }

                long value = transactionOutput.getValue();
                if(calculateLongTextSize(value)> GlobalSetting.TransactionConstant.TRANSACTION_TEXT_VALUE_MAX_SIZE){
                    logger.debug("交易校验失败：交易金额所占存储空间超出限制。");
                    return false;
                }

                ScriptLock scriptLock = transactionOutput.getScriptLock();
                if(calculateScriptTextSize(scriptLock) > GlobalSetting.ScriptConstant.SCRIPT_OUTPUT_TEXT_MAX_SIZE){
                    logger.debug("交易校验失败：交易输出脚本所占存储空间超出限制。");
                    return false;
                }
            }
        }

        //校验整笔交易所占存储空间
        if(calculateTransactionTextSize(transaction) > GlobalSetting.BlockConstant.TRANSACTION_TEXT_MAX_SIZE){
            logger.debug("交易数据异常，交易所占存储空间太大。");
            return false;
        }
        return true;
    }
    //endregion



    //region 计算文本大小
    public static long calculateBlockTextSize(Block block) {
        long size = 0;
        long timestamp = block.getTimestamp();
        size += String.valueOf(timestamp).length();

        List<Transaction> transactions = block.getTransactions();
        for(Transaction transaction:transactions){
            size += calculateTransactionTextSize(transaction);
        }

        size += calculateLongTextSize(block.getNonce());
        return size;
    }
    //TODO 计算DTO的大小
    public static long calculateTransactionTextSize(Transaction transaction) {
        long size = 0;
        List<TransactionInput> inputs = transaction.getInputs();
        size += calculateTransactionInputTextSize(inputs);
        List<TransactionOutput> outputs = transaction.getOutputs();
        size += calculateTransactionOutputTextSize(outputs);
        return size;
    }
    private static long calculateTransactionOutputTextSize(List<TransactionOutput> outputs) {
        long size = 0;
        if(outputs == null || outputs.size()==0){
            return size;
        }
        for(TransactionOutput transactionOutput:outputs){
            size += calculateTransactionOutputTextSize(transactionOutput);
        }
        return size;
    }
    private static long calculateTransactionOutputTextSize(TransactionOutput output) {
        long size = 0;
        if(output == null){
            return 0L;
        }
        String address = output.getAddress();
        size += address.length();
        long value = output.getValue();
        size += calculateLongTextSize(value);
        ScriptLock scriptLock = output.getScriptLock();
        size += calculateScriptTextSize(scriptLock);
        return size;
    }
    private static long calculateTransactionInputTextSize(List<TransactionInput> inputs) {
        long size = 0;
        if(inputs == null || inputs.size()==0){
            return size;
        }
        for(TransactionInput transactionInput:inputs){
            size += calculateTransactionInputTextSize(transactionInput);
        }
        return size;
    }
    private static long calculateTransactionInputTextSize(TransactionInput input) {
        long size = 0;
        if(input == null){
            return size;
        }
        TransactionOutput unspendTransactionOutput = input.getUnspendTransactionOutput();
        size += calculateTransactionOutputTextSize(unspendTransactionOutput);
        ScriptKey scriptKey = input.getScriptKey();
        size += calculateScriptTextSize(scriptKey);
        return size;
    }
    private static long calculateScriptTextSize(Script script) {
        long size = 0;
        if(script == null || script.size()==0){
            return size;
        }
        for(String scriptCode:script){
            size += scriptCode.length();
        }
        return size;
    }
    private static long calculateLongTextSize(long number){
        return String.valueOf(number).length();
    }
    //endregion

    /**
     * 校验区块的结构
     */
    public static boolean isBlockStructureLegal(Block block) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null || transactions.size()==0){
            logger.debug("区块数据异常：区块中的交易数量为0。区块必须有一笔CoinBase的交易。");
            return false;
        }
        //校验区块中交易的数量
        long transactionCount = BlockTool.getTransactionCount(block);
        if(transactionCount > GlobalSetting.BlockConstant.BLOCK_MAX_TRANSACTION_COUNT){
            logger.debug(String.format("区块数据异常，区块里包含的交易数量超过限制。"));
            return false;
        }
        for(int i=0; i<transactions.size(); i++){
            Transaction transaction = transactions.get(i);
            if(i == 0){
                if(transaction.getTransactionType() != TransactionType.COINBASE){
                    logger.debug("区块数据异常：区块第一笔交易必须是CoinBase。");
                    return false;
                }
            }else {
                if(transaction.getTransactionType() != TransactionType.NORMAL){
                    logger.debug("区块数据异常：区块非第一笔交易必须是普通交易。");
                    return false;
                }
            }
        }
        //校验交易的结构
        for(int i=0; i<transactions.size(); i++){
            Transaction transaction = transactions.get(i);
            if(!isTransactionStructureLegal(transaction)){
                logger.debug("交易数据异常：交易结构异常。");
                return false;
            }
        }
        return true;
    }

    /**
     * 校验交易的结构
     */
    public static boolean isTransactionStructureLegal(Transaction transaction) {
        TransactionType transactionType = transaction.getTransactionType();
        if(TransactionType.COINBASE == transactionType){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size()!=0){
                logger.debug("交易数据异常：CoinBase交易不能有交易输入。");
                return false;
            }
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs == null || outputs.size()!=1){
                logger.debug("交易数据异常：CoinBase交易有且只能有一笔交易。");
                return false;
            }
            return true;
        }else if(TransactionType.NORMAL == transactionType){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size() > GlobalSetting.TransactionConstant.TRANSACTION_MAX_INPUT_COUNT){
                logger.debug("交易数据异常：普通交易的交易输入数量超过限制。");
                return false;
            }
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs == null || outputs.size() > GlobalSetting.TransactionConstant.TRANSACTION_MAX_OUTPUT_COUNT){
                logger.debug("交易数据异常：普通交易的交易输出数量超过限制。");
                return false;
            }
            return true;
        }else {
            logger.debug("交易数据异常：不能识别的交易的类型。");
            return false;
        }
    }
}
