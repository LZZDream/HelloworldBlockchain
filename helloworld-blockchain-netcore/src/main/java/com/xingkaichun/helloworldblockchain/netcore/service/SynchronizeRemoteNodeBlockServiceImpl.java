package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.Synchronizer;
import com.xingkaichun.helloworldblockchain.core.SynchronizerDatabase;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.Dto2ModelTool;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.QueryBlockDtoByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.QueryBlockHashByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.netcore.node.client.BlockchainNodeClient;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class SynchronizeRemoteNodeBlockServiceImpl implements SynchronizeRemoteNodeBlockService {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizeRemoteNodeBlockServiceImpl.class);

    private BlockchainCore blockchainCore;
    private NodeService nodeService;
    private BlockchainNodeClient blockchainNodeClient;
    private ConfigurationService configurationService;
    /**
     * 若是有分叉时，一次同步的最后一个区块的高度至少要比本地区块链的高度大于N个。
     * 假设本地区块链挖矿过快，等外部区块通过网络传输到本节点后，在加入本地区块链做校验的时候，
     * 可能发现同步过来的区块的最大高度已经小于本地区块链的高度了。
     * 所以可以尝试一次多同步几个区块，至少保证本地计算能力达不到同步过来的高度。
     */
    private static final long SYNCHRONIZE_BLOCK_SIZE_FROM_LOCAL_BLOCKCHAIN_HEIGHT = 10;


    public SynchronizeRemoteNodeBlockServiceImpl(BlockchainCore blockchainCore, NodeService nodeService, BlockchainNodeClient blockchainNodeClient, ConfigurationService configurationService) {
        this.blockchainCore = blockchainCore;
        this.nodeService = nodeService;
        this.blockchainNodeClient = blockchainNodeClient;
        this.configurationService = configurationService;
    }



    @Override
    public void synchronizeRemoteNodeBlock(NodeDto node) {
        BlockchainDatabase blockchainDataBase = blockchainCore.getBlockchainDataBase();
        Synchronizer synchronizer = blockchainCore.getSynchronizer();
        SynchronizerDatabase synchronizerDataBase = synchronizer.getSynchronizerDataBase();

        String nodeId = buildNodeId(node);
        //这里直接清除老旧的数据，这里希望同步的操作可以在进程没有退出之前完成。
        synchronizerDataBase.clear(nodeId);
        Block tailBlock = blockchainDataBase.queryTailBlock();
        long localBlockchainHeight = tailBlock==null? LongUtil.ZERO:tailBlock.getHeight();

        //本地区块链与node区块链是否分叉？
        boolean fork = false;
        if(LongUtil.isEquals(localBlockchainHeight,LongUtil.ZERO)){
            fork = false;
        } else {
            localBlockchainHeight = tailBlock.getHeight();
            ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClient.queryBlockHashByBlockHeight(node,localBlockchainHeight);
            if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                return;
            }
            String blockHash = queryBlockHashByBlockHeightResponseServiceResult.getResult().getBlockHash();
            //远程节点的高度没有本地大
            if(StringUtil.isNullOrEmpty(blockHash)){
                return;
            } else {
                //没有分叉
                if(StringUtil.isEquals(tailBlock.getHash(),blockHash)){
                    fork = false;
                } else {
                    //有分叉
                    fork = true;
                }
            }
        }

        if(fork){
            //分叉
            //从当前区块同步至到未分叉区块
            long tempBlockHeight = localBlockchainHeight;
            while (true){
                if(LongUtil.isLessEqualThan(tempBlockHeight,LongUtil.ZERO)){
                    break;
                }
                //分叉长度过大，不可同步。这里，认为这已经形成了硬分叉(两条完全不同的区块链)。
                if(LongUtil.isGreatThan(localBlockchainHeight,tempBlockHeight + GlobalSetting.NodeConstant.FORK_BLOCK_SIZE)){
                    forkNodeHandler(node,synchronizerDataBase);
                    return;
                }
                BlockDTO blockDTO = getBlockDtoByBlockHeight(node,tempBlockHeight);
                if(blockDTO == null){
                    break;
                }

                ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClient.queryBlockHashByBlockHeight(node,tempBlockHeight);
                if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                    break;
                }
                String blockHash = queryBlockHashByBlockHeightResponseServiceResult.getResult().getBlockHash();
                Block localBlock = blockchainDataBase.queryBlockByBlockHeight(tempBlockHeight);
                if(StringUtil.isEquals(blockHash,localBlock.getHash())){
                    break;
                }
                synchronizerDataBase.addBlockDTO(nodeId,tempBlockHeight,blockDTO);
                tempBlockHeight = tempBlockHeight - LongUtil.ONE;
            }
            //从当前节点同步至最新
            tempBlockHeight = localBlockchainHeight + LongUtil.ONE;
            while (true){
                if(LongUtil.isLessEqualThan(tempBlockHeight,LongUtil.ZERO)){
                    break;
                }
                BlockDTO blockDTO = getBlockDtoByBlockHeight(node,tempBlockHeight);
                if(blockDTO == null){
                    break;
                }

                ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClient.queryBlockHashByBlockHeight(node,tempBlockHeight);
                if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                    break;
                }
                synchronizerDataBase.addBlockDTO(nodeId,tempBlockHeight,blockDTO);
                tempBlockHeight = tempBlockHeight + LongUtil.ONE;
                //若是有分叉时，一次同步的最后一个区块至少要比本地区块链的高度大于N个
                if(LongUtil.isGreatEqualThan(tempBlockHeight,localBlockchainHeight + SYNCHRONIZE_BLOCK_SIZE_FROM_LOCAL_BLOCKCHAIN_HEIGHT)){
                    break;
                }
            }

            synchronizerDataBase.addDataTransferFinishFlag(nodeId);
            //数据库里nodeid的数据必须清空：清空说明这些数据已经被区块链系统使用
            //最大允许的同步时间
            int maxTimestamp = 60*60*1000;
            //当前花费的同步时间
            int totalTimestamp = 0;
            //检测时间间隔
            long timestamp = 10*1000;
            while (true){
                List<String> allNodeId = synchronizerDataBase.getAllNodeId();
                if(allNodeId == null || !allNodeId.contains(nodeId)){
                    break;
                }
                ThreadUtil.sleep(timestamp);
                totalTimestamp += timestamp;
                if(totalTimestamp > maxTimestamp){
                    logger.error(String.format("节点[%s:%d]数据太长时间没有被区块链系统使用，请检查原因",node.getIp(),GlobalSetting.DEFAULT_PORT));
                    synchronizerDataBase.clear(nodeId);
                }
            }
        } else {
            //未分叉
            long tempBlockHeight = localBlockchainHeight + LongUtil.ONE;
            while (true){
                BlockDTO blockDTO = getBlockDtoByBlockHeight(node,tempBlockHeight);
                if(blockDTO == null){
                    synchronizerDataBase.clear(nodeId);
                    return;
                }

                ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeightResponseServiceResult = blockchainNodeClient.queryBlockHashByBlockHeight(node,tempBlockHeight);
                if(!ServiceResult.isSuccess(queryBlockHashByBlockHeightResponseServiceResult)){
                    synchronizerDataBase.clear(nodeId);
                    return;
                }

                Block block = Dto2ModelTool.blockDto2Block(blockchainDataBase,blockDTO);
                boolean isAddBlockSuccess = blockchainDataBase.addBlock(block);
                if(!isAddBlockSuccess){
                    synchronizerDataBase.clear(nodeId);
                    return;
                }
                tempBlockHeight = tempBlockHeight + LongUtil.ONE;
            }
        }
    }

    /**
     * 这里表明真的分叉区块个数过多了，形成了新的分叉，区块链协议不支持同步了。
     */
    private void forkNodeHandler(NodeDto node, SynchronizerDatabase synchronizerDataBase) {
        synchronizerDataBase.clear(buildNodeId(node));
        nodeService.updateOrInsertForkPropertity(node);
    }

    private String buildNodeId(NodeDto node) {
        return node.getIp();
    }

    private BlockDTO getBlockDtoByBlockHeight(NodeDto node, long blockHeight) {
        BlockDTO localBlockDTO = getLocalBlockDtoByBlockHeight(node,blockHeight);
        if(localBlockDTO != null){
            return localBlockDTO;
        }
        ServiceResult<QueryBlockDtoByBlockHeightResponse> blockDtoServiceResult = blockchainNodeClient.queryBlockDtoByBlockHeight(node,blockHeight);
        if(!ServiceResult.isSuccess(blockDtoServiceResult)){
            return null;
        }
        BlockDTO blockDTO = blockDtoServiceResult.getResult().getBlockDTO();
        return blockDTO;
    }

    private BlockDTO getLocalBlockDtoByBlockHeight(NodeDto node, long blockHeight) {
        SynchronizerDatabase synchronizerDataBase = blockchainCore.getSynchronizer().getSynchronizerDataBase();
        BlockDTO blockDTO = synchronizerDataBase.getBlockDto(buildNodeId(node),blockHeight);
        return blockDTO;
    }
}
