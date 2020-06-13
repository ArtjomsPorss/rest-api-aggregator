package com.artjomsporss.restapiaggregator.crypto;

import com.artjomsporss.restapiaggregator.FinnHubRestCaller;
import com.artjomsporss.restapiaggregator.finnhub_api.ApiJobCommand;
import com.artjomsporss.restapiaggregator.scheduler.RemoteAPIJobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * The purpose of this class is to check Finnhub API reagarding which resolutions are supported by each CryptoExchange.
 * Steps to do it:
 * 1. db.cryptoSymbol.distinct('exchangeName') <-- gets an exchange
 * 2. db.cryptoSymbol.findOne( { exchange:'exchangeName' } )
 * 3. Get resolution times for an exchange:
 * 	- Get an exchange
 * 	- Get any one symbol for an exchange
 * 	- Call API for data using symbol + exchange and using EACH RESOLUTION, then fill Exchange data for each resolution in DB
 * 	- Get Resolution data from DB to query remote API
 */
@Service
public class CryptoExchangeResolutionAggregatorScheduler extends RemoteAPIJobScheduler {

    private Logger log = LoggerFactory.getLogger(CryptoExchangeResolutionAggregatorScheduler.class);
    private Queue<ApiJobCommand> jobQueue = new ArrayDeque<>();
    private int callcount = 0;

    @Autowired
    private FinnHubRestCaller restCaller;
    @Autowired
    private CryptoExchangeRepository exchangeRepo;
    // key: Exchange / map: Resolution
    private Map<String, List<String>> exchangesAndResolutions;
    @Autowired
    private MongoTemplate mongoTemplate;

    @EventListener(ApplicationStartedEvent.class)
    public void aggregate() {
        //get exchanges
        List<CryptoExchange> exchanges = mongoTemplate.findAll(CryptoExchange.class);
        //get a symbol per exchange
        List<CryptoSymbol> symbols = new ArrayList<>();
        exchanges.forEach(e -> {
            symbols.add(mongoTemplate.findOne(query(where("exchangeName").is(e.getExchangeName())), CryptoSymbol.class));
        });
        //aggregate per list of symbols
        aggregate(symbols);
    }

    private void aggregate(List<CryptoSymbol> symbols) {
        this.exchangesAndResolutions = new HashMap<String, List<String>>();

        // firstly, populate job queue
        symbols.forEach(s -> {
            CryptoCandle.Resolution.asList().stream().forEach(r -> {
                ApiJobCommand command = new CryptoExchangeResolutionCommand(r, s.getSymbol(), s.getExchangeName(), restCaller, exchangesAndResolutions);
                this.jobQueue.add(command);
            });
        });

        do {
            try {
                if (this.jobQueue.isEmpty()) {
                    // done
                    //TODO once the job is done, update exchangeRepo using data collected inexchangesAndResolutions
                    updateCryptoExchangeRepositoryWithData();
                    log.info("Crypto Exchange Resolution aggregation is done ...");
                    return;
                } else {
                    ApiJobCommand job = this.jobQueue.peek();
                    if(job.execute()) {
                        log.debug(String.format("job call [%d] successfully executed, removing from queue", callcount++));
                        this.jobQueue.remove();
                    } else {
                        log.debug("Reached time limit, resetting call count");
                        callcount = 0;
                        sleep();
                    }
                }
            } catch(Exception e) {
                log.error("Unexpected error");
                e.printStackTrace();
                return;
            }
        } while(true);
    }

    private void updateCryptoExchangeRepositoryWithData() {
        log.info(String.format("updating exchanges with resolutions. resolutions for %d exchanges were found", exchangesAndResolutions.keySet().size()));
        List<CryptoExchange> exchanges = exchangeRepo.findAll();
        exchanges.forEach(e -> {
            List<String> resolutions = exchangesAndResolutions.get(e.getExchangeName());
            e.setResolutions(resolutions);
        });
        exchangeRepo.saveAll(exchanges);
    }
}
