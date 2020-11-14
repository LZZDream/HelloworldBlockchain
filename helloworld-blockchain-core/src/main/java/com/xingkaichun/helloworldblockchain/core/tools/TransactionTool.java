package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.common.primitives.Bytes;
import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptExecuteResult;
import com.xingkaichun.helloworldblockchain.core.model.transaction.*;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.crypto.SHA256Util;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionInputDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionOutputDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.UnspendTransactionOutputDTO;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Transaction工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class TransactionTool {

    private static final Logger logger = LoggerFactory.getLogger(TransactionTool.class);

    /**
     * 交易输入总额
     */
    public static long getInputsValue(Transaction transaction) {
        return getInputsValue(transaction.getInputs());
    }
    /**
     * 交易输入总额
     */
    public static long getInputsValue(List<TransactionInput> inputs) {
        long total = 0;
        if(inputs != null){
            for(TransactionInput input : inputs) {
                total += input.getUnspendTransactionOutput().getValue();
            }
        }
        return total;
    }



    /**
     * 交易输出总额
     */
    public static long getOutputsValue(Transaction transaction) {
        return getOutputsValue(transaction.getOutputs());
    }
    /**
     * 交易输出总额
     */
    public static long getOutputsValue(List<TransactionOutput> outputs) {
        long total = 0;
        if(outputs != null){
            for(TransactionOutput o : outputs) {
                total += o.getValue();
            }
        }
        return total;
    }



    /**
     * 获取用于签名的交易数据
     */
    public static String getSignatureData(Transaction transaction) {
        String data = transaction.getTransactionHash();
        return data;
    }

    /**
     * 交易签名
     */
    public static String signature(String privateKey, Transaction transaction) {
        byte[] bytesMessage = HexUtil.hexStringToBytes(getSignatureData(transaction));
        byte[] bytesSignature = AccountUtil.signature(privateKey,bytesMessage);
        String stringSignature = HexUtil.bytesToHexString(bytesSignature);
        return stringSignature;
    }

    /**
     * 验证脚本
     */
    public static boolean verifyScript(Transaction transaction) {
        try{
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size()!=0){
                for(TransactionInput transactionInput:inputs){
                    Script payToClassicAddressScript = StackBasedVirtualMachine.createPayToClassicAddressScript(transactionInput.getInputScript(),transactionInput.getUnspendTransactionOutput().getOutputScript());
                    StackBasedVirtualMachine stackBasedVirtualMachine = new StackBasedVirtualMachine();
                    ScriptExecuteResult scriptExecuteResult = stackBasedVirtualMachine.executeScript(transaction,payToClassicAddressScript);
                    if(scriptExecuteResult.size()!=1 || !Boolean.valueOf(scriptExecuteResult.pop())){
                        return false;
                    }
                }
            }
        }catch (Exception e){
            logger.debug("交易校验失败：交易脚本钥匙解锁交易脚本锁异常。",e);
            return false;
        }
        return true;
    }

    /**
     * 计算交易哈希
     */
    public static String calculateTransactionHash(Transaction transaction){
        return calculateTransactionHash(Model2DtoTool.transaction2TransactionDTO(transaction));
    }

    /**
     * 计算交易哈希
     */
    public static String calculateTransactionHash(TransactionDTO transactionDTO){
        byte[] bytesTransaction = bytesTransaction(transactionDTO);
        byte[] sha256Digest = SHA256Util.digestTwice(bytesTransaction);
        return HexUtil.bytesToHexString(sha256Digest);
    }

    /**
     * 字节型脚本
     */
    public static byte[] bytesTransaction(TransactionDTO transactionDTO) {
        List<byte[]> bytesUnspendTransactionOutputList = new ArrayList<>();
        List<TransactionInputDTO> inputs = transactionDTO.getTransactionInputDtoList();
        if(inputs != null){
            for(TransactionInputDTO transactionInputDTO:inputs){
                UnspendTransactionOutputDTO unspendTransactionOutputDto = transactionInputDTO.getUnspendTransactionOutputDTO();
                byte[] bytesTransactionHash = HexUtil.hexStringToBytes(unspendTransactionOutputDto.getTransactionHash());
                byte[] bytesTransactionOutputIndex = ByteUtil.longToBytes8(unspendTransactionOutputDto.getTransactionOutputIndex());
                byte[] bytesUnspendTransactionOutput = Bytes.concat(ByteUtil.concatLengthBytes(bytesTransactionHash),
                        ByteUtil.concatLengthBytes(bytesTransactionOutputIndex));
                bytesUnspendTransactionOutputList.add(bytesUnspendTransactionOutput);
            }
        }

        List<byte[]> bytesTransactionOutputList = new ArrayList<>();
        List<TransactionOutputDTO> outputs = transactionDTO.getTransactionOutputDtoList();
        if(outputs != null){
            for(TransactionOutputDTO transactionOutputDTO:outputs){
                byte[] bytesValue = ByteUtil.longToBytes8(transactionOutputDTO.getValue());
                byte[] bytesOutputScript = ScriptTool.bytesScript(transactionOutputDTO.getOutputScriptDTO());
                byte[] bytesTransactionOutput = Bytes.concat(ByteUtil.concatLengthBytes(bytesValue),
                        ByteUtil.concatLengthBytes(bytesOutputScript));
                bytesTransactionOutputList.add(bytesTransactionOutput);
            }
        }

        byte[] data = Bytes.concat(ByteUtil.concatLengthBytes(bytesUnspendTransactionOutputList),
                ByteUtil.concatLengthBytes(bytesTransactionOutputList));
        return data;
    }

    /**
     * 交易中的金额是否符合系统的约束
     */
    public static boolean isTransactionAmountLegal(Transaction transaction) {
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for(TransactionOutput output:outputs){
                if(!isTransactionAmountLegal(output.getValue())){
                    logger.debug("交易金额不合法");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 是否是一个合法的交易金额：这里用于限制交易金额的最大值、最小值、小数保留位置
     */
    public static boolean isTransactionAmountLegal(long transactionAmount) {
        try {
            //交易金额不能小于等于0
            if(transactionAmount <= 0){
                logger.debug("交易金额不合法：交易金额不能小于等于0");
                return false;
            }
            //交易金额最小值不需要校验，假设值不正确，业务逻辑通过不了。

            //交易金额最大值不需要校验，假设值不正确，业务逻辑通过不了
            return true;
        } catch (Exception e) {
            logger.debug("校验金额方法出现异常，请检查。",e);
            return false;
        }
    }

    /**
     * 校验激励
     */
    public static boolean isIncentiveRight(long targetMinerReward, Transaction transaction) {
        if(transaction.getTransactionType() != TransactionType.COINBASE){
            logger.debug("区块数据异常，区块中的第一笔交易应当是挖矿奖励交易。");
            return false;
        }
        List<TransactionInput> inputs = transaction.getInputs();
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(inputs != null && inputs.size()!=0){
            logger.debug("区块数据异常，挖矿奖励交易交易输入应当是空。");
            return false;
        }
        if(outputs == null || outputs.size()!=1){
            logger.debug("区块数据异常，挖矿奖励交易只能有一个交易输出。");
            return false;
        }
        if(targetMinerReward < outputs.get(0).getValue()){
            logger.debug("挖矿奖励数据异常，挖矿奖励金额大于系统核算奖励金额。");
            return false;
        }
        return true;
    }

    /**
     * 是否存在重复的交易输入
     */
    public static boolean isExistDuplicateTransactionInput(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs == null || inputs.size()==0){
            return false;
        }
        Set<String> transactionOutputIdSet = new HashSet<>();
        for(TransactionInput transactionInput : inputs) {
            TransactionOutput unspendTransactionOutput = transactionInput.getUnspendTransactionOutput();
            String transactionOutputId = unspendTransactionOutput.getTransactionOutputId();
            if(transactionOutputIdSet.contains(transactionOutputId)){
                return true;
            }
            transactionOutputIdSet.add(transactionOutputId);
        }
        return false;
    }

    /**
     * 交易输入必须要大于交易输出
     */
    public static boolean isTransactionInputsGreatEqualThanOutputsRight(Transaction transaction) {
        long inputsValue = TransactionTool.getInputsValue(transaction);
        long outputsValue = TransactionTool.getOutputsValue(transaction);
        if(inputsValue < outputsValue) {
            logger.debug("交易校验失败：交易的输入必须大于等于交易的输出。不合法的交易。");
            return false;
        }
        return true;
    }

    public static UnspendTransactionOutput transactionOutput2UnspendTransactionOutput(TransactionOutput transactionOutput) {
        //UnspendTransactionOutput是TransactionOutput子类，且没有其它的属性才可以这样转换。
        String json = new Gson().toJson(transactionOutput);
        UnspendTransactionOutput unspendTransactionOutput = new Gson().fromJson(json,UnspendTransactionOutput.class);
        return unspendTransactionOutput;
    }

    public static long getTransactionInputCount(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        long transactionInputCount = inputs==null?0:inputs.size();
        return transactionInputCount;
    }

    public static long getTransactionOutputCount(Transaction transaction) {
        List<TransactionOutput> outputs = transaction.getOutputs();
        long transactionOutputCount = outputs==null?0:outputs.size();
        return transactionOutputCount;
    }

    public static long calculateTransactionFee(Transaction transaction) {
        long transactionInputCount = getTransactionInputCount(transaction);
        long transactionOutputCount = getTransactionOutputCount(transaction);
        if(transactionInputCount <= 0){
            return 0;
        }
        return transactionInputCount-transactionOutputCount;
    }
}
