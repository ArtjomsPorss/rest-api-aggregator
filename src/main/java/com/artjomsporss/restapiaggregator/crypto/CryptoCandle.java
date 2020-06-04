package com.artjomsporss.restapiaggregator.crypto;

import java.util.List;

/**
 * Contains candle data within specific period
 * Data is represented in lists, which are sequential order
 */
public class CryptoCandle {
    // resoltions
    public static final String MINUTE_1 = "1";
    public static final String MINUTE_5 = "5";
    public static final String MINUTE_15 = "15";
    public static final String MINUTE_30 = "30";
    public static final String MINUTE_60 = "60";
    public static final String DAY = "D";
    public static final String WEEK = "W";
    public static final String MONTH = "M";


    // used in s field
    public static final String RESPONSE_OK = "ok";
    public static final String RESPONSE_DATA = "no_data";

    // close prices for returned candles
    List<String> c;
    // high prices for returned candles
    List<String> h;
    // low prices for returned candles
    List<String> l;
    // open prices for returned candles
    List<String> o;
    // response status can be ok or no_data
    String s;
    // timestamps of returned candles
    List<String> t;
    // volume data for returned candles
    List<String> v;
}
