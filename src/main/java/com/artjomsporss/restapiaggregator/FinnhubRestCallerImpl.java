package com.artjomsporss.restapiaggregator;

import com.artjomsporss.restapiaggregator.common.ApiElement;
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

    public List<StockExchange> getExchangeList() {
        StockExchange[] arr = rest.getForObject(String.format(URL + "stock/exchange?token=%s", token), StockExchange[].class);
        List<StockExchange> list = Arrays.asList(arr);
        list.forEach(ApiElement::initDate);
        return list;
    }

    public List<StockSymbol> getStockSymbolList(String exchange) {
        StockSymbol[] arr = rest.getForObject(String.format(URL + "stock/symbol?exchange=%s&token=%s", exchange, token), StockSymbol[].class);
        List<StockSymbol> list = Arrays.asList(arr);
        list.forEach(ApiElement::initDate);
        return list;
    }

    public StockQuote getQuote(String stockSymbol) {
        StockQuote quote = rest.getForObject(String.format(URL + "quote?symbol=%s&token=%s", stockSymbol, token), StockQuote.class);
        quote.initDate();
        return quote;
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

