package com.artjomsporss.restapiaggregator;

import com.artjomsporss.restapiaggregator.exchange.Exchange;
import com.artjomsporss.restapiaggregator.exchange.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class RestApiAggregatorApplication {

	@Autowired
	ExchangeRepository exchangeRepo;

	public static void main(String[] args) {
		SpringApplication.run(RestApiAggregatorApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(FinnhubRestCallerImpl caller) {
		return args -> {
			Exchange[] exarr = caller.getExchange();
			List exList = new ArrayList(Arrays.asList(exarr));
			exList = exchangeRepo.saveAll(exList);
			exList.forEach(System.out::println);

		};
	}


}
