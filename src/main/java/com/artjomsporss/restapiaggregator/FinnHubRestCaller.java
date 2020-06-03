package com.artjomsporss.restapiaggregator;

import com.artjomsporss.restapiaggregator.stock.StockExchange;
import com.artjomsporss.restapiaggregator.stock.StockQuote;
import com.artjomsporss.restapiaggregator.stock.StockSymbol;

import java.util.List;

public interface FinnHubRestCaller {
    /**
     * Get a list of stock exchanges
     * @return array of stock exchanges
     */
    List<StockExchange> getExchangeList();

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
    StockQuote getQuote(String stockSymbol);
}
