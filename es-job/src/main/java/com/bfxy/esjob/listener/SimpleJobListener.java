package com.bfxy.esjob.listener;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * $SimpleJobListener 自定义日志追踪
 */
public class SimpleJobListener implements ElasticJobListener {

	private static Logger LOGGER = LoggerFactory.getLogger(SimpleJobListener.class);
	
	@Override
	public void beforeJobExecuted(ShardingContexts shardingContexts) {
		LOGGER.info("-----------------执行任务之前：{}", JSON.toJSONString(shardingContexts));
	}

	@Override
	public void afterJobExecuted(ShardingContexts shardingContexts) {
		LOGGER.info("-----------------执行任务之后：{}", JSON.toJSONString(shardingContexts));
	}
	
}