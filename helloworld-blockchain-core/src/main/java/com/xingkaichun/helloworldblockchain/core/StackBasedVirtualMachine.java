package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.script.*;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 基于栈的虚拟机
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class StackBasedVirtualMachine {

    /**
     * 执行脚本
     */
    public ScriptExecuteResult executeScript(Transaction transactionEnvironment, Script script) throws RuntimeException {
        ScriptExecuteResult stack = new ScriptExecuteResult();

        for(int i=0;i<script.size();i++){
            String command = script.get(i);
            byte[] byteCommand = HexUtil.hexStringToBytes(command);
            if(Arrays.equals(OperationCodeEnum.OP_DUP.getCode(),byteCommand)){
                stack.push(stack.peek());
            }else if(Arrays.equals(OperationCodeEnum.OP_HASH160.getCode(),byteCommand)){
                String top = stack.peek();
                String publicKeyHash = AccountUtil.publicKeyHashFromPublicKey(top);
                stack.pop();
                stack.push(publicKeyHash);
            }else if(Arrays.equals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),byteCommand)){
                if(!StringUtil.isEquals(stack.pop(),stack.pop())){
                    throw new RuntimeException("脚本执行失败");
                }
            }else if(Arrays.equals(OperationCodeEnum.OP_CHECKSIG.getCode(),byteCommand)){
                String publicKey = stack.pop();
                byte[] bytesMessage = HexUtil.hexStringToBytes(TransactionTool.getSignatureData(transactionEnvironment));
                String signature = stack.pop();
                byte[] bytesSignature = HexUtil.hexStringToBytes(signature);
                boolean verifySignatureSuccess = AccountUtil.verifySignature(publicKey,bytesMessage,bytesSignature);
                if(!verifySignatureSuccess){
                    throw new RuntimeException("脚本执行失败");
                }
                stack.push(String.valueOf(Boolean.TRUE));
            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA128.getCode(),byteCommand)){
                stack.push(script.get(++i));
            }else {
                throw new RuntimeException("不能识别的指令");
            }
        }
        return stack;
    }

    public static Script createPayToClassicAddressScript(InputScript inputScript, OutputScript outputScript) {
        Script script = new Script();
        script.addAll(inputScript);
        script.addAll(outputScript);
        return script;
    }

    public static InputScript createPayToPublicKeyHashInputScript(String sign, String publicKey) {
        InputScript script = new InputScript();
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA128.getCode()));
        script.add(sign);
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA128.getCode()));
        script.add(publicKey);
        return script;
    }

    public static OutputScript createPayToPublicKeyHashOutputScript(String address) {
        OutputScript script = new OutputScript();
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA128.getCode()));
        String publicKeyHash = AccountUtil.publicKeyHashFromAddress(address);
        script.add(publicKeyHash);
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));
        return script;
    }

    public static String getPublicKeyHashByPayToPublicKeyHashOutputScript(List<String> outputScript) {
        return outputScript.get(3);
    }
}
