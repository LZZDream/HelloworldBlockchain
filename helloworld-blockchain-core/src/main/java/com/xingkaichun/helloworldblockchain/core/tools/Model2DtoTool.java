package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;

import java.util.ArrayList;
import java.util.List;

/**
 * model转dto工具
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class Model2DtoTool {

    private static Gson gson = new Gson();


    public static BlockDTO block2BlockDTO(Block block) {
        if(block == null){
            return null;
        }
        List<TransactionDTO> transactionDtoList = new ArrayList<>();
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                TransactionDTO transactionDTO = transaction2TransactionDTO(transaction);
                transactionDtoList.add(transactionDTO);
            }
        }

        BlockDTO blockDTO = new BlockDTO();
        blockDTO.setTimestamp(block.getTimestamp());
        blockDTO.setPreviousBlockHash(block.getPreviousBlockHash());
        blockDTO.setTransactionDtoList(transactionDtoList);
        blockDTO.setNonce(block.getNonce());
        return blockDTO;
    }

    public static TransactionDTO transaction2TransactionDTO(Transaction transaction) {
        List<TransactionInputDTO> inputs = new ArrayList<>();
        List<TransactionInput> transactionInputList = transaction.getInputs();
        if(transactionInputList!=null){
            for (TransactionInput transactionInput:transactionInputList){
                UnspendTransactionOutputDTO unspendTransactionOutputDto = transactionOutput2UnspendTransactionOutputDto(transactionInput.getUnspendTransactionOutput());

                TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
                transactionInputDTO.setUnspendTransactionOutputDTO(unspendTransactionOutputDto);
                transactionInputDTO.setInputScriptDTO(inputScript2InputScriptDTO(transactionInput.getInputScript()));
                inputs.add(transactionInputDTO);
            }
        }

        List<TransactionOutputDTO> outputs = new ArrayList<>();
        List<TransactionOutput> transactionOutputList = transaction.getOutputs();
        if(transactionOutputList!=null){
            for(TransactionOutput transactionOutput:transactionOutputList){
                TransactionOutputDTO transactionOutputDTO = transactionOutput2TransactionOutputDTO(transactionOutput);
                outputs.add(transactionOutputDTO);
            }
        }

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionInputDtoList(inputs);
        transactionDTO.setTransactionOutputDtoList(outputs);
        return transactionDTO;
    }

    public static InputScriptDTO inputScript2InputScriptDTO(InputScript inputScript) {
        InputScriptDTO inputScriptDTO = new InputScriptDTO();
        inputScriptDTO.addAll(inputScript);
        return inputScriptDTO;
    }

    public static OutputScriptDTO outputScript2OutputScriptDTO(OutputScript outputScript) {
        OutputScriptDTO outputScriptDTO = new OutputScriptDTO();
        outputScriptDTO.addAll(outputScript);
        return outputScriptDTO;
    }

    public static TransactionOutputDTO transactionOutput2TransactionOutputDTO(TransactionOutput transactionOutput) {
        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        transactionOutputDTO.setValue(transactionOutput.getValue());
        transactionOutputDTO.setOutputScriptDTO(outputScript2OutputScriptDTO(transactionOutput.getOutputScript()));
        return transactionOutputDTO;
    }

    public static UnspendTransactionOutputDTO transactionOutput2UnspendTransactionOutputDto(TransactionOutput transactionOutput) {
        UnspendTransactionOutputDTO unspendTransactionOutputDto = new UnspendTransactionOutputDTO();
        unspendTransactionOutputDto.setTransactionHash(transactionOutput.getTransactionHash());
        unspendTransactionOutputDto.setTransactionOutputIndex(transactionOutput.getTransactionOutputIndex());
        return unspendTransactionOutputDto;
    }

    public static String signature(TransactionDTO transactionDTO, String privateKey) {
        byte[] bytesMessage = HexUtil.hexStringToBytes(signatureData(transactionDTO));
        byte[] bytesSignature = AccountUtil.signature(privateKey,bytesMessage);
        String stringSignature = HexUtil.bytesToHexString(bytesSignature);
        return stringSignature;
    }

    public static String signatureData(TransactionDTO transactionDTO) {
        String data = TransactionTool.calculateTransactionHash(transactionDTO);
        return data;
    }

    public static String encode(BlockDTO blockDTO) {
        return gson.toJson(blockDTO);
    }

    public static BlockDTO decodeToBlockDTO(String stringBlockDTO) {
        return gson.fromJson(stringBlockDTO,BlockDTO.class);
    }
}
