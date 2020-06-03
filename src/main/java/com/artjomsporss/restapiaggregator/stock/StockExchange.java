package com.artjomsporss.restapiaggregator.stock;

import com.artjomsporss.restapiaggregator.common.ApiElement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;


/**
 * Contains data about various Stock Exchanges
 * Sample Request: https://finnhub.io/api/v1/stock/exchange?token=123123123123
 * Sample response: [{code=US, currency=USD, name=US exchanges},{...}]
 */
@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockExchange extends ApiElement {
    private String _id;
    private String code;
    private String currency;
    private String name;
}
