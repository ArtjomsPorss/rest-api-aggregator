package com.artjomsporss.restapiaggregator;

import com.artjomsporss.restapiaggregator.exchange.Exchange;

public interface FinnHubRestCaller {
    /**
     * Get a list of stock exchanges
     * @return array of stock exchanges
     */
    Exchange[] getExchange();
}
