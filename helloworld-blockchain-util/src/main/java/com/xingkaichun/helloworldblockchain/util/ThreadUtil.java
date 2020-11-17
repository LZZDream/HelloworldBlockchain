package com.xingkaichun.helloworldblockchain.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class ThreadUtil {

    private static final Logger logger = LoggerFactory.getLogger(ThreadUtil.class);

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("线程休眠异常",e);
        }
    }
}
