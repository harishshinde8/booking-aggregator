package com.booking.aggregator.booking.config;

import com.booking.aggregator.booking.vertx.verticle.PnrWebSocketVerticle;
import io.vertx.core.Vertx;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VertxInitializer {

    private final Vertx vertx;

    @PostConstruct
    public void deployVerticles() {
        vertx.deployVerticle(new PnrWebSocketVerticle(), res -> {
            if (res.succeeded()) {
                log.info("PnrWebSocketVerticle deployed successfully (ID: {})", res.result());
            } else {
                log.error("Failed to deploy PnrWebSocketVerticle", res.cause());
            }
        });
    }
}
