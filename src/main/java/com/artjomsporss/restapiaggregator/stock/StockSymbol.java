package com.artjomsporss.restapiaggregator.stock;

import com.artjomsporss.restapiaggregator.common.ApiElement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

/**
 * A stock that belongs to a specific exchange
 * Sample request: /stock/symbol?exchange=US
 * Sample response: [ { "description": "AGILENT TECHNOLOGIES INC", "displaySymbol": "A", "symbol": "A" }, {...} ]
 */
@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockSymbol extends ApiElement {
    private String _id;
    private String description;
    private String displaySymbol;
    private String symbol;
    private String exchangeCode;
}
