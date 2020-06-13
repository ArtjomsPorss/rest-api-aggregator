package com.artjomsporss.restapiaggregator.crypto;

import com.artjomsporss.restapiaggregator.FinnHubRestCaller;
import com.artjomsporss.restapiaggregator.finnhub_api.ApiJobCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
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
            /*
            TODO generateTimes() is used temporarily for testing - getting last 8 hours only to see how well the data is populated on Finnhub.
            Time is generated dynamically because it allows to actually catch the latest data
             */
            generateTimes();

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

    private void generateTimes() {
        this.from = Long.toString(LocalDateTime.now().truncatedTo(ChronoField.MINUTE_OF_HOUR.getBaseUnit()).minusHours(9).minusMinutes(15).toEpochSecond(ZoneOffset.UTC));
        this.to = Long.toString(LocalDateTime.now().truncatedTo(ChronoField.MINUTE_OF_HOUR.getBaseUnit()).toEpochSecond(ZoneOffset.UTC));
    }
}
