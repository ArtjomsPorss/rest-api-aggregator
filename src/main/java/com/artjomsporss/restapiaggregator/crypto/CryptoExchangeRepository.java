package com.artjomsporss.restapiaggregator.crypto;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CryptoExchangeRepository extends MongoRepository<CryptoExchange, String> {

}
