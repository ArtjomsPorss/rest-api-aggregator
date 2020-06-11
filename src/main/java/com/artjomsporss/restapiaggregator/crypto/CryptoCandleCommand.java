package com.artjomsporss.restapiaggregator.crypto;

import com.artjomsporss.restapiaggregator.FinnHubRestCaller;
import com.artjomsporss.restapiaggregator.api_jobs.ApiJobCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

public class CryptoCandleCommand implements ApiJobCommand {
    Logger log = LoggerFactory.getLogger(CryptoCandleCommand.class);

    private String exchangeSymbol;
    private String from;
    private String to;
    private String resolution;
    private FinnHubRestCaller restCaller;
    private CryptoCandleRepository repository;

    public CryptoCandleCommand(String exchangeSymbol, String from, String to,
                               String resolution, FinnHubRestCaller restCaller
            , CryptoCandleRepository cryptoCandleRepository) {
        this.exchangeSymbol = exchangeSymbol;
        this.from = from;
        this.to = to;
        this.resolution = resolution;
        this.restCaller = restCaller;
        this.repository = cryptoCandleRepository;
    }

    public static List<CryptoCandleCommand> createAll(
            List<String> requiredSymbols, String from, String to,
            String resolution, FinnHubRestCaller restCaller, CryptoCandleRepository cryptoCandleRepository) {
        return requiredSymbols.stream()
                .map(s -> new CryptoCandleCommand(s, from, to, resolution, restCaller, cryptoCandleRepository))
                .collect(Collectors.toList());
    }

    @Override
    public boolean execute() {
        try {
            ApiCryptoCandle candleObject = restCaller.getCryptoCandles(exchangeSymbol, resolution, from, to);
            List<CryptoCandle> candles = candleObject.getCandles();
            log.debug(String.format("Symbol[%s] candles received[%d]", candleObject.getExchangeSymbol(), candles.size()));
            if(!candles.isEmpty()) {
                repository.saveAll(candles);
            }
            return true;
        } catch (HttpClientErrorException hce) {
            return false;
        }
    }
}
