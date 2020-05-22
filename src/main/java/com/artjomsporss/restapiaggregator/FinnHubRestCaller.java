package com.artjomsporss.restapiaggregator;

public interface FinnHubRestCaller {
    /**
     * Get a list of stock exchanges
     * @return array of stock exchanges
     */
    StockExchange[] getExchange();
}
