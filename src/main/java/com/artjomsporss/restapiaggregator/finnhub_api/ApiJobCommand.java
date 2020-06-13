package com.artjomsporss.restapiaggregator.finnhub_api;

/**
 * Encapsulates API call with corresponding dependencies.
 * Implementation chooses how to perform the action.
 */
public interface ApiJobCommand {
    /**
     * Executes the job using encapsulated fields.
     * Used primarily to queue jobs for execution.
     * @return true if job was executed, false otherwise
     */
    boolean execute();
}
