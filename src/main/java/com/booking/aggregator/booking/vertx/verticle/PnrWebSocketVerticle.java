package com.booking.aggregator.booking.vertx.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.HttpServer;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PnrWebSocketVerticle extends AbstractVerticle {

    private final Set<ServerWebSocket> connectedClients = ConcurrentHashMap.newKeySet();

    @Override
    public void start(Promise<Void> startPromise) {
        EventBus eventBus = vertx.eventBus();

        // Create HTTP server for WebSocket
        HttpServer server = vertx.createHttpServer();

        server.webSocketHandler(socket -> {
            if ("/pnr-updates".equals(socket.path())) {
                connectedClients.add(socket);
                log.info("Client connected: {}", socket.remoteAddress());

                socket.closeHandler(v -> {
                    connectedClients.remove(socket);
                    log.info("Client disconnected: {}", socket.remoteAddress());
                });
            } else {
                socket.reject();
            }
        }).listen(9999, http -> {
            if (http.succeeded()) {
                log.info("WebSocket server started on ws://localhost:9999/pnr-updates");
                startPromise.complete();
            } else {
                log.error("Failed to start WebSocket server", http.cause());
                startPromise.fail(http.cause());
            }
        });

        // Listen to "pnr.fetched" events on Vert.x EventBus
        eventBus.consumer("pnr.fetched", message -> {
            String payload = message.body().toString();
            log.info("Broadcasting PNR fetched event to WebSocket clients: {}", payload);
            broadcastToClients(payload);
        });
    }

    private void broadcastToClients(String message) {
        connectedClients.forEach(client -> {
            if (!client.isClosed()) {
                client.writeTextMessage(message);
            }
        });
    }
}