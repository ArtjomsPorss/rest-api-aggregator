package com.artjomsporss.restapiaggregator.crypto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

/**
 * This object is used to store Candle data from API in repository
 * Object is created by ApiCryptoCandle
 * Contains candle data within specific period
 * Data is represented in lists, which are sequential order
 */
@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoCandle {

    /**
     * close price for returned candle
     */
    String c;

    /**
     * high price for returned candle
     */
    String h;

    /**
     * low price for returned candle
     */
    String l;

    /**
     * open price for returned candle
     */
    String o;

    /**
     * timestamp of returned candle
     */
    String t;

    /**
     * volume data for returned candle
     */
    String v;
}
