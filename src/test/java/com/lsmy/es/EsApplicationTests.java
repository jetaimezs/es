package com.lsmy.es;

import com.alibaba.fastjson.JSON;
import java.io.IOException;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EsApplicationTests {

	@Autowired
	private RestHighLevelClient client;

	@Test
	public void contextLoads() {
		System.out.println(client);
	}

	public static final RequestOptions COMMON_OPTIONS;

	static {
		RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
		//		builder.addHeader("Authorization", "Bearer " + TOKEN);
		//		builder.setHttpAsyncResponseConsumerFactory(
		//				new HttpAsyncResponseConsumerFactory
		//						.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
		COMMON_OPTIONS = builder.build();
	}

	@Test
	void indexData() throws IOException {
		IndexRequest indexRequest = new IndexRequest("users");
		indexRequest.id("1");
		User user = new User();
		user.setAge(21);
		user.setGender("F");
		user.setUserName("lsmy");
		String str = JSON.toJSONString(user);
		indexRequest.source(str, XContentType.JSON);
		IndexResponse index = client.index(indexRequest, COMMON_OPTIONS);
		System.out.println(index);

	}

	@Test
	void searchData() throws IOException {
		//创建检索请求
		SearchRequest searchRequest = new SearchRequest();
		//创建索引
		searchRequest.indices("newbank");
		//指定dsl  检索条件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
		//按照年龄聚合
		TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
		sourceBuilder.aggregation(ageAgg);
		//计算平均薪资
		AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
		sourceBuilder.aggregation(balanceAvg);
		System.out.println(sourceBuilder.toString());
		//		sourceBuilder.from();
		//		sourceBuilder.size();
		//		sourceBuilder.aggregation();

		searchRequest.source(sourceBuilder);

		//执行检索
		SearchResponse search = client.search(searchRequest, COMMON_OPTIONS);

		//分析结果
		System.out.println(search.toString());
		//Map map = JSON.parseObject(search.toString(), Map.class);
		SearchHit[] hits = search.getHits().getHits();
		for (SearchHit hit : hits) {
			String sourceAsString = hit.getSourceAsString();
			Account account = JSON.parseObject(sourceAsString, Account.class);
			System.out.println("account:" + account);
		}

		Aggregations aggregations = search.getAggregations();
		for (Aggregation aggregation : aggregations) {
			System.out.println("当前聚合名：" + aggregation.getName());
		}
		Terms ageAgg1 = aggregations.get("ageAgg");
		for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
			System.out.println("年龄：" + bucket.getKeyAsString());
		}

		Avg balanceAvg1 = aggregations.get("balanceAvg");
		System.out.println("平均薪资：" + balanceAvg1.getValue());
	}
	@ToString
	@Data
	class User {
		private String userName;
		private String gender;
		private Integer age;
	}

	@ToString
	@Data
	public static class Account {

		private int account_number;
		private int balance;
		private String firstname;
		private String lastname;
		private int age;
		private String gender;
		private String address;
		private String employer;
		private String email;
		private String city;
		private String state;
	}

}
