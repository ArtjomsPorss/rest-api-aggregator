package com.artjomsporss.restapiaggregator.crypto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private String c;

    /**
     * high price for returned candle
     */
    private String h;

    /**
     * low price for returned candle
     */
    private String l;

    /**
     * open price for returned candle
     */
    private String o;

    /**
     * timestamp of returned candle
     */
    private String t;

    /**
     * volume data for returned candle
     */
    private String v;

    /**
     * Stores the symbol of cryptocurrency, related to CryptoSymbol.class
     */
    private String symbol;

    /**
     * Contains different resolutions available for candles when calling API
     * When searched with minute, the API will try to find data - one per minute and return it as result.
     * Different exchanges support different resolutions, e.g. some exchange may have data per each five minute, others per each minute.
     * If there is no data for the resolution you asked, an empty result will be returned.
     */
    public enum Resolution {
        ONE("1"), FIVE("5"), FIFTEEN("15"), THIRTY("30"), SIXTY("60"), DAY("D"), WEEK("W"), MONTH("M");

        private String value;
        private static List<Resolution> list;

        private Resolution(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static List<Resolution> asList() {
            if(list == null) {
                list = Arrays.asList(ONE, FIVE, FIFTEEN, THIRTY, SIXTY, DAY, WEEK, MONTH);
            }
            return new ArrayList<>(list);
        }
    }
}
