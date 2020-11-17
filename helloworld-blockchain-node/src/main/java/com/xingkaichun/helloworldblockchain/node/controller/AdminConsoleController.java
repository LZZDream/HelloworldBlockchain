package com.xingkaichun.helloworldblockchain.node.controller;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.pay.BuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.core.model.pay.BuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.core.model.pay.Recipient;
import com.xingkaichun.helloworldblockchain.core.tools.WalletTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.node.dto.AdminConsoleApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.account.*;
import com.xingkaichun.helloworldblockchain.node.dto.block.DeleteBlockRequest;
import com.xingkaichun.helloworldblockchain.node.dto.block.DeleteBlockResponse;
import com.xingkaichun.helloworldblockchain.node.dto.miner.*;
import com.xingkaichun.helloworldblockchain.node.dto.node.*;
import com.xingkaichun.helloworldblockchain.node.dto.synchronizer.*;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员控制台的控制器：用于控制本地区块链节点，如激活矿工、停用矿工、同步其它节点数据等。
 * 这里的操作都需要一定得权限才可以操作，不适合对所有人开放。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
@Controller
@RequestMapping
public class AdminConsoleController {

    private static final Logger logger = LoggerFactory.getLogger(AdminConsoleController.class);

    @Autowired
    private NetBlockchainCore netBlockchainCore;

    /**
     * 矿工是否激活
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.IS_MINER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsMinerActiveResponse> isMineActive(@RequestBody IsMinerActiveRequest request){
        try {
            boolean isMineActive = netBlockchainCore.getConfigurationService().isMinerActive();
            IsMinerActiveResponse response = new IsMinerActiveResponse();
            response.setMinerInActiveState(isMineActive);
            return ServiceResult.createSuccessServiceResult("查询矿工是否处于激活状态成功",response);
        } catch (Exception e){
            String message = "查询矿工是否处于激活状态失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 激活矿工
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveMinerResponse> activeMiner(@RequestBody ActiveMinerRequest request){
        try {
            netBlockchainCore.getConfigurationService().activeMiner();
            ActiveMinerResponse response = new ActiveMinerResponse();
            response.setActiveMinerSuccess(true);
            return ServiceResult.createSuccessServiceResult("激活矿工成功",response);
        } catch (Exception e){
            String message = "激活矿工失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 停用矿工
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.DEACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeactiveMinerResponse> deactiveMiner(@RequestBody DeactiveMinerRequest request){
        try {
            netBlockchainCore.getConfigurationService().deactiveMiner();
            DeactiveMinerResponse response = new DeactiveMinerResponse();
            response.setDeactiveMinerSuccess(true);
            return ServiceResult.createSuccessServiceResult("停用矿工成功",response);
        } catch (Exception e){
            String message = "停用矿工失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 同步器是否激活
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.IS_SYNCHRONIZER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsSynchronizerActiveResponse> isSynchronizerActive(@RequestBody IsSynchronizerActiveRequest request){
        try {
            boolean isSynchronizerActive = netBlockchainCore.getConfigurationService().isSynchronizerActive();
            IsSynchronizerActiveResponse response = new IsSynchronizerActiveResponse();
            response.setSynchronizerInActiveState(isSynchronizerActive);
            return ServiceResult.createSuccessServiceResult("查询同步器是否激活成功",response);
        } catch (Exception e){
            String message = "查询同步器是否激活失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 激活同步器
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ACTIVE_SYNCHRONIZER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveSynchronizerResponse> activeSynchronizer(@RequestBody ActiveSynchronizerRequest request){
        try {
            netBlockchainCore.getConfigurationService().activeSynchronizer();
            ActiveSynchronizerResponse response = new ActiveSynchronizerResponse();
            response.setActiveSynchronizerSuccess(true);
            return ServiceResult.createSuccessServiceResult("激活同步器成功",response);
        } catch (Exception e){
            String message = "激活同步器失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 停用同步器
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.DEACTIVE_SYNCHRONIZER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeactiveSynchronizerResponse> deactiveSynchronizer(@RequestBody DeactiveSynchronizerRequest request){
        try {
            netBlockchainCore.getConfigurationService().deactiveSynchronizer();
            DeactiveSynchronizerResponse response = new DeactiveSynchronizerResponse();
            response.setDeactiveSynchronizerSuccess(true);
            return ServiceResult.createSuccessServiceResult("停用同步器成功",response);
        } catch (Exception e){
            String message = "停用同步器失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 新增节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ADD_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<AddNodeResponse> addNode(@RequestBody AddNodeRequest request){
        try {
            NodeDto node = request.getNode();
            if(StringUtil.isNullOrEmpty(node.getIp())){
                return ServiceResult.createFailServiceResult("节点IP不能为空");
            }
            if(netBlockchainCore.getNodeService().queryNode(node) != null){
                return ServiceResult.createFailServiceResult("节点已经存在，不需要重复添加");
            }
            netBlockchainCore.getNodeService().addNode(node);
            AddNodeResponse response = new AddNodeResponse();
            response.setAddNodeSuccess(true);
            return ServiceResult.createSuccessServiceResult("新增节点成功",response);
        } catch (Exception e){
            String message = "新增节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 更新节点信息
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.UPDATE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<UpdateNodeResponse> updateNode(@RequestBody UpdateNodeRequest request){
        try {
            if(request.getNode() == null){
                return ServiceResult.createFailServiceResult("请填写节点信息");
            }
            if(netBlockchainCore.getNodeService().queryNode(request.getNode()) == null){
                return ServiceResult.createFailServiceResult("节点不存在，无法更新");
            }
            netBlockchainCore.getNodeService().updateNode(request.getNode());
            UpdateNodeResponse response = new UpdateNodeResponse();
            return ServiceResult.createSuccessServiceResult("更新节点信息成功",response);
        } catch (Exception e){
            String message = "更新节点信息失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 删除节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.DELETE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeleteNodeResponse> deleteNode(@RequestBody DeleteNodeRequest request){
        try {
            netBlockchainCore.getNodeService().deleteNode(request.getNode());
            DeleteNodeResponse response = new DeleteNodeResponse();
            return ServiceResult.createSuccessServiceResult("删除节点成功",response);
        } catch (Exception e){
            String message = "删除节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 查询所有节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.QUERY_ALL_NODE_LIST,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryAllNodeListResponse> queryAllNodeList(@RequestBody QueryAllNodeListRequest request){
        try {
            List<NodeDto> nodeList = netBlockchainCore.getNodeService().queryAllNodeList();
            QueryAllNodeListResponse response = new QueryAllNodeListResponse();
            response.setNodeList(nodeList);
            return ServiceResult.createSuccessServiceResult("查询节点成功",response);
        } catch (Exception e){
            String message = "查询节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 查询开启自动寻找区块链节点的功能
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.IS_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsAutoSearchNodeResponse> isAutoSearchNewNode(@RequestBody IsAutoSearchNodeRequest request){
        try {
            boolean isAutoSearchNode = netBlockchainCore.getConfigurationService().isAutoSearchNode();
            IsAutoSearchNodeResponse response = new IsAutoSearchNodeResponse();
            response.setAutoSearchNewNode(isAutoSearchNode);
            return ServiceResult.createSuccessServiceResult("查询是否允许自动搜索区块链节点成功",response);
        } catch (Exception e){
            String message = "查询是否允许自动搜索区块链节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 设置是否允许自动寻找区块链节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.SET_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<SetAutoSearchNodeResponse> setAutoSearchNode(@RequestBody SetAutoSearchNodeRequest request){
        try {
            netBlockchainCore.getConfigurationService().setAutoSearchNode(request.isAutoSearchNode());
            SetAutoSearchNodeResponse response = new SetAutoSearchNodeResponse();
            return ServiceResult.createSuccessServiceResult("设置是否允许自动搜索区块链节点成功",response);
        } catch (Exception e){
            String message = "设置是否允许自动搜索区块链节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 新增账户
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ADD_ACCOUNT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<AddAccountResponse> addAccount(@RequestBody AddAccountRequest request){
        try {
            String privateKey = request.getPrivateKey();
            if(StringUtil.isNullOrEmpty(privateKey)){
                return ServiceResult.createFailServiceResult("账户私钥不能为空。");
            }
            Account account = AccountUtil.accountFromPrivateKey(privateKey);
            getBlockchainCore().getWallet().addAccount(account);
            AddAccountResponse response = new AddAccountResponse();
            response.setAddAccountSuccess(true);
            return ServiceResult.createSuccessServiceResult("新增账户成功",response);
        } catch (Exception e){
            String message = "新增账户失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 删除账户
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.DELETE_ACCOUNT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeleteAccountResponse> deleteAccount(@RequestBody DeleteAccountRequest request){
        try {
            String address = request.getAddress();
            if(StringUtil.isNullOrEmpty(address)){
                return ServiceResult.createFailServiceResult("请填写需要删除的地址");
            }
            getBlockchainCore().getWallet().deleteAccountByAddress(address);
            DeleteAccountResponse response = new DeleteAccountResponse();
            response.setDeleteAccountSuccess(true);
            return ServiceResult.createSuccessServiceResult("删除账号成功",response);
        } catch (Exception e){
            String message = "删除账号失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 查询所有的账户
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.QUERY_ALL_ACCOUNT_LIST,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryAllAccountListResponse> queryAllAccountList(@RequestBody QueryAllAccountListRequest request){
        try {
            List<Account> allAccount = getBlockchainCore().getWallet().queryAllAccount();

            List<QueryAllAccountListResponse.AccountDto> accountDtoList = new ArrayList<>();
            if(allAccount != null){
                for(Account account:allAccount){
                    QueryAllAccountListResponse.AccountDto accountDto = new QueryAllAccountListResponse.AccountDto();
                    accountDto.setAddress(account.getAddress());
                    accountDto.setPrivateKey(account.getPrivateKey());
                    accountDto.setValue(WalletTool.obtainBalance(getBlockchainCore(),account.getAddress()));
                    accountDtoList.add(accountDto);
                }
            }
            long balance = 0;
            for(QueryAllAccountListResponse.AccountDto accountDto:accountDtoList){
                balance += accountDto.getValue();
            }
            QueryAllAccountListResponse response = new QueryAllAccountListResponse();
            response.setAccountDtoList(accountDtoList);
            response.setBalance(balance);
            return ServiceResult.createSuccessServiceResult("[查询所有账户]成功",response);
        } catch (Exception e){
            String message = "[查询所有账户]失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 删除区块
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.DELETE_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeleteBlockResponse> deleteBlock(@RequestBody DeleteBlockRequest request){
        try {
            if(request.getBlockHeight() == null){
                return ServiceResult.createFailServiceResult("删除区块失败，区块高度不能空。");
            }
            getBlockchainCore().deleteBlocksUtilBlockHeightLessThan(request.getBlockHeight());
            DeleteBlockResponse response = new DeleteBlockResponse();
            return ServiceResult.createSuccessServiceResult("删除区块成功",response);
        } catch (Exception e){
            String message = "删除区块失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 构建交易
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.BUILD_TRANSACTION,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<BuildTransactionResponse> buildTransaction(@RequestBody BuildTransactionRequest request){
        try {
            List<Recipient> recipientList = request.getRecipientList();
            if(recipientList == null || recipientList.isEmpty()){
                return ServiceResult.createFailServiceResult("交易输出不能为空。");
            }
            for(Recipient recipient:recipientList){
                if(StringUtil.isNullOrEmpty(recipient.getAddress())){
                    return ServiceResult.createFailServiceResult("交易输出的地址不能为空。");
                }
            }
            BuildTransactionResponse buildTransactionResponse = netBlockchainCore.buildTransaction(request);
            return ServiceResult.createSuccessServiceResult("构建交易成功",buildTransactionResponse);
        } catch (Exception e){
            String message = "构建交易失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    private BlockchainCore getBlockchainCore(){
        return netBlockchainCore.getBlockchainCore();
    }
}