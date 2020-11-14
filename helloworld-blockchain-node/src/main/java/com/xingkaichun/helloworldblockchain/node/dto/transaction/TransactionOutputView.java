package com.xingkaichun.helloworldblockchain.node.dto.transaction;

public class TransactionOutputView {
    private String address;
    private long value;
    private String outputScript;
    private String transactionHash;
    private long transactionOutputIndex;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public long getTransactionOutputIndex() {
        return transactionOutputIndex;
    }

    public void setTransactionOutputIndex(long transactionOutputIndex) {
        this.transactionOutputIndex = transactionOutputIndex;
    }
}
