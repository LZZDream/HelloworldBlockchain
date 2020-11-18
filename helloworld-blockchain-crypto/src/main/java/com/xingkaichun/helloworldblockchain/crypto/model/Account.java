package com.xingkaichun.helloworldblockchain.crypto.model;


import java.io.Serializable;

/**
 * 字符串格式的数字货币的账户
 *
 * 通常情况下，数字货币的账户由一个非对称秘钥对（私钥、公钥）、一个地址构成。
 * 非对称秘钥由私钥、公钥构成。私钥可以推导出公钥。公钥不能逆推出私钥。
 * 公钥可以推导出数字货币的地址。地址不能逆推出公钥。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class Account implements Serializable {

    //私钥
    private String privateKey;
    //公钥
    private String publicKey;
    //地址
    private String address;

    public Account(String privateKey, String publicKey, String address) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.address = address;
    }


    //region get set
    //不需要暴露set方法

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getAddress() {
        return address;
    }

    //endregion
}
