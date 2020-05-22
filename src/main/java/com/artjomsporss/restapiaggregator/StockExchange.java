package com.artjomsporss.restapiaggregator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockExchange {
    String code;
    String currency;
    String name;
}
