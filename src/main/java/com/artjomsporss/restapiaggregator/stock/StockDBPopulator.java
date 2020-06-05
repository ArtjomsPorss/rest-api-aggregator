package com.artjomsporss.restapiaggregator.stock;

import com.artjomsporss.restapiaggregator.FinnHubRestCaller;
import com.artjomsporss.restapiaggregator.common.ApiElement;
import com.artjomsporss.restapiaggregator.crypto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * The goal of this service is prepopulate db (if required)
 * with absolutely necessary data such as exchanges and stocks.
 * This data will be used later to query using cron job
 *
 * TODO looks like either @EventListener or @Scheduled runs method in a separate thread.
 * TODO To make @EventListener annotated methods run synchronously, try calling them from @PostConstruct annotated method instead.
 */
@Service
public class StockDBPopulator {

    private final Logger log = LoggerFactory.getLogger(StockDBPopulator.class);
    public static final String HTTP_TOO_MANY_REQUESTS = "429 Too Many Requests: [API limit reached. Please try again later.]";

    @Autowired
    FinnHubRestCaller api;
    @Autowired
    StockExchangeRepository stockExchangeRepo;
    @Autowired
    StockSymbolRepository stockSymbolRepository;
    @Autowired
    StockQuoteRepository stockQuoteRepository;
    @Autowired
    CryptoExchangeRepository cryptoExchangeRepo;
    @Autowired
    private CryptoSymbolRepository cryptoSymbolRepo;
    @Autowired
    private CryptoCandleRepository cryptoCandleRepo;


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
        List exList = stockExchangeRepo.findAll();
        if(null != exList) {
            if(isDateToday(exList)) {
                //was populated today - skip
                return;
            }
        }
        exList = api.getStockExchangeList();
        exList = stockExchangeRepo.saveAll(exList);
        log.info("Saved Stock Echanges:");
        exList.forEach(e -> {log.info(e.toString());});
    }

    /**
     * Happens FIRST before ApplicationReadyEvent
     * TODO some exchanges don't have stock data -ignore them
     */
//    @EventListener(ApplicationStartedEvent.class)
//    @Order(2)
    public void populateStockSymbolList() {
        // check if db was populated today already, if not - clear and get new data
        List<StockExchange> exList = stockExchangeRepo.findAll();

        try {
            exList.forEach(e -> {
                List<StockSymbol> ssList = stockSymbolRepository.findByExchangeCode(e.getCode());
                if (isDateToday(ssList)) {
                    //was populated today - skip
                    return;
                }
                // delete symbols for current exchange code
                stockSymbolRepository.deleteByExchangeCode(e.getCode());
                // populate symbol with exchange code (IMPORTANT for further linking Stocks to Exchanges)
                List<StockSymbol> symList = api.getStockSymbolList(e.getCode());
                symList.parallelStream().forEach(s -> {
                    s.setExchangeCode(e.getCode());
                });
                // save Stock Symbols
                symList = stockSymbolRepository.saveAll(symList);
                log.info(String.format("Saved %d Stock Symbols for %s exchange", symList.size(), e.getCode()));
            });
        } catch (HttpClientErrorException hce) { //TODO replace with a framework that calculates call per minute, calls, maybe: handles error and waits accordingly
            // if too many requests, sleep for a minute and continue
            if(hce.getMessage().contains(HTTP_TOO_MANY_REQUESTS)) {
                sleep();
                populateStockSymbolList();
            }
        }
    }

    /**
     * Populate db with crypto exchanges that
     */
    @EventListener(ApplicationStartedEvent.class)
    @Order(3)
    public void populateCryptoExchangeList() {
        List<CryptoExchange> ceList = cryptoExchangeRepo.findAll();
        if(this.isDateToday(ceList)){
            return;
        }
        ceList = api.getCryptoExchanges();
        cryptoExchangeRepo.deleteAll();
        ceList = cryptoExchangeRepo.saveAll(ceList);
        log.info("Saved Crypto Echanges");
        ceList.forEach(e -> log.info(e.toString()));
    }

    /**
     * Populate crypto currencies for all exchanges
     */
    @EventListener(ApplicationStartedEvent.class)
    @Order(4)
    public void populateCryptoSymbolList() {
        List<CryptoExchange> ceList = cryptoExchangeRepo.findAll();
        try {
            ceList.forEach(ce -> {
                List<CryptoSymbol> csList = cryptoSymbolRepo.findByExchangeName(ce.getExchangeName());
                // if symbols for current exchanges were updated today - skip
                if (isDateToday(csList)) {
                    return;
                }
                csList = api.getCryptoSymbols(ce.getExchangeName());
                // exchange may be needed to set for future referencing
                csList.forEach(e -> e.setExchangeName(ce.getExchangeName()));
                // delete old data, if present
                cryptoSymbolRepo.deleteByExchangeName(ce.getExchangeName());
                // save new data
                csList = cryptoSymbolRepo.saveAll(csList);
                // logging
                log.info("Saved Crypto Echanges");
                csList.forEach(cs -> log.info(cs.toString()));
            });
        } catch (HttpClientErrorException hce) { //TODO replace with a framework that calculates call per minute, calls, maybe: handles error and waits accordingly
                // if too many requests, sleep for a minute and continue
                if(hce.getMessage().contains(HTTP_TOO_MANY_REQUESTS)) {
                    sleep();
                    populateCryptoSymbolList();
                }
        }
    }

    @EventListener(ApplicationStartedEvent.class)
    @Order(5)
    public void getCryptoCandles() {
//    public void getCryptoCandles(String exchange, String symbol, String resolution, long from, long to) {
        long t1 = LocalDateTime.of(2020, 06, 1,0,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        long t2 = LocalDateTime.of(2020, 06, 1,1,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        ApiCryptoCandle cc = api.getCryptoCandles("BINANCE","FUNBTC", ApiCryptoCandle.MINUTE_1, t1, t2);
        log.info(cc.toString());

        t1 = LocalDateTime.of(2020, 06, 1,0,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        t2 = LocalDateTime.of(2020, 06, 1,1,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        cc = api.getCryptoCandles("BINANCE","FUNBTC", ApiCryptoCandle.MINUTE_5, t1, t2);
        log.info(cc.toString());

        t1 = LocalDateTime.of(2020, 06, 1,0,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        t2 = LocalDateTime.of(2020, 06, 1,1,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        cc = api.getCryptoCandles("BINANCE","FUNBTC", ApiCryptoCandle.MINUTE_15, t1, t2);
        log.info(cc.toString());

        t1 = LocalDateTime.of(2020, 06, 1,0,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        t2 = LocalDateTime.of(2020, 06, 1,1,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        cc = api.getCryptoCandles("BINANCE","FUNBTC", ApiCryptoCandle.MINUTE_30, t1, t2);
        log.info(cc.toString());

        t1 = LocalDateTime.of(2020, 06, 1,0,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        t2 = LocalDateTime.of(2020, 06, 1,1,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        cc = api.getCryptoCandles("BINANCE","FUNBTC", ApiCryptoCandle.MINUTE_60, t1, t2);
        log.info(cc.toString());

        t1 = LocalDateTime.of(2020, 06, 1,0,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        t2 = LocalDateTime.of(2020, 06, 1,1,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        cc = api.getCryptoCandles("BINANCE","FUNBTC", ApiCryptoCandle.DAY, t1, t2);
        log.info(cc.toString());

        t1 = LocalDateTime.of(2020, 06, 1,0,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        t2 = LocalDateTime.of(2020, 06, 1,1,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        cc = api.getCryptoCandles("BINANCE","FUNBTC", ApiCryptoCandle.WEEK, t1, t2);
        log.info(cc.toString());

        t1 = LocalDateTime.of(2020, 06, 1,0,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        t2 = LocalDateTime.of(2020, 06, 1,1,0).atZone(ZoneId.systemDefault()).toEpochSecond();
        cc = api.getCryptoCandles("BINANCE","FUNBTC", ApiCryptoCandle.MONTH, t1, t2);
        log.info(cc.toString());

//        cryptoSymbolRepo.findAll().forEach(s -> {
//            List<String> periods = List.of(CryptoCandle.MINUTE_1, CryptoCandle.MINUTE_5, CryptoCandle.MINUTE_15, CryptoCandle.MINUTE_30, CryptoCandle.MINUTE_60, CryptoCandle.DAY, CryptoCandle.MONTH);
//            periods.forEach(p -> {
//                try {
//                    CryptoCandle cc = api.getCryptoCandles(s.getExchangeName(), s.getSymbol(), p, t1, t2);
//                    // if data returned, save it
//                    if(cc.getS() == CryptoCandle.RESPONSE_OK) {
//                        log.info("candle found: " + cc.toString());
//                        cryptoCandleRepo.save(cc);
//                    }
//                    log.info("candle not found");
//                } catch(HttpClientErrorException hce) {
//                    sleep();
//                } catch(RestClientException rce) {
//
//                }
//            });
//        });


//        cc = cryptoCandleRepo.save(cc);
//        log.info(cc.toString());
    }

    //    @Scheduled(fixedDelay = 1000)
    public void getQuotes() {
        StockQuote quote = api.getStockQuote("BIRG.IR");
        quote = stockQuoteRepository.save(quote);
        System.out.println(quote);
    }

    private void sleep() {
        final long sleepTime = 60000;
        try {
            log.info(String.format("Sleeping for %d seconds...", sleepTime/1000));
            Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
            log.info(String.format("Sleep for %d seconds got interrupted - InterruptedException", sleepTime));
        }
    }

    private boolean isDateToday(List<? extends ApiElement> list) {
        Optional<? extends ApiElement> el = list.stream().findFirst();
        return el.isPresent() && el.get().getDate().getDayOfMonth() == LocalDateTime.now().getDayOfMonth();
    }
}
