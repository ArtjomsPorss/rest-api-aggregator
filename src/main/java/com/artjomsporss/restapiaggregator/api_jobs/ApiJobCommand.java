package com.artjomsporss.restapiaggregator.api_jobs;

import org.springframework.web.client.HttpClientErrorException;

/**
 * Encapsulates API call with corresponding dependencies.
 * Implementation chooses how to perform the action.
 */
public interface ApiJobCommand {
    /**
     * Executes job using stored data
     * @throws HttpClientErrorException if rest api reached call limit
     */
    void execute() throws HttpClientErrorException;
}
