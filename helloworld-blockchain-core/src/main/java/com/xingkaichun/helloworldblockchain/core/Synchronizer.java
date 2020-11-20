package com.xingkaichun.helloworldblockchain.core;

/**
 * 区块链同步器。
 * 区块链是一个分布式的数据库。
 * 本地节点的区块链高度落后于网络节点A的区块链高度，这时就需要将A的区块同步至本地区块链，这个就是该类的主要功能。
 * //TODO 同步相关迁移到netcore比较恰当
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public abstract class Synchronizer {

    //节点同步数据库
    protected SynchronizerDatabase synchronizerDataBase;

    public Synchronizer(SynchronizerDatabase synchronizerDataBase) {
        this.synchronizerDataBase = synchronizerDataBase;
    }


    //region 同步其它区块链节点的数据
    /**
     * 启用同步器。
     * 同步器有两种状态：活动状态与非活动状态。
     * 若同步器处于活动状态，开始同步其它区块链节点的数据，直至本次同步结束。
     * 若同步器处于非活动状态，矿工不会进行任何工作。
     */
    public abstract void start();

    /**
     * 同步器是否处于激活状态。
     */
    public abstract boolean isActive();

    /**
     * 激活同步器：设置同步器为活动状态。
     */
    public abstract void active();

    /**
     * 停用同步器：设置同步器为非活动状态。
     */
    public abstract void deactive();
    //endregion




    //region get set

    public SynchronizerDatabase getSynchronizerDataBase() {
        return synchronizerDataBase;
    }
    //endregion
}
