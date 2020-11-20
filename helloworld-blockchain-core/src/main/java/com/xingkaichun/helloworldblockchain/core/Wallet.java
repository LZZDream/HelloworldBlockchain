package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.crypto.model.Account;

import java.util.List;

/**
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public abstract class Wallet {

    public abstract List<Account> queryAllAccount();

    public abstract Account createAccount();

    public abstract void addAccount(Account account);

    public abstract void deleteAccountByAddress(String address);
}
