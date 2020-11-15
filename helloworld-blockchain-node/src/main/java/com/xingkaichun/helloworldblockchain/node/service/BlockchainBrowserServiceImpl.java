package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.*;
import com.xingkaichun.helloworldblockchain.core.tools.ScriptTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCore;
import com.xingkaichun.helloworldblockchain.node.dto.transaction.TransactionInputView;
import com.xingkaichun.helloworldblockchain.node.dto.transaction.TransactionOutputDetailView;
import com.xingkaichun.helloworldblockchain.node.dto.transaction.TransactionOutputView;
import com.xingkaichun.helloworldblockchain.node.dto.transaction.TransactionView;
import com.xingkaichun.helloworldblockchain.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
@Service
public class BlockchainBrowserServiceImpl implements BlockchainBrowserService {

    @Autowired
    private NetBlockchainCore netBlockchainCore;

    @Override
    public TransactionOutputDetailView queryTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId) {
        TransactionOutput transactionOutput = getBlockchainCore().getBlockchainDataBase().queryTransactionOutputByTransactionOutputId(transactionOutputId);
        if(transactionOutput == null){
            return null;
        }

        TransactionOutputDetailView transactionOutputDetailView = new TransactionOutputDetailView();
        transactionOutputDetailView.setBlockHeight(transactionOutput.getBlockHeight());
        transactionOutputDetailView.setBlockHash(transactionOutput.getBlockHash());
        transactionOutputDetailView.setTransactionHash(transactionOutput.getTransactionHash());
        transactionOutputDetailView.setValue(transactionOutput.getValue());
        transactionOutputDetailView.setOutputScript(ScriptTool.toString(transactionOutput.getOutputScript()));
        transactionOutputDetailView.setTransactionOutputIndex(transactionOutput.getTransactionOutputIndex());
        transactionOutputId.setTransactionHash(transactionOutput.getTransactionHash());
        transactionOutputId.setTransactionOutputIndex(transactionOutput.getTransactionOutputIndex());
        TransactionOutput transactionOutputTemp = getBlockchainCore().getBlockchainDataBase().queryUnspendTransactionOutputByTransactionOutputId(transactionOutputId);
        transactionOutputDetailView.setSpend(transactionOutputTemp==null);

        //来源
        Transaction inputTransaction = getBlockchainCore().getBlockchainDataBase().queryTransactionByTransactionHash(transactionOutputId.getTransactionHash());
        TransactionView inputTransactionView = queryTransactionByTransactionHash(inputTransaction.getTransactionHash());

        //去向
        TransactionView outputTransactionView = null;
        if(transactionOutputTemp==null){
            String transactionHash = getBlockchainCore().getBlockchainDataBase().queryToTransactionHashByTransactionOutputId(transactionOutputId);
            Transaction outputTransaction = getBlockchainCore().getBlockchainDataBase().queryTransactionByTransactionHash(transactionHash);
            outputTransactionView = queryTransactionByTransactionHash(transactionHash);

            List<TransactionInput> inputs = outputTransaction.getInputs();
            if(inputs != null){
                for(TransactionInput transactionInput:inputs){
                    UnspendTransactionOutput unspendTransactionOutput = transactionInput.getUnspendTransactionOutput();
                    if(transactionOutput.getTransactionHash().equals(unspendTransactionOutput.getTransactionHash()) &&
                            transactionOutput.getTransactionOutputIndex()==unspendTransactionOutput.getTransactionOutputIndex()){
                        transactionOutputDetailView.setInputScript(ScriptTool.toString(transactionInput.getInputScript()));
                        break;
                    }
                }
            }
        }
        transactionOutputDetailView.setInputTransaction(inputTransactionView);
        transactionOutputDetailView.setOutputTransaction(outputTransactionView);
        return transactionOutputDetailView;
    }

    @Override
    public List<TransactionOutputDetailView> queryTransactionOutputListByAddress(String address, long from, long size) {
        List<TransactionOutput> utxoList = getBlockchainCore().queryTransactionOutputListByAddress(address,from,size);
        if(utxoList == null){
            return null;
        }
        List<TransactionOutputDetailView> transactionOutputDetailViewList = new ArrayList<>();
        for(TransactionOutput transactionOutput:utxoList){
            TransactionOutputDetailView transactionOutputDetailView = queryTransactionOutputByTransactionOutputId(transactionOutput);
            transactionOutputDetailViewList.add(transactionOutputDetailView);
        }
        return transactionOutputDetailViewList;
    }

    @Override
    public List<TransactionOutputDetailView> queryUnspendTransactionOutputListByAddress(String address, long from, long size) {
        List<TransactionOutput> utxoList = getBlockchainCore().queryUnspendTransactionOutputListByAddress(address,from,size);
        if(utxoList == null){
            return null;
        }
        List<TransactionOutputDetailView> transactionOutputDetailViewList = new ArrayList<>();
        for(TransactionOutput transactionOutput:utxoList){
            TransactionOutputDetailView transactionOutputDetailView = queryTransactionOutputByTransactionOutputId(transactionOutput);
            transactionOutputDetailViewList.add(transactionOutputDetailView);
        }
        return transactionOutputDetailViewList;
    }

    @Override
    public List<TransactionView> queryTransactionListByAddress(String address, long from, long size) {
        List<Transaction> transactionList = getBlockchainCore().queryTransactionListByAddress(address,from,size);
        if(transactionList == null){
            return null;
        }
        List<TransactionView> transactionViewList = new ArrayList<>();
        for(Transaction transaction:transactionList){
            TransactionView transactionView = queryTransactionByTransactionHash(transaction.getTransactionHash());
            transactionViewList.add(transactionView);
        }
        return transactionViewList;
    }

    @Override
    public List<TransactionView> queryTransactionListByBlockHashTransactionHeight(String blockHash, long from, long size) {
        Block block = getBlockchainCore().queryBlockByBlockHash(blockHash);
        long fromUpdate = block.getStartTransactionIndexInBlockchain() + from -1 ;
        if(from+size-1 > block.getTransactions().size()){
            size = block.getTransactions().size() - from + 1;
        }
        List<Transaction> transactionList = getBlockchainCore().queryTransactionListByTransactionHeight(fromUpdate,size);
        List<TransactionView> transactionViewList = new ArrayList<>();
        for(Transaction transaction:transactionList){
            TransactionView transactionView = queryTransactionByTransactionHash(transaction.getTransactionHash());
            transactionViewList.add(transactionView);
        }
        return transactionViewList;
    }



    @Override
    public TransactionView queryTransactionByTransactionHash(String transactionHash) {
        Transaction transaction = getBlockchainCore().queryTransactionByTransactionHash(transactionHash);
        if(transaction == null){
            return null;
        }
        long blockchainHeight = getBlockchainCore().queryBlockchainHeight();
        Block block = getBlockchainCore().queryBlockByBlockHeight(transaction.getBlockHeight());
        TransactionView transactionView = new TransactionView();

        transactionView.setTransactionHash(transaction.getTransactionHash());
        transactionView.setBlockHeight(transaction.getBlockHeight());
        transactionView.setConfirmCount(blockchainHeight-block.getHeight());
        transactionView.setBlockTime(DateUtil.timestamp2ChinaTime(block.getTimestamp()));

        transactionView.setTransactionFee(TransactionTool.calculateTransactionFee(transaction));
        transactionView.setTransactionType(transaction.getTransactionType().name());
        transactionView.setTransactionInputCount(TransactionTool.getTransactionInputCount(transaction));
        transactionView.setTransactionOutputCount(TransactionTool.getTransactionOutputCount(transaction));
        transactionView.setTransactionInputValues(TransactionTool.getInputsValue(transaction));
        transactionView.setTransactionOutputValues(TransactionTool.getOutputsValue(transaction));

        List<TransactionInput> inputs = transaction.getInputs();
        List<TransactionInputView> transactionInputViewList = new ArrayList<>();
        if(inputs != null){
            for(TransactionInput transactionInput:inputs){
                TransactionInputView transactionInputView = new TransactionInputView();
                transactionInputView.setAddress(transactionInput.getUnspendTransactionOutput().getAddress());
                transactionInputView.setValue(transactionInput.getUnspendTransactionOutput().getValue());
                transactionInputView.setInputScript(ScriptTool.toString(transactionInput.getInputScript()));
                transactionInputView.setTransactionHash(transactionInput.getUnspendTransactionOutput().getTransactionHash());
                transactionInputView.setTransactionOutputIndex(transactionInput.getUnspendTransactionOutput().getTransactionOutputIndex());
                transactionInputViewList.add(transactionInputView);
            }
        }

        List<TransactionOutput> outputs = transaction.getOutputs();
        List<TransactionOutputView> transactionOutputViewList = new ArrayList<>();
        if(outputs != null){
            for(TransactionOutput transactionOutput:outputs){
                TransactionOutputView transactionOutputView = new TransactionOutputView();
                transactionOutputView.setAddress(transactionOutput.getAddress());
                transactionOutputView.setValue(transactionOutput.getValue());
                transactionOutputView.setOutputScript(ScriptTool.toString(transactionOutput.getOutputScript()));
                transactionOutputView.setTransactionHash(transactionOutput.getTransactionHash());
                transactionOutputView.setTransactionOutputIndex(transactionOutput.getTransactionOutputIndex());
                transactionOutputViewList.add(transactionOutputView);
            }
        }

        transactionView.setTransactionInputViewList(transactionInputViewList);
        transactionView.setTransactionOutputViewList(transactionOutputViewList);

        if(transactionInputViewList != null){
            List<String> inputScriptList = new ArrayList<>();
            for (TransactionInputView transactionInputView : transactionInputViewList){
                inputScriptList.add(transactionInputView.getInputScript());
            }
            transactionView.setInputScriptList(inputScriptList);
        }
        if(transactionOutputViewList != null){
            List<String> outputScriptList = new ArrayList<>();
            for (TransactionOutputView transactionOutputView : transactionOutputViewList){
                outputScriptList.add(transactionOutputView.getOutputScript());
            }
            transactionView.setOutputScriptList(outputScriptList);
        }
        return transactionView;
    }

    @Override
    public List<TransactionOutputDetailView> querySpendTransactionOutputListByAddress(String address, long from, long size) {
        List<TransactionOutput> stxoList = getBlockchainCore().querySpendTransactionOutputListByAddress(address,from,size);
        if(stxoList == null){
            return null;
        }
        List<TransactionOutputDetailView> transactionOutputDetailViewList = new ArrayList<>();
        for(TransactionOutput transactionOutput:stxoList){
            TransactionOutputDetailView transactionOutputDetailView = queryTransactionOutputByTransactionOutputId(transactionOutput);
            transactionOutputDetailViewList.add(transactionOutputDetailView);
        }
        return transactionOutputDetailViewList;
    }

    private BlockchainCore getBlockchainCore(){
        return netBlockchainCore.getBlockchainCore();
    }
}
