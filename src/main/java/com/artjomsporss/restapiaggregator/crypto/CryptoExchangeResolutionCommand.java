package com.artjomsporss.restapiaggregator.crypto;

import com.artjomsporss.restapiaggregator.FinnHubRestCaller;
import com.artjomsporss.restapiaggregator.finnhub_api.ApiJobCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CryptoExchangeResolutionCommand implements ApiJobCommand {

    private final Logger log = LoggerFactory.getLogger(CryptoExchangeResolutionCommand.class);
    // key: Exchange / value: Resolution
    private final Map<String, List<String>> exchangesAndResolutions;
    private final String symbol;
    private final String exchangeName;
    private final FinnHubRestCaller restCaller;
    private final CryptoCandle.Resolution resolution;

    public CryptoExchangeResolutionCommand(CryptoCandle.Resolution resolution, String symbol, String exchangeName,
                                           FinnHubRestCaller restCaller,
                                           Map<String, List<String>> exchangesAndResolutions) {
        this.resolution = resolution;
        this.symbol = symbol;
        this.exchangeName = exchangeName;
        this.restCaller = restCaller;
        this.exchangesAndResolutions = exchangesAndResolutions;
    }

    @Override
    public boolean execute() {
        log.info(String.format("Preparing a call"));
        // get dates
        String from = generateFromDate();
        String to = Long.toString(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        try {
            // get data from APO
            ApiCryptoCandle candleWrapper = restCaller.getCryptoCandles(symbol, resolution.toString(), from, to);
            // get candles
            List<CryptoCandle> candleList = candleWrapper.getCandles();
            if(!candleList.isEmpty()) {
                log.info(String.format("[%d] results found", candleList.size()));
                // add data to map for future processing. Once all data is collected,
                // CryptoExchanges from cryptoExchangeRepo will be updated using it
                addResolutionToMap();
            } else {
                log.info("Nothing found");
            }
            return true;
        } catch (HttpClientErrorException hce) {
            return false;
        }
    }

    private void addResolutionToMap() {
        List<String> resolutions = exchangesAndResolutions.get(exchangeName);
        if(resolutions == null) {
            resolutions = new ArrayList<>();
        }
        resolutions.add(resolution.toString());
        exchangesAndResolutions.put(exchangeName, resolutions);
    }

    private String generateFromDate() {
        LocalDateTime date = null;
        switch(resolution) {
            case ONE:
            case FIVE:
            case FIFTEEN:
            case THIRTY:
            case SIXTY: date = LocalDateTime.now().minusHours(8); break;
            case DAY:
            case WEEK:
            case MONTH:
            default: date = LocalDateTime.now().minusMonths(2); break;
        }
        return Long.toString(date.toEpochSecond(ZoneOffset.UTC));
    }

}
