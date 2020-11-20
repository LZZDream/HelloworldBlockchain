package com.xingkaichun.helloworldblockchain.node;

import com.xingkaichun.helloworldblockchain.core.tools.ResourcePathTool;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCoreFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 启动入口
 * //TODO 区块链浏览器项目拆分到独立的项目？？？还是在代码里标清楚浏览器部分的代码？？？
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
@Configuration
@SpringBootApplication
@ServletComponentScan
public class HelloWorldBlockchainNodeApplication {

	@Value("${blockchainDataPath:}")
	private String blockchainDataPath;


	public static void main(String[] args) {
		SpringApplication.run(HelloWorldBlockchainNodeApplication.class, args);
	}


	@Bean
	public NetBlockchainCore buildNetBlockchainCore() {
		NetBlockchainCore netBlockchainCore = NetBlockchainCoreFactory.createNetBlockchainCore(ResourcePathTool.getDataRootPath(blockchainDataPath));
		netBlockchainCore.start();
		return netBlockchainCore;
	}
}
