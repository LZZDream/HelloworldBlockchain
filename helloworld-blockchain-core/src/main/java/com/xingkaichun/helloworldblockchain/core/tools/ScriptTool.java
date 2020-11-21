package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.common.primitives.Bytes;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 脚本工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class ScriptTool {

    /**
     * 字节型脚本
     */
    public static byte[] bytesScript(List<String> script) {
        byte[] bytesScript = new byte[0];
        for(int i=0;i<script.size();i++){
            String operationCode = script.get(i);
            byte[] bytesOperationCode = HexUtil.hexStringToBytes(operationCode);
            if(Arrays.equals(OperationCodeEnum.OP_DUP.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_HASH160.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_CHECKSIG.getCode(),bytesOperationCode)){
                bytesScript = Bytes.concat(bytesScript, ByteUtil.concatLengthBytes(bytesOperationCode));
            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA128.getCode(),bytesOperationCode)){
                String operationData = script.get(++i);
                byte[] bytesOperationData = HexUtil.hexStringToBytes(operationData);
                bytesScript = Bytes.concat(bytesScript, ByteUtil.concatLengthBytes(bytesOperationCode), ByteUtil.concatLengthBytes(bytesOperationData));
            }else {
                throw new RuntimeException("不能识别的指令");
            }
        }
        return bytesScript;
    }




    /**
     * 字节型脚本
     */
    public static String toString(List<String> script) {
        String stringScript = "";
        for(int i=0;i<script.size();i++){
            String operationCode = script.get(i);
            byte[] bytesOperationCode = HexUtil.hexStringToBytes(operationCode);
            if(Arrays.equals(OperationCodeEnum.OP_DUP.getCode(),bytesOperationCode)){
                stringScript = stringScript + OperationCodeEnum.OP_DUP.getName() + " ";
            }else if(Arrays.equals(OperationCodeEnum.OP_HASH160.getCode(),bytesOperationCode)){
                stringScript = stringScript + OperationCodeEnum.OP_HASH160.getName() + " ";
            }else if(Arrays.equals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),bytesOperationCode)){
                stringScript = stringScript + OperationCodeEnum.OP_EQUALVERIFY.getName() + " ";
            }else if(Arrays.equals(OperationCodeEnum.OP_CHECKSIG.getCode(),bytesOperationCode)){
                stringScript = stringScript + OperationCodeEnum.OP_CHECKSIG.getName() + " ";
            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA128.getCode(),bytesOperationCode)){
                String operationData = script.get(++i);
                stringScript = stringScript + OperationCodeEnum.OP_PUSHDATA128.getName() + " ";
                stringScript = stringScript + operationData + " ";
            }else {
                throw new RuntimeException("不能识别的指令");
            }
        }
        return stringScript;
    }
}
