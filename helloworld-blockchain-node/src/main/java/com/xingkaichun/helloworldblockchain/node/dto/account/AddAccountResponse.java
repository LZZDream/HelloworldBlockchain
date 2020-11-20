package com.xingkaichun.helloworldblockchain.node.dto.account;

import com.xingkaichun.helloworldblockchain.crypto.model.Account;

/**
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class AddAccountResponse {

    private boolean addAccountSuccess ;

    private Account account;


    public boolean isAddAccountSuccess() {
        return addAccountSuccess;
    }

    public void setAddAccountSuccess(boolean addAccountSuccess) {
        this.addAccountSuccess = addAccountSuccess;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
