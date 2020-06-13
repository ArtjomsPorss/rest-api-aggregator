package com.artjomsporss.restapiaggregator.scheduler;

import com.artjomsporss.restapiaggregator.finnhub_api.ApiJobCommand;
import com.artjomsporss.restapiaggregator.crypto.CryptoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class FinnhubRestScheduler extends RemoteAPIJobScheduler {
    private final Logger log = LoggerFactory.getLogger(FinnhubRestScheduler.class);

    @Autowired
    private CryptoService cryptoService;

    private Queue<ApiJobCommand> jobQueue;

    private int callcount;

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
//    @EventListener(ApplicationStartedEvent.class)
    public void startAggregationJob() {
        do {
            try {
                if (this.jobQueue.isEmpty()) {
                    populateQueue();
                } else {
                    ApiJobCommand job = this.jobQueue.peek();
                    if(job.execute()) {
                        log.debug(String.format("job call [%d] successfully executed, removing from queue", callcount++));
                        this.jobQueue.remove();
                    } else {
                        log.debug("Reached time limit, resetting call count");
                        callcount = 0;
                        sleep();
                    }
                }
            } catch(Exception e) {
                log.error("Unexpected error");
                e.printStackTrace();
            }
        } while(true);
    }

    private void populateQueue() {
        int amountOfJobs = 1000;
        this.jobQueue.addAll(this.getJobs(amountOfJobs));
    }

    private Collection<? extends ApiJobCommand> getJobs(int amountOfJobs) {
        List<ApiJobCommand> gatheredJobs = new ArrayList<>(amountOfJobs);
        gatheredJobs.addAll(getHourlyCandleJobs(amountOfJobs));

        return gatheredJobs;
    }

    /**
     * Find Currencies in all Crypto exchanges that don't have candle data during last hour.
     * If current time is 12:31, the DB will be checked for data between 11:00 - 11:59.
     * If no data present, these exchanges-currencies will be returned in form of CandleJobs.
     * @param amountOfJobs amount of jobs expected to be populated
     * @return gathered CandleJobs
     */
    private Collection<? extends ApiJobCommand> getHourlyCandleJobs(int amountOfJobs) {
        // resolution in minutes
        final String resolution = "1";

        List<ApiJobCommand> gatheredCandleJobs = new ArrayList<>(amountOfJobs);
        //
        //TODO temporarily added minusWeeks(1)
        // now 11:59 / 12:00 / 12:31
        LocalDateTime toDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusYears(1);   // 11:00 / 12:00 / 12:00
        LocalDateTime fromDate = toDate.minusHours(1); // 10:00 / 11:00 / 11:00
        toDate = toDate.minusMinutes(1); // 10:59 / 11:59 / 11:59

        String from = Long.toString(fromDate.toEpochSecond(ZoneOffset.UTC));
        String to = Long.toString(toDate.toEpochSecond(ZoneOffset.UTC));

        while(gatheredCandleJobs.size() < amountOfJobs) {
            gatheredCandleJobs.addAll(cryptoService.getCommandsForMissingCandles(from, to, resolution, amountOfJobs));
            from = Long.toString(fromDate.minusHours(1).toEpochSecond(ZoneOffset.UTC));
            to = Long.toString(toDate.minusHours(1).toEpochSecond(ZoneOffset.UTC));
        }
        return gatheredCandleJobs;
    }

}
