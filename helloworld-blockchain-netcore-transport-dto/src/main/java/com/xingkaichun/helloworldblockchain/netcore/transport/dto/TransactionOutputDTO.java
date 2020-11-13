package com.xingkaichun.helloworldblockchain.netcore.transport.dto;


import java.io.Serializable;

/**
 * 交易输出
 * 属性含义参考 com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class TransactionOutputDTO implements Serializable {

    //交易输出的金额
    private long value;
    //脚本锁
    private OutputScriptDTO outputScriptDTO;




    //region get set

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public OutputScriptDTO getOutputScriptDTO() {
        return outputScriptDTO;
    }

    public void setOutputScriptDTO(OutputScriptDTO outputScriptDTO) {
        this.outputScriptDTO = outputScriptDTO;
    }

    //endregion
}
