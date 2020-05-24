package com.artjomsporss.restapiaggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
// expects a properties file outside main folder
@PropertySource("file:../external.properties")
public class FinnhubRestCallerImpl implements FinnHubRestCaller {

    // comes from external.properties
    @Value("${finnhub.token}")
    private String token;

    private static final String URL = "https://finnhub.io/api/v1/stock/";

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

    public StockExchange[] getExchange() {
        return rest.getForObject(generateUrl("exchange"), StockExchange[].class);
    }

    protected String generateUrl(String endpoint) {
        return URL + endpoint + generateToken();
    }

    protected String generateToken() {
        return "?token=" + token;
    }
}
