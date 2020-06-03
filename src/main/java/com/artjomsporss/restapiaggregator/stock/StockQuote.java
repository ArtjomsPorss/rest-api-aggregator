package com.artjomsporss.restapiaggregator.stock;

import com.artjomsporss.restapiaggregator.common.ApiElement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockQuote extends ApiElement {
    private String _id;
    //    Open price of the day
    private String o;

    //    High price of the day
    private String h;

    //    Low price of the day
    private String l;

    //    Current price
    private String c;

    //    Previous close price
    private String pc;
}
