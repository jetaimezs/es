/*
 * Copyright (C)
 * FileName: ElasticSearchConfig
 * Author:   lim160
 * Date:     2020/9/27 17:47
 * Description: //模块目的、功能描述
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.lsmy.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 〈〉<br>
 *
 * @author 17092777 李明
 */
@Configuration
public class ElasticSearchConfig {

	public static final RequestOptions COMMON_OPTIONS;
	static {
		RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//		builder.addHeader("Authorization", "Bearer " + TOKEN);
//		builder.setHttpAsyncResponseConsumerFactory(
//				new HttpAsyncResponseConsumerFactory
//						.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
		COMMON_OPTIONS = builder.build();
	}

	@Bean
	public RestHighLevelClient restHighLevelClient() {
		RestClientBuilder builder;
		builder = RestClient.builder(new HttpHost("192.168.56.10", 9200, "http"));
		RestHighLevelClient client = new RestHighLevelClient(builder);
		return client;
	}
}
