package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * (区块、交易)结构大小工具类
 *
 * 存储大小是基于DTO对象计算的。考虑到多种语言实现区块链，若采用不同语言实现所构造的model计算大小，可能比较复杂。
 * 而DTO本身能组成区块链的完整数据，DTO数据又比较精简，所以基于DTO计算区块大小、交易大小非常方便。
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
        return isBlockStorageCapacityLegal(Model2DtoTool.block2BlockDTO(block));
    }
    public static boolean isBlockStorageCapacityLegal(BlockDTO blockDTO) {
        //区块的时间戳的长度不需要校验  假设时间戳长度不正确，则在随后的业务逻辑中走不通

        //区块的前哈希的长度不需要校验  假设前哈希长度不正确，则在随后的业务逻辑中走不通

        //校验共识占用存储空间
        long nonce = blockDTO.getNonce();
        if(LongUtil.isLessThan(nonce, GlobalSetting.BlockConstant.MIN_NONCE)){
            return false;
        }
        if(LongUtil.isGreatThan(nonce, GlobalSetting.BlockConstant.MAX_NONCE)){
            return false;
        }

        //校验区块中的交易占用的存储空间
        long blockTextSize = calculateBlockTextSize(blockDTO);
        if(blockTextSize > GlobalSetting.BlockConstant.BLOCK_TEXT_MAX_SIZE){
            logger.debug(String.format("区块数据异常，区块容量超过限制。"));
            return false;
        }

        //校验每一笔交易占用的存储空间
        List<TransactionDTO> transactionDtoList = blockDTO.getTransactionDtoList();
        if(transactionDtoList != null){
            for(TransactionDTO transactionDTO:transactionDtoList){
                if(!isTransactionStorageCapacityLegal(transactionDTO)){
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
        return isTransactionStorageCapacityLegal(Model2DtoTool.transaction2TransactionDTO(transaction));
    }

    public static boolean isTransactionStorageCapacityLegal(TransactionDTO transactionDTO) {
        List<TransactionInputDTO> transactionInputDtoList = transactionDTO.getTransactionInputDtoList();
        List<TransactionOutputDTO> transactionOutputDtoList = transactionDTO.getTransactionOutputDtoList();

        //校验交易输入
        if(transactionInputDtoList != null){
            for(TransactionInputDTO transactionInputDTO:transactionInputDtoList){
                //交易的未花费输出所占存储容量不需要校验  假设不正确，则在随后的业务逻辑中走不通
                //校验脚本存储容量
                InputScriptDTO inputScriptDTO = transactionInputDTO.getInputScriptDTO();
                //校验脚本操作码操作数的容量
                if(!isScriptStorageCapacityLegal(inputScriptDTO)){
                    return false;
                }
            }
        }

        //校验交易输出
        if(transactionOutputDtoList != null){
            for(TransactionOutputDTO transactionOutputDTO:transactionOutputDtoList){
                //交易金额所占存储容量不需要校验  假设不正确，则在随后的业务逻辑中走不通
                //校验脚本存储容量
                OutputScriptDTO outputScriptDTO = transactionOutputDTO.getOutputScriptDTO();
                //校验脚本操作码操作数的容量
                if(!isScriptStorageCapacityLegal(outputScriptDTO)){
                    return false;
                }

            }
        }

        //校验整笔交易所占存储空间
        if(calculateTransactionTextSize(transactionDTO) > GlobalSetting.BlockConstant.TRANSACTION_TEXT_MAX_SIZE){
            logger.debug("交易数据异常，交易所占存储空间太大。");
            return false;
        }
        return true;
    }
    //endregion



    //region 计算文本大小
    public static long calculateBlockTextSize(Block block) {
        return calculateBlockTextSize(Model2DtoTool.block2BlockDTO(block));
    }
    public static long calculateBlockTextSize(BlockDTO blockDTO) {
        long size = 0;
        long timestamp = blockDTO.getTimestamp();
        size += calculateLongTextSize(timestamp);

        String previousBlockHash = blockDTO.getPreviousBlockHash();
        size += previousBlockHash.length();

        long nonce = blockDTO.getNonce();
        size += calculateLongTextSize(nonce);

        List<TransactionDTO> transactionDtoList = blockDTO.getTransactionDtoList();
        for(TransactionDTO transactionDTO:transactionDtoList){
            size += calculateTransactionTextSize(transactionDTO);
        }
        return size;
    }
    public static long calculateTransactionTextSize(Transaction transaction) {
        return calculateTransactionTextSize(Model2DtoTool.transaction2TransactionDTO(transaction));
    }
    public static long calculateTransactionTextSize(TransactionDTO transactionDTO) {
        long size = 0;
        List<TransactionInputDTO> transactionInputDtoList = transactionDTO.getTransactionInputDtoList();
        size += calculateTransactionInputTextSize(transactionInputDtoList);
        List<TransactionOutputDTO> transactionOutputDtoList = transactionDTO.getTransactionOutputDtoList();
        size += calculateTransactionOutputTextSize(transactionOutputDtoList);
        return size;
    }
    private static long calculateTransactionOutputTextSize(List<TransactionOutputDTO> transactionOutputDtoList) {
        long size = 0;
        if(transactionOutputDtoList == null || transactionOutputDtoList.size()==0){
            return size;
        }
        for(TransactionOutputDTO transactionOutputDTO:transactionOutputDtoList){
            size += calculateTransactionOutputTextSize(transactionOutputDTO);
        }
        return size;
    }
    private static long calculateTransactionOutputTextSize(TransactionOutputDTO transactionOutputDTO) {
        long size = 0;
        OutputScriptDTO outputScriptDTO = transactionOutputDTO.getOutputScriptDTO();
        size += calculateScriptTextSize(outputScriptDTO);
        long value = transactionOutputDTO.getValue();
        size += calculateLongTextSize(value);
        return size;
    }
    private static long calculateTransactionOutputTextSize(UnspendTransactionOutputDTO unspendTransactionOutputDTO) {
        long size = 0;
        String address = unspendTransactionOutputDTO.getTransactionHash();
        size += address.length();
        long value = unspendTransactionOutputDTO.getTransactionOutputIndex();
        size += calculateLongTextSize(value);
        return size;
    }
    private static long calculateTransactionInputTextSize(List<TransactionInputDTO> inputs) {
        long size = 0;
        if(inputs == null || inputs.size()==0){
            return size;
        }
        for(TransactionInputDTO transactionInputDTO:inputs){
            size += calculateTransactionInputTextSize(transactionInputDTO);
        }
        return size;
    }
    private static long calculateTransactionInputTextSize(TransactionInputDTO input) {
        long size = 0;
        if(input == null){
            return size;
        }
        UnspendTransactionOutputDTO unspendTransactionOutputDTO = input.getUnspendTransactionOutputDTO();
        size += calculateTransactionOutputTextSize(unspendTransactionOutputDTO);
        InputScriptDTO inputScriptDTO = input.getInputScriptDTO();
        size += calculateScriptTextSize(inputScriptDTO);
        return size;
    }
    private static long calculateScriptTextSize(ScriptDTO script) {
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
            return true;
        }else {
            logger.debug("交易数据异常：不能识别的交易的类型。");
            return false;
        }
    }

    /**
     * 校验脚本操作码、操作数的存储容量
     * 校验脚本的存储容量
     */
    public static boolean isScriptStorageCapacityLegal(ScriptDTO scriptDTO) {
        for(int i=0;i<scriptDTO.size();i++){
            String operationCode = scriptDTO.get(i);
            byte[] bytesOperationCode = HexUtil.hexStringToBytes(operationCode);
            if(Arrays.equals(OperationCodeEnum.OP_DUP.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_HASH160.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_CHECKSIG.getCode(),bytesOperationCode)){

            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA128.getCode(),bytesOperationCode)){
                String operationData = scriptDTO.get(++i);
                byte[] bytesOperationData = HexUtil.hexStringToBytes(operationData);
                if(bytesOperationData.length > 128){
                    return false;
                }
            }else {
                return false;
            }
        }
        if(calculateScriptTextSize(scriptDTO) > GlobalSetting.ScriptConstant.SCRIPT_TEXT_MAX_SIZE){
            logger.debug("交易校验失败：交易输出脚本所占存储空间超出限制。");
            return false;
        }
        return true;
    }
}
