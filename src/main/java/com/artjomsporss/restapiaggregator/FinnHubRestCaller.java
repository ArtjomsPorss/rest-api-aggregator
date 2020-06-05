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

    ApiCryptoCandle getCryptoCandles(String exchange, String symbol, String resolution, long from, long to);
}
