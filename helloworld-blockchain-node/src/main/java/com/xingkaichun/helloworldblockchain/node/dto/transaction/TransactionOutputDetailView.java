package com.xingkaichun.helloworldblockchain.node.dto.transaction;

public class TransactionOutputDetailView {
    private long blockHeight;
    private String blockHash;
    private String transactionHash;
    private long transactionOutputIndex;
    private long value;
    private boolean isSpend;
    private String outputScript;
    private String inputScript;

    private TransactionView inputTransaction;
    private TransactionView outputTransaction;

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public boolean isSpend() {
        return isSpend;
    }

    public void setSpend(boolean spend) {
        isSpend = spend;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getOutputScript() {
        return outputScript;
    }

    public void setOutputScript(String outputScript) {
        this.outputScript = outputScript;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getInputScript() {
        return inputScript;
    }

    public void setInputScript(String inputScript) {
        this.inputScript = inputScript;
    }

    public long getTransactionOutputIndex() {
        return transactionOutputIndex;
    }

    public void setTransactionOutputIndex(long transactionOutputIndex) {
        this.transactionOutputIndex = transactionOutputIndex;
    }

    public TransactionView getInputTransaction() {
        return inputTransaction;
    }

    public void setInputTransaction(TransactionView inputTransaction) {
        this.inputTransaction = inputTransaction;
    }

    public TransactionView getOutputTransaction() {
        return outputTransaction;
    }

    public void setOutputTransaction(TransactionView outputTransaction) {
        this.outputTransaction = outputTransaction;
    }

}
