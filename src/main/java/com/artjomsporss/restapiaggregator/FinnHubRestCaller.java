package com.artjomsporss.restapiaggregator;

import com.artjomsporss.restapiaggregator.crypto.ApiCryptoCandle;
import com.artjomsporss.restapiaggregator.crypto.CryptoExchange;
import com.artjomsporss.restapiaggregator.crypto.CryptoSymbol;
import com.artjomsporss.restapiaggregator.stock.StockExchange;
import com.artjomsporss.restapiaggregator.stock.StockQuote;
import com.artjomsporss.restapiaggregator.stock.StockSymbol;

import java.util.List;

public interface FinnHubRestCaller {
    /**
     * Get a list of stock exchanges
     * @return array of stock exchanges
     */
    List<StockExchange> getStockExchangeList();

    /**
     * Get a list of stocks for a specific exchange
     * @return a list of stocks
     */
    List<StockSymbol> getStockSymbolList(String exchange);

    /**
     * Get a real time quote for specific stock
     * @param stockSymbol
     * @return
     */
    StockQuote getStockQuote(String stockSymbol);

    /**
     * Get a list of crypto exchanges
     * @return List<CryptoExchange>
     */
    List<CryptoExchange> getCryptoExchanges();

    /**
     * Get a list of crypto currencies traded at specific exchange
     * @param exchange
     * @return List<CryptoSymbol>
     */
    List<CryptoSymbol> getCryptoSymbols(String exchange);

    /**
     * Get cryptocandles using provided data
     * @param exchangeSymbol for which get candles
     * @param resolution differs from seconds to days, months e.g: 1, 5, 15, 30, 60, D, W, M
     * @param from when to get candles
     * @param to when get candles
     * @return a list of candles
     */
    ApiCryptoCandle getCryptoCandles(String exchangeSymbol, String resolution, String from, String to);
}
