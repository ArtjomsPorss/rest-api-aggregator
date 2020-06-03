package com.artjomsporss.restapiaggregator.stock;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockQuoteRepository extends MongoRepository<StockQuote, String> {
}
