package com.artjomsporss.restapiaggregator;

import com.artjomsporss.restapiaggregator.crypto.CryptoCandle;
import com.artjomsporss.restapiaggregator.crypto.CryptoExchange;
import com.artjomsporss.restapiaggregator.crypto.CryptoSymbol;
import com.artjomsporss.restapiaggregator.stock.StockExchange;
import com.artjomsporss.restapiaggregator.stock.StockQuote;
import com.artjomsporss.restapiaggregator.stock.StockSymbol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * This class is setup to call external api of Finnhub
 *
 * class expects external.properties containing:
 * finnhub.token=your_finnhub_token (get your token at https://finnhub.io/)
 *
 * spring.data.mongodb.database=your_db_name
 * spring.data.mongodb.username=your_db_user
 * spring.data.mongodb.password=db_user_password
 *
 */
@Service
@EnableScheduling
// expects a properties file outside main folder
@PropertySource("file:../external.properties")
public class FinnhubRestCallerImpl implements FinnHubRestCaller {

    // comes from external.properties
    @Value("${finnhub.token}")
    private String token;

    private static final String URL = "https://finnhub.io/api/v1/";

    private RestTemplate rest;

    @PostConstruct
    protected void setup() {
        this.rest = new RestTemplate();
        // setting token for every request
        this.setRequestToken();
    }

    protected void setRequestToken() {
        assert this.rest != null : "RestTemplate must be initialised";
        Map<String, String> defaultVars = new HashMap<>();
        defaultVars.put("token", token);
        rest.setDefaultUriVariables(defaultVars);
    }

    public List<StockExchange> getStockExchangeList() {
        StockExchange[] arr = rest.getForObject(String.format(URL + "stock/exchange?token=%s", token), StockExchange[].class);
        return Arrays.stream(arr).map(cs -> {
            cs.initDate();
            return cs;
        }).collect(Collectors.toList());
    }

    public List<StockSymbol> getStockSymbolList(String exchange) {
        StockSymbol[] arr = rest.getForObject(String.format(URL + "stock/symbol?exchange=%s&token=%s", exchange, token), StockSymbol[].class);
        return Arrays.stream(arr).map(cs -> {
            cs.initDate();
            return cs;
        }).collect(Collectors.toList());
    }

    public StockQuote getStockQuote(String stockSymbol) {
        StockQuote quote = rest.getForObject(String.format(URL + "quote?symbol=%s&token=%s", stockSymbol, token), StockQuote.class);
        quote.initDate();
        return quote;
    }

    public List<CryptoExchange> getCryptoExchanges() {
        String[] exArr = rest.getForObject(String.format(URL + "crypto/exchange?token=%s", token), String[].class);
        return Arrays.stream(exArr).map(es -> {
            CryptoExchange ex = new CryptoExchange();
            ex.setExchangeName(es);
            ex.initDate();
            return ex;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CryptoSymbol> getCryptoSymbols(String exchange) {
        CryptoSymbol[] csArr = rest.getForObject(String.format(URL + "crypto/symbol?exchange=%s&token=%s", exchange, token), CryptoSymbol[].class);
        return Arrays.stream(csArr).map(cs -> {
            cs.initDate();
            return cs;
        }).collect(Collectors.toList());
    }

    @Override
    public CryptoCandle getCryptoCandles(String exchange, String symbol, String resolution, long from, long to) {
        return rest.getForObject(String.format(URL + "candle?symbol=%s:%s&resolution=%s&from=%d&to=%d&token=%s", exchange, symbol, resolution, from, to, token), CryptoCandle.class);
    }

    protected Map <String, String> params(String k, String v) {
        if(k != null && v != null && !k.isBlank() && !v.isBlank()) {
            return Map.of(k, v, "token", token);
        } else {
            return Map.of("token", token);
        }
    }

    protected String generateUrl(String endpoint) {
        return URL + endpoint;
    }

    protected String generateToken() {
        return "?token=" + token;
    }
}

