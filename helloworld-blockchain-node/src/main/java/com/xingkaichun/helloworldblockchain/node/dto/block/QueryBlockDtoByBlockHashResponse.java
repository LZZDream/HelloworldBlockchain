package com.xingkaichun.helloworldblockchain.node.dto.block;

import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class QueryBlockDtoByBlockHashResponse {

    private BlockDto blockDto ;




    //region get set

    public BlockDto getBlockDto() {
        return blockDto;
    }

    public void setBlockDto(BlockDto blockDto) {
        this.blockDto = blockDto;
    }


    //endregion



    public static class BlockDto {
        private long height;
        private long confirmCount;
        private String blockSize;
        private long transactionCount;
        private String time;
        private long minerIncentiveValue;

        private String minerDifficulty;
        private String nonce;
        private String hash;
        private String previousBlockHash;
        private String nextBlockHash;
        private String merkleTreeRoot;

        public long getHeight() {
            return height;
        }

        public void setHeight(long height) {
            this.height = height;
        }

        public long getConfirmCount() {
            return confirmCount;
        }

        public void setConfirmCount(long confirmCount) {
            this.confirmCount = confirmCount;
        }

        public String getBlockSize() {
            return blockSize;
        }

        public void setBlockSize(String blockSize) {
            this.blockSize = blockSize;
        }

        public long getTransactionCount() {
            return transactionCount;
        }

        public void setTransactionCount(long transactionCount) {
            this.transactionCount = transactionCount;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public long getMinerIncentiveValue() {
            return minerIncentiveValue;
        }

        public void setMinerIncentiveValue(long minerIncentiveValue) {
            this.minerIncentiveValue = minerIncentiveValue;
        }

        public String getMinerDifficulty() {
            return minerDifficulty;
        }

        public void setMinerDifficulty(String minerDifficulty) {
            this.minerDifficulty = minerDifficulty;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getPreviousBlockHash() {
            return previousBlockHash;
        }

        public void setPreviousBlockHash(String previousBlockHash) {
            this.previousBlockHash = previousBlockHash;
        }

        public String getNextBlockHash() {
            return nextBlockHash;
        }

        public void setNextBlockHash(String nextBlockHash) {
            this.nextBlockHash = nextBlockHash;
        }

        public String getMerkleTreeRoot() {
            return merkleTreeRoot;
        }

        public void setMerkleTreeRoot(String merkleTreeRoot) {
            this.merkleTreeRoot = merkleTreeRoot;
        }

    }



    public static class TransactionDto {
        private String transactionHash;
        private long transactionFee;
        private String time;
        private String transactionType;
        private long transactionInputValues;
        private long transactionOutputValues;
        private List<TransactionInputDto> transactionInputDtoList;
        private List<TransactionOutputDto> transactionOutputDtoList;


        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTransactionHash() {
            return transactionHash;
        }

        public void setTransactionHash(String transactionHash) {
            this.transactionHash = transactionHash;
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

        public List<TransactionInputDto> getTransactionInputDtoList() {
            return transactionInputDtoList;
        }

        public void setTransactionInputDtoList(List<TransactionInputDto> transactionInputDtoList) {
            this.transactionInputDtoList = transactionInputDtoList;
        }

        public List<TransactionOutputDto> getTransactionOutputDtoList() {
            return transactionOutputDtoList;
        }

        public void setTransactionOutputDtoList(List<TransactionOutputDto> transactionOutputDtoList) {
            this.transactionOutputDtoList = transactionOutputDtoList;
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
    }

    public static class TransactionInputDto {
        private String address;
        private long value;

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
    }

    public static class TransactionOutputDto {
        private String address;
        private long value;

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
    }
}
