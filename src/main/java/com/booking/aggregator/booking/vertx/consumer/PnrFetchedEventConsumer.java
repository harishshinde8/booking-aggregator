package com.booking.aggregator.booking.vertx.consumer;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PnrFetchedEventConsumer {

    private final Vertx vertx;

    public PnrFetchedEventConsumer(Vertx vertx) {
        this.vertx = vertx;
    }

    @PostConstruct
    public void registerConsumer() {
        EventBus eventBus = vertx.eventBus();

        eventBus.consumer("pnr.fetched", message -> {
            log.info("Fetch Pnr events | Received PNR fetched event  {}", message.body());

            // Send a notification
            try {
                // Send notifications
                log.info("Fetch Pnr events | Processed event successfully for PNR {}", message.body());
            } catch (Exception e) {
                log.error("Fetch Pnr events | Error while processing PNR fetched event", e);
            }
        });

        log.info("Fetch Pnr events | Vert.x EventBus consumer registered for 'pnr.fetched' topic");
    }
}
