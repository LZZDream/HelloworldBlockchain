package com.xingkaichun.helloworldblockchain.core.model.transaction;

import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;

import java.io.Serializable;

/**
 * 交易输入
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class TransactionInput implements Serializable {

    //交易的输入是一笔交易的输出
    private UnspendTransactionOutput unspendTransactionOutput;
    /**
     * 脚本钥匙
     * 脚本钥匙用于解锁交易输出的脚本锁。解锁成功，则证明了(持有脚本钥匙的)用户拥有(脚本锁所在的)交易输出的所有权，
     * 拥有交易输出的所有权的用户才被允许使用这个交易输出。
     *
     * 这里特别注意
     * 任何用户都可以获取交易，并从交易中获取交易钥匙。
     * 不允许用户拿到脚本钥匙然后就使用这个交易输出，并能够成功组装自己的交易。
     * 假如新组装的交易先于老交易被区块链网络接受，这就相当于恶意用户花了别人的交易输出。
     */
    private InputScript inputScript;




    //region get set


    public UnspendTransactionOutput getUnspendTransactionOutput() {
        return unspendTransactionOutput;
    }

    public void setUnspendTransactionOutput(UnspendTransactionOutput unspendTransactionOutput) {
        this.unspendTransactionOutput = unspendTransactionOutput;
    }

    public InputScript getInputScript() {
        return inputScript;
    }

    public void setInputScript(InputScript inputScript) {
        this.inputScript = inputScript;
    }

    //endregion
}