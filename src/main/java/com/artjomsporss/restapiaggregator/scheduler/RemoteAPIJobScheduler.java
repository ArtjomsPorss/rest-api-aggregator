package com.artjomsporss.restapiaggregator.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public abstract class RemoteAPIJobScheduler {

    @Value("${sleeptime}")
    private Long sleepTime;

    private final Logger log = LoggerFactory.getLogger(FinnhubRestScheduler.class);

    protected void sleep() {
        try {
            log.debug(String.format("Sleeping for %d seconds...", this.sleepTime/1000));
            Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
            log.warn(String.format("Sleep for %d seconds got interrupted - InterruptedException", this.sleepTime));
        }
    }
}
