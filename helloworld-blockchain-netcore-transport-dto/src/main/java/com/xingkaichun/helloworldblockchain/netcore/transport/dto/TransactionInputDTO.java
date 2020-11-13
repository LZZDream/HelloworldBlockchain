package com.xingkaichun.helloworldblockchain.netcore.transport.dto;

import java.io.Serializable;

/**
 * 交易输入
 * 属性含义参考 com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class TransactionInputDTO implements Serializable {

    //未花费输出
    private UnspendTransactionOutputDTO unspendTransactionOutputDTO;
    //脚本钥匙
    private InputScriptDTO inputScriptDTO;




    //region get set

    public UnspendTransactionOutputDTO getUnspendTransactionOutputDTO() {
        return unspendTransactionOutputDTO;
    }

    public void setUnspendTransactionOutputDTO(UnspendTransactionOutputDTO unspendTransactionOutputDTO) {
        this.unspendTransactionOutputDTO = unspendTransactionOutputDTO;
    }

    public InputScriptDTO getInputScriptDTO() {
        return inputScriptDTO;
    }

    public void setInputScriptDTO(InputScriptDTO inputScriptDTO) {
        this.inputScriptDTO = inputScriptDTO;
    }

    //endregion
}