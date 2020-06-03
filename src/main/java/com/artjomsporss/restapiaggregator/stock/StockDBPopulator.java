package com.artjomsporss.restapiaggregator.stock;

import com.artjomsporss.restapiaggregator.FinnHubRestCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * The goal of this service is prepopulate db (if required)
 * with absolutely necessary data such as exchanges and stocks.
 * This data will be used later to query using cron job
 */
@Service
public class StockDBPopulator {

    private final Logger log = LoggerFactory.getLogger(StockDBPopulator.class);
    public static final String HTTP_TOO_MANY_REQUESTS = "429 Too Many Requests: [API limit reached. Please try again later.]";

    @Autowired
    FinnHubRestCaller api;
    @Autowired
    StockExchangeRepository exchangeRepo;
    @Autowired
    StockSymbolRepository symbolRepository;
    @Autowired
    StockQuoteRepository quoteRepository;


    @EventListener(ApplicationPreparedEvent.class)
    public void ApplicationPreparedEvent() {
        System.out.println("          ApplicationPreparedEvent");
    }
    @EventListener(ApplicationEnvironmentPreparedEvent.class)
    public void ApplicationEnvironmentPreparedEvent() {
        System.out.println("ApplicationPreparedEvent");
    }

//    @EventListener(ApplicationReadyEvent.class)
//    public void loadData() {
//
//    }

    /**
     * Happens after psvm executed
     */
    @EventListener(ApplicationReadyEvent.class)
    public void ApplicationReadyEvent() {
        System.out.println("          ApplicationReadyEvent");
    }
    @EventListener(ApplicationStartingEvent.class)
    public void ApplicationStartingEvent() {
        System.out.println("          ApplicationStartingEvent");
    }

    @EventListener(ApplicationContextInitializedEvent.class)
    public void ApplicationContextInitializedEvent() {
        System.out.println("          ApplicationContextInitializedEvent");
    }

    /**
     * Happens FIRST before ApplicationReadyEvent
     */
    @EventListener(ApplicationStartedEvent.class)
    @Order(1)
    public void populateExchangeList() {
        // check if db was populated today already, if not - clear and get new data
        List exList = exchangeRepo.findAll();
        if(null != exList) {
            Optional<StockExchange> el = exList.stream().findFirst();
            if(el.isPresent() && el.get().getDate().getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                //was populated today - skip
                return;
            }
        }
        exList = api.getExchangeList();
        exList = exchangeRepo.saveAll(exList);
        log.info("ADDED TO DB:");
        exList.forEach(e -> log.info(e.toString()));
    }

    /**
     * Happens FIRST before ApplicationReadyEvent
     */
    @EventListener(ApplicationStartedEvent.class)
    @Order(2)
    public void populateStockList() {
        // check if db was populated today already, if not - clear and get new data
        List<StockExchange> exList = exchangeRepo.findAll();

        try {
            exList.forEach(e -> {
                List ssList = symbolRepository.findByExchangeCode(e.getCode());
                // checking for null here must be covered by unit tests
                Optional<StockSymbol> el = ssList.stream().findFirst();
                if (el.isPresent() && el.get().getDate().getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                    //was populated today - skip
                    return;
                }
                // delete symbols for current exchange code
                symbolRepository.deleteByExchangeCode(e.getCode());
                // populate symbol with exchange code (IMPORTANT for further linking Stocks to Exchanges)
                List<StockSymbol> symList = api.getStockSymbolList(e.getCode());
                symList.parallelStream().forEach(s -> {
                    s.setExchangeCode(e.getCode());
                });
                // save Stock Symbols
                symList = symbolRepository.saveAll(symList);
                log.info(String.format("Saved %d Stock Symbols for %s exchange", symList.size(), e.getCode()));
            });
        } catch (HttpClientErrorException hce) {
            // if too many requests, sleep for a minute and continue
            if(hce.getMessage().contains(HTTP_TOO_MANY_REQUESTS)) {
                sleep();
                populateStockList();
            }
        }
    }

    private void sleep() {
        final long sleepTime = 60000;
        try {
            log.info(String.format("Sleeping for %d...", sleepTime));
            Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
            log.info(String.format("Sleep for %d seconds got interrupted - InterruptedException", sleepTime));
        }
    }


//    @Scheduled(fixedDelay = 1000)
    public void getQuotes() {
        StockQuote quote = api.getQuote("BIRG.IR");
        quote = quoteRepository.save(quote);
        System.out.println(quote);
    }
}
