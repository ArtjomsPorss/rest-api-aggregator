package com.artjomsporss.restapiaggregator.crypto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.mongodb.client.model.Filters.elemMatch;
import static com.mongodb.client.model.Filters.nin;

@Service
public class CryptoService {
    @Autowired
    CryptoSymbolRepository cryptoSymbolRepository;
    @Autowired
    CryptoCandleRepository cryptoCandleRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * Find symbols that have not been populated in DB within provided times: from, to. Both inclusive times.
     *
     * @param from time
     * @param to time
     * @param amountOfJobs
     * @return
     */
    public List<CryptoSymbol> findDetailsForMissingCandles(LocalDateTime from, LocalDateTime to, int amountOfJobs) {
        //TODO check timestamp >= from
        // check timestamp <= to
        // TODO limit to amountOfJobs
        // prepare aggregators
        LookupOperation lookup = Aggregation.lookup("cryptoCandle", "symbol", "symbol", "stockdata");
        MatchOperation match = Aggregation.match(new Criteria("stockdata").size(0));
        Aggregation agg = Aggregation.newAggregation(lookup, match);
        // query
        AggregationResults<CryptoSymbol> aggregation = mongoTemplate.aggregate(agg, "cryptoSymbol", CryptoSymbol.class);
        List<CryptoSymbol> aggregatedSymbols = aggregation.getMappedResults();
//        cryptoCandleRepository.findAll(Example.of)
        return aggregatedSymbols;

    }
}
