package com.artjomsporss.restapiaggregator;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.client.RestTemplate;


@Configuration
public class ApplicationConfig {
    @Value("${spring.data.mongodb.host}")
    private String mongodbHost;
    @Value("${spring.data.mongodb.port}")
    private String mongodbPort;
    @Value("${spring.data.mongodb.database}")
    private String mongoDbName;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(String.format("mongodb://%s:%s", mongodbHost, mongodbPort));
    }
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, mongoDbName);
    }
}
