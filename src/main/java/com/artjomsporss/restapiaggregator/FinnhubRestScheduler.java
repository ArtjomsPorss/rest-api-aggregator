package com.artjomsporss.restapiaggregator;

import com.artjomsporss.restapiaggregator.api_jobs.ApiJobCommand;
import com.artjomsporss.restapiaggregator.crypto.CryptoService;
import com.artjomsporss.restapiaggregator.crypto.CryptoSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class FinnhubRestScheduler {
    private final Logger log = LoggerFactory.getLogger(FinnhubRestScheduler.class);

    @Value("${sleeptime}")
    private Long sleepTime;

    @Autowired
    private CryptoService cryptoService;


    //TODO  jobqueue
    Queue<ApiJobCommand> jobQueue;

    @PostConstruct
    public void init(){
        this.jobQueue = new ArrayDeque<>();
    }

    /*
        if queue is empty
            populate the queue
                get all candles that have not been populated in DB during last hour - pick first X (several hundred??)
                create job objects (with corresponding db calls)
        else
            attempt to execute call
                if exception - sleep, then attempt to execute call again
            call executed - pop it off the queue
            go back to checking the queue
 */
    //TODO method at application start
    //TODO make this using event listener instead of looping
    @EventListener(ApplicationStartedEvent.class)
    public void startAggregationJob() {
        do {
            try {
                if (this.jobQueue.isEmpty()) {
                    populateQueue();
                } else {
                    ApiJobCommand job = this.jobQueue.peek();
                    job.execute();
                    this.jobQueue.remove();
                }
            } catch(HttpClientErrorException hcee) {
                log.debug("Reached time limit");
            }
        } while(true);
    }

    private void populateQueue() {
        int amountOfJobs = 1000;
        this.jobQueue.addAll(this.getJobs(amountOfJobs));
    }

    private Collection<? extends ApiJobCommand> getJobs(int amountOfJobs) {
        List<ApiJobCommand> gatheredJobs = new ArrayList<>(amountOfJobs);
        gatheredJobs.addAll(getCandleJobs(amountOfJobs));

        return gatheredJobs;
    }

    /**
     * Find Currencies in all Crypto exchanges that don't have candle data during last hour.
     * If current time is 12:31, the DB will be checked for data between 11:00 - 11:59.
     * If no data present, these exchanges-currencies will be returned in form of CandleJobs.
     * @param amountOfJobs amount of jobs expected to be populated
     * @return gathered CandleJobs
     */
    private Collection<? extends ApiJobCommand> getCandleJobs(int amountOfJobs) {
        List<ApiJobCommand> gatheredCandleJobs = new ArrayList<>(amountOfJobs);
        // now 11:59 / 12:00 / 12:31
        LocalDateTime to = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);   // 11:00 / 12:00 / 12:00
        LocalDateTime from = to.minusHours(1); // 10:00 / 11:00 / 11:00
        to = to.minusMinutes(1); // 10:59 / 11:59 / 11:59

        while(gatheredCandleJobs.size() < amountOfJobs) {
            gatheredCandleJobs.addAll(findCandlesNotGatheredDuring(from, to, amountOfJobs));
        }
        return gatheredCandleJobs;
    }

    private Collection<? extends ApiJobCommand> findCandlesNotGatheredDuring(LocalDateTime from, LocalDateTime to, int amountOfJobs) {
        List<CryptoSymbol> cryptoSymbol = cryptoService.findDetailsForMissingCandles(from, to, amountOfJobs);
        return null;//TODO fix return value
    }

    private void sleep() {
        try {
            log.debug(String.format("Sleeping for %d seconds...", this.sleepTime/1000));
            Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
            log.warn(String.format("Sleep for %d seconds got interrupted - InterruptedException", this.sleepTime));
        }
    }

}
