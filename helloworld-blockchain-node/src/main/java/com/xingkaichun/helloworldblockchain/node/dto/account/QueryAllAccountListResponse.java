package com.xingkaichun.helloworldblockchain.node.dto.account;

import java.io.Serializable;
import java.util.List;

/**
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class QueryAllAccountListResponse {

    private long balance;
    private List<AccountDto> accountDtoList;


    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public List<AccountDto> getAccountDtoList() {
        return accountDtoList;
    }

    public void setAccountDtoList(List<AccountDto> accountDtoList) {
        this.accountDtoList = accountDtoList;
    }

    public static class AccountDto implements Serializable {

        private String privateKey;
        private String address;
        private long value;

        public AccountDto(String privateKey, String address, long value) {
            this.privateKey = privateKey;
            this.address = address;
            this.value = value;
        }

        public AccountDto() {
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

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
