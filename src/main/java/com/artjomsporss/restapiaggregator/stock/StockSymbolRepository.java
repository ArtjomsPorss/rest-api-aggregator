package com.artjomsporss.restapiaggregator.stock;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockSymbolRepository extends MongoRepository<StockSymbol, String> {
    List<StockSymbol> findByExchangeCode(String exchangeCode);
    void deleteByExchangeCode(String exchangeCode);
}
