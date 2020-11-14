package com.xingkaichun.helloworldblockchain.node.dto.transaction;

import java.util.List;

public class TransactionView {
    private long blockHeight;
    private long confirmCount;
    private String transactionHash;
    private String blockTime;

    private long transactionFee;
    private String transactionType;
    private long transactionInputCount;
    private long transactionOutputCount;
    private long transactionInputValues;
    private long transactionOutputValues;

    private List<TransactionInputView> transactionInputViewList;
    private List<TransactionOutputView> transactionOutputViewList;

    private List<String> inputScriptList;
    private List<String> outputScriptList;


    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public long getConfirmCount() {
        return confirmCount;
    }

    public void setConfirmCount(long confirmCount) {
        this.confirmCount = confirmCount;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(String blockTime) {
        this.blockTime = blockTime;
    }

    public long getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(long transactionFee) {
        this.transactionFee = transactionFee;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public long getTransactionInputCount() {
        return transactionInputCount;
    }

    public void setTransactionInputCount(long transactionInputCount) {
        this.transactionInputCount = transactionInputCount;
    }

    public long getTransactionOutputCount() {
        return transactionOutputCount;
    }

    public void setTransactionOutputCount(long transactionOutputCount) {
        this.transactionOutputCount = transactionOutputCount;
    }

    public long getTransactionInputValues() {
        return transactionInputValues;
    }

    public void setTransactionInputValues(long transactionInputValues) {
        this.transactionInputValues = transactionInputValues;
    }

    public long getTransactionOutputValues() {
        return transactionOutputValues;
    }

    public void setTransactionOutputValues(long transactionOutputValues) {
        this.transactionOutputValues = transactionOutputValues;
    }

    public List<TransactionInputView> getTransactionInputViewList() {
        return transactionInputViewList;
    }

    public void setTransactionInputViewList(List<TransactionInputView> transactionInputViewList) {
        this.transactionInputViewList = transactionInputViewList;
    }

    public List<TransactionOutputView> getTransactionOutputViewList() {
        return transactionOutputViewList;
    }

    public void setTransactionOutputViewList(List<TransactionOutputView> transactionOutputViewList) {
        this.transactionOutputViewList = transactionOutputViewList;
    }

    public List<String> getInputScriptList() {
        return inputScriptList;
    }

    public void setInputScriptList(List<String> inputScriptList) {
        this.inputScriptList = inputScriptList;
    }

    public List<String> getOutputScriptList() {
        return outputScriptList;
    }

    public void setOutputScriptList(List<String> outputScriptList) {
        this.outputScriptList = outputScriptList;
    }

}
