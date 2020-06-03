package com.artjomsporss.restapiaggregator;

import com.artjomsporss.restapiaggregator.stock.StockExchangeRepository;
import com.artjomsporss.restapiaggregator.stock.StockSymbolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class RestApiAggregatorApplication {

	@Autowired
	StockExchangeRepository exchangeRepo;
	@Autowired
	StockSymbolRepository symbolRepository;

	public static void main(String[] args) {
		SpringApplication.run(RestApiAggregatorApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(FinnhubRestCallerImpl caller) {
		return args -> {

		};
	}


}
