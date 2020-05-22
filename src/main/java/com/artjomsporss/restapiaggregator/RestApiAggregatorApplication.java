package com.artjomsporss.restapiaggregator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class RestApiAggregatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApiAggregatorApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(FinnhubRestCallerImpl caller) {
		return args -> {
			StockExchange[] exarr = caller.getExchange();
			ArrayList exList = new ArrayList(Arrays.asList(exarr));
			exList.forEach(System.out::println);
		};
	}


}
