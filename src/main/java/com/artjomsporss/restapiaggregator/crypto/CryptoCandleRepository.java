package com.artjomsporss.restapiaggregator.crypto;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CryptoCandleRepository extends MongoRepository<CryptoCandle, String> {

}
