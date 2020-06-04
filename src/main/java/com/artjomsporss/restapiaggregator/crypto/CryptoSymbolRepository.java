package com.artjomsporss.restapiaggregator.crypto;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CryptoSymbolRepository extends MongoRepository<CryptoSymbol, String> {
    List<CryptoSymbol> findByExchangeName(String exchangeName);
    void deleteByExchangeName(String exchangeName);
}
