package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.core.model.transaction.*;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * dto转model工具
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class Dto2ModelTool {

    public static Block blockDto2Block(BlockchainDatabase blockchainDataBase, BlockDTO blockDTO) {
        //求上一个区块的hash
        String previousBlockHash = blockDTO.getPreviousBlockHash();
        Block previousBlock = blockchainDataBase.queryBlockByBlockHash(previousBlockHash);

        Block block = new Block();
        block.setTimestamp(blockDTO.getTimestamp());
        block.setPreviousBlockHash(previousBlockHash);
        block.setNonce(blockDTO.getNonce());

        long blockHeight = previousBlock==null? GlobalSetting.GenesisBlock.HEIGHT+1:previousBlock.getHeight()+1;
        block.setHeight(blockHeight);

        List<Transaction> transactionList = transactionDto2Transaction(blockchainDataBase,blockDTO.getTransactionDtoList());
        block.setTransactions(transactionList);

        String merkleTreeRoot = BlockTool.calculateBlockMerkleTreeRoot(block);
        block.setMerkleTreeRoot(merkleTreeRoot);

        block.setHash(BlockTool.calculateBlockHash(block));
        //简单校验hash的难度 构造能满足共识的hash很难
        if(!blockchainDataBase.getConsensus().isReachConsensus(blockchainDataBase,block)){
            throw new RuntimeException();
        }
        return block;
    }

    private static List<Transaction> transactionDto2Transaction(BlockchainDatabase blockchainDataBase, List<TransactionDTO> transactionDtoList) {
        List<Transaction> transactionList = new ArrayList<>();
        if(transactionDtoList != null){
            for(TransactionDTO transactionDTO:transactionDtoList){
                Transaction transaction = transactionDto2Transaction(blockchainDataBase,transactionDTO);
                transactionList.add(transaction);
            }
        }
        return transactionList;
    }

    public static Transaction transactionDto2Transaction(BlockchainDatabase blockchainDataBase, TransactionDTO transactionDTO) {
        List<TransactionInput> inputs = new ArrayList<>();
        List<TransactionInputDTO> transactionInputDtoList = transactionDTO.getTransactionInputDtoList();
        if(transactionInputDtoList != null){
            for (TransactionInputDTO transactionInputDTO:transactionInputDtoList){
                UnspendTransactionOutputDTO unspendTransactionOutputDto = transactionInputDTO.getUnspendTransactionOutputDTO();
                TransactionOutputId transactionOutputId = new TransactionOutputId();
                transactionOutputId.setTransactionHash(unspendTransactionOutputDto.getTransactionHash());
                transactionOutputId.setTransactionOutputIndex(unspendTransactionOutputDto.getTransactionOutputIndex());
                TransactionOutput unspendTransactionOutput = blockchainDataBase.queryUnspendTransactionOutputByTransactionOutputId(transactionOutputId);
                if(unspendTransactionOutput == null){
                    throw new ClassCastException("UnspendTransactionOutput不应该是null。");
                }
                TransactionInput transactionInput = new TransactionInput();
                transactionInput.setUnspendTransactionOutput(TransactionTool.transactionOutput2UnspendTransactionOutput(unspendTransactionOutput));
                transactionInput.setInputScript(inputScriptDto2InputScript(transactionInputDTO.getInputScriptDTO()));
                inputs.add(transactionInput);
            }
        }

        List<TransactionOutput> outputs = new ArrayList<>();
        List<TransactionOutputDTO> dtoOutputs = transactionDTO.getTransactionOutputDtoList();
        if(dtoOutputs != null){
            for(TransactionOutputDTO transactionOutputDTO:dtoOutputs){
                TransactionOutput transactionOutput = transactionOutputDto2TransactionOutput(transactionOutputDTO);
                outputs.add(transactionOutput);
            }
        }

        Transaction transaction = new Transaction();
        TransactionType transactionType = obtainTransactionDTO(transactionDTO);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionHash(TransactionTool.calculateTransactionHash(transactionDTO));
        transaction.setInputs(inputs);
        transaction.setOutputs(outputs);
        return transaction;
    }

    public static TransactionOutput transactionOutputDto2TransactionOutput(TransactionOutputDTO transactionOutputDTO) {
        TransactionOutput transactionOutput = new TransactionOutput();
        String publicKeyHash = StackBasedVirtualMachine.getPublicKeyHashByPayToPublicKeyHashOutputScript(transactionOutputDTO.getOutputScriptDTO());
        String address = AccountUtil.addressFromPublicKeyHash(publicKeyHash);
        transactionOutput.setAddress(address);
        transactionOutput.setValue(transactionOutputDTO.getValue());
        transactionOutput.setOutputScript(outputScriptDto2OutputScript(transactionOutputDTO.getOutputScriptDTO()));
        return transactionOutput;
    }

    private static TransactionType obtainTransactionDTO(TransactionDTO transactionDTO) {
        if(transactionDTO.getTransactionInputDtoList() == null || transactionDTO.getTransactionInputDtoList().size()==0){
            return TransactionType.COINBASE;
        }
        return TransactionType.NORMAL;
    }

    private static OutputScript outputScriptDto2OutputScript(OutputScriptDTO outputScriptDTO) {
        if(outputScriptDTO == null){
            return null;
        }
        OutputScript outputScript = new OutputScript();
        outputScript.addAll(outputScriptDTO);
        return outputScript;
    }

    private static InputScript inputScriptDto2InputScript(InputScriptDTO inputScriptDTO) {
        if(inputScriptDTO == null){
            return null;
        }
        InputScript inputScript = new InputScript();
        inputScript.addAll(inputScriptDTO);
        return inputScript;
    }
}
