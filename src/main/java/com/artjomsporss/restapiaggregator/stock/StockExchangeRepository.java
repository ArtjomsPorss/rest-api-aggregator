package com.artjomsporss.restapiaggregator.stock;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockExchangeRepository extends MongoRepository<StockExchange, String> {

}
