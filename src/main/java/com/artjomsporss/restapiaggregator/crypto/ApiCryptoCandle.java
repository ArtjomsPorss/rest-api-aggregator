package com.artjomsporss.restapiaggregator.crypto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains candle data within specific period
 * Data is represented in lists, which are sequential order
 */
@Data
public class ApiCryptoCandle {
    // resolutions
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
    public static final String RESPONSE_NO_DATA = "no_data";

    /**
     * close prices for returned candles
     */
    String[] c;

    /**
     * high prices for returned candles
     */
    String[] h;

    /**
     * low prices for returned candles
     */
    String[] l;

    /**
     * open prices for returned candles
     */
    String[] o;

    /**
     * response status can be ok or no_data
     */
    String s;

    /**
     * timestamps of returned candles
     */
    String[] t;

    /**
     * volume data for returned candles
     */
    String[] v;

    /**
     * Create a list of List<CryptoCandle> from the values in the arrays of this ApiCryptoCandle object
     * @return
     */
    public List<CryptoCandle> getCandles() {
        List<CryptoCandle> list = new ArrayList<>();
        if(s.equals(RESPONSE_OK)
                && null != c && c.length > 0
                && null != h && null != l && null != o && null != t && null != v
                && c.length == h.length && h.length == l.length && l.length == o.length && o.length == t.length && t.length == v.length) {
            for (int i = 0; i < c.length; ++i) {
                CryptoCandle cc = new CryptoCandle();
                cc.setC(c[i]);
                cc.setH(h[i]);
                cc.setL(l[i]);
                cc.setO(o[i]);
                cc.setT(t[i]);
                cc.setV(v[i]);
                list.add(cc);
            }
        }
        return list;
    }

}
