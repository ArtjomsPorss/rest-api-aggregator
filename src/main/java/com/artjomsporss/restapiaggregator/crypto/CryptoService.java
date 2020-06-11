package com.artjomsporss.restapiaggregator.crypto;

import com.artjomsporss.restapiaggregator.FinnHubRestCaller;
import com.artjomsporss.restapiaggregator.api_jobs.ApiJobCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    FinnHubRestCaller restCaller;

    /**
     * Find symbols that have not been populated in DB within provided times: from, to. Both inclusive times.
     *
     * @param from time
     * @param to time
     * @param amountOfJobs
     * @return
     */
    public List<? extends ApiJobCommand> getCommandsForMissingCandles(String from, String to, String resolution, int amountOfJobs) {
        //TODO check timestamp >= from
        // check timestamp <= to
        // TODO limit to amountOfJobs

        // get candles that are present during last hour TODO change 0L and System.currentTimeMillis()
        Query query = Query.query(new Criteria("t").gte(from).lte(to));
        List<CryptoCandle> presentWithinLastHour = mongoTemplate.find(query, CryptoCandle.class);
        List<String> symbolsNodNeeded = presentWithinLastHour.parallelStream().map(e -> e.getSymbol()).distinct().collect(Collectors.toList());
        // get all symbols
        List<CryptoSymbol> allSymbols = cryptoSymbolRepository.findAll();
        List<String> allSymbolsStr = allSymbols.parallelStream().map(s -> s.getSymbol()).collect(Collectors.toList());
        // filter only the symbols that are missing
        List<String> requiredSymbols = allSymbolsStr.parallelStream().filter(e -> !symbolsNodNeeded.contains(e)).collect(Collectors.toList());

        return CryptoCandleCommand.createAll(requiredSymbols, from, to, resolution, restCaller, cryptoCandleRepository);
    }




}
