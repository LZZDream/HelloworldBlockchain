package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;

import java.util.List;

public class WalletTool {

    public static long obtainBalance(BlockchainCore blockchainCore, String address) {
        //交易输出总金额
        long totalValue = 0;
        long from = 0;
        long size = 100;
        while(true){
            List<TransactionOutput> utxoList = blockchainCore.getBlockchainDataBase().queryUnspendTransactionOutputListByAddress(address,from,size);
            if(utxoList == null || utxoList.size()==0){
                break;
            }
            for(TransactionOutput transactionOutput:utxoList){
                totalValue += transactionOutput.getValue();
            }
            from += size;
        }
        return totalValue;
    }

    public static long obtainSpend(BlockchainCore blockchainCore, String address) {
        //交易输出总金额
        long totalValue = 0;
        long from = 0;
        long size = 100;
        while(true){
            List<TransactionOutput> stxoList = blockchainCore.getBlockchainDataBase().querySpendTransactionOutputListByAddress(address,from,size);
            if(stxoList == null || stxoList.size()==0){
                break;
            }
            for(TransactionOutput transactionOutput:stxoList){
                totalValue += transactionOutput.getValue();
            }
            from += size;
        }
        return totalValue;
    }

    public static long obtainReceipt(BlockchainCore blockchainCore, String address) {
        //交易输出总金额
        long totalValue = 0;
        long from = 0;
        long size = 100;
        while(true){
            List<TransactionOutput> txoList = blockchainCore.getBlockchainDataBase().queryTransactionOutputListByAddress(address,from,size);
            if(txoList == null || txoList.size()==0){
                break;
            }
            for(TransactionOutput transactionOutput:txoList){
                totalValue += transactionOutput.getValue();
            }
            from += size;
        }
        return totalValue;
    }
}
