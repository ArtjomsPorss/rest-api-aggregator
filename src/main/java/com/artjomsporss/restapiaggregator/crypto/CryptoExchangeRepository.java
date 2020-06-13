package com.artjomsporss.restapiaggregator.crypto;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CryptoExchangeRepository extends MongoRepository<CryptoExchange, String> {

    List<CryptoExchange> findByExchangeName(String exchangeName);
}
