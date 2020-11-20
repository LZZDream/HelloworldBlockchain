package com.xingkaichun.helloworldblockchain.node.dto.transaction;

import java.util.List;

/**
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class QueryTransactionListByAddressResponse {

    private List<TransactionView> transactionViewList;




    //region get set

    public List<TransactionView> getTransactionViewList() {
        return transactionViewList;
    }

    public void setTransactionViewList(List<TransactionView> transactionViewList) {
        this.transactionViewList = transactionViewList;
    }


    //endregion
}
