package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;

import java.util.List;


/**
 * 同步器数据库
 * 区块链是一个分布式的数据库。
 * 当其它节点的区块链链的长度大于本节点区块链的链的长度时，本节点应该同步其它节点的区块数据。
 * 同步器将其它节点的区块数据下载到本地节点，这些数据暂时保存到本地节点，但是还没有加入到本地区块链的链上。
 * 本类的主要功能就是存储其它节点的区块数据，以供本地区块链节点使用。若同步过来的区块链节点数据可以加入本地区块链的链上，
 * 那么在合适的时候，本地节点将会把这些数据加入到本地区块链的链上。
 *
 * 至少需要同步多少个区块？
 * A为什么要同步B的区块数据？因为A一直在寻找链的长度比自己的长的区块链，希望用其它节点的区块来增长自己区块链的长度。
 * 所以A至少要需要同步这么一个区块，这个区块的区块高度比自己的最大的区块高度大1，这样A才能增大自己区块链的长度。
 * 假设本节点A区块链(A的区块个数是M个)需要同步B节点区块链(B的区块个数是(M+N)个)的区块数据。
 * 当M>0、N<0，这种情况下，因为A的链长比B长，A不需要去同步B的区块。
 * 当M>0、N=0，这种情况下，因为A的链长和B一样长，A不需要去同步B的区块。
 * 当M>0、N>0：
 * 情况1，A与B不分叉，即A区块链的前M个区块数据和B的前M个区块数据完全一致，A至少需要同步B区块链的1个区块(第M+1个区块)。
 * 情况2，若A与B从第(M-P)个区块分叉(也就是前(M-P-1)个区块数据一致，A与B区块链从第((M-P-1)+1)个区块开始不一致了)，
 * 那么A至少需要同步B区块链((M+1)-(M-P-1))个区块的数据。
 *
 * A同步B区块链分为两个阶段：
 * 数据传输阶段
 * 将B节点的数据分批次传输到A节点
 * 真正同步阶段
 * A区块链对传输过来的区块数据进行校验检测。
 * 若校验失败，丢弃传输过来的区块数据。
 * 若校验通过，将传输过来的区块数据整合进自身区块链，A的区块链将变得更长。
 *
 * A同步B区块链时，需要同步的区块个数太多，一次网络传输不完，也不可能一次性把所有区块数据加载到内存，因此这里传输采用的单位是Block。
 *
 * 假设P=10000，在A同步B区块链的区块数据的过程中，B一次传输100个区块。
 * 每当A接收到B的100个区块时，A立刻尝试把同步的区块放入A的区块链上。
 * 当第一批100个区块传输完毕，因为100<10000，所以A认为这一百个区块太少，A不能形成更长的区块链。
 * 当第二批100个区块传输完毕，因为100+100<10000，所以A认为这这两批次区块太少，A不能形成更长的区块链。
 * ......
 * 因此应当有一个标识，用来代表本节点区块链可以进行真正的同步操作了。
 * A对B可以进行'真正同步'操作的时机：已同步B的区块高度大于自身区块高度。
 * 但是，为了简单，我们选择当B的数据完全传输完毕，A再进行真正的同步操作。
 * 所以，在这里，人为的增加了一个代表数据传输完毕的标识。
 *
 * 本地区块链可能需要同步多个节点区块链的数据，因此本类需要能够存放多个节点的区块链数据，并且保证它们互不干扰。
 *
 * 本类的正确使用方式
 * 获取一个有数据传输完毕标识的节点ID。
 * 根据节点ID可获取传输过来的完整数据。(循环获取节点下一个Block，直至获取结果为null)。在这一步中，
 * 因为获取到了传输的数据，所以可以做自己的业务逻辑了。
 * 使用完毕后，清除节点ID传输数据，清除节点ID的数据传输完毕标识。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public abstract class SynchronizerDatabase {

    /**
     * 保存节点(nodeId)传输过来的数据
     */
    public abstract boolean addBlockDTO(String nodeId, long blockHeight, BlockDTO blockDTO) ;
    /**
     * 获取节点(nodeId)传输过来所有区块中最小的区块高度。
     */
    public abstract long getMinBlockHeight(String nodeId) ;
    /**
     * 获取节点(nodeId)传输过来所有区块中最大的区块高度。
     */
    public abstract long getMaxBlockHeight(String nodeId) ;
    /**
     * 根据节点(nodeId)与区块高度获取区块
     */
    public abstract BlockDTO getBlockDto(String nodeId, long blockHeight) ;
    /**
     * 给节点(nodeId)添加数据传输完成的标识。
     */
    public abstract void addDataTransferFinishFlag(String nodeId) ;
    /**
     * 节点(nodeId)有数据传输完成的标识吗？
     */
    public abstract boolean hasDataTransferFinishFlag(String nodeId);
    /**
     * 删除节点(nodeId)传输过来的数据。
     * 清除节点(nodeId)的数据传输完成标识
     */
    public abstract void clear(String nodeId) ;
    /**
     * 获取一个有数据传输完成标识的节点ID
     */
    public abstract String getDataTransferFinishFlagNodeId() ;
    /**
     * 获取所有节点ID
     */
    public abstract List<String> getAllNodeId() ;
    /**
     * 获取节点ID最后更新时间
     */
    public abstract long getLastUpdateTimestamp(String nodeId) ;
}
