package com.ilegra.holiexpress.common;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Promise;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BaseMicroserviceVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMicroserviceVerticle.class);

    protected ServiceDiscovery discovery;
    protected CircuitBreaker circuitBreaker;
    protected Set<Record> registeredRecords = new ConcurrentHashSet<>();

    @Override
    public void start() {
        discovery = ServiceDiscovery.create(vertx,
                new ServiceDiscoveryOptions()
                        .setBackendConfiguration(config()
                                .getJsonObject("service-discovery", new JsonObject())));
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        List<Promise<Void>> promises = new ArrayList<>();
        registeredRecords.forEach(record -> {
            Promise<Void> cleanup = Promise.promise();
            promises.add(cleanup);
            discovery.unpublish(record.getRegistration(), cleanup.future());
        });

        if (promises.isEmpty()) {
            discovery.close();
            stopPromise.complete();
            LOGGER.info("Service Discovery has been closed");
        } else {
            CompositeFuture.all(promises.stream().map(Promise::future).collect(Collectors.toList()))
                    .onComplete(asyncResult -> {
                        discovery.close();
                        if (asyncResult.failed()) {
                            LOGGER.error("Failed to close service discovery", asyncResult.cause());
                            stopPromise.fail(asyncResult.cause());
                        } else {
                            LOGGER.info("Service Discovery has been closed");
                            stopPromise.complete();
                        }
                    });
        }
    }

    protected Promise<Void> publishHttpEndpoint(String name, String host, int port) {
        Record record = HttpEndpoint.createRecord(name, host, port, "/",
                new JsonObject().put("api.name", config().getString("api.name", ""))
        );
        return publish(record);
    }

    protected Promise<Void> publish(Record record) {
        if (discovery == null) {
            start();
        }

        Promise<Void> promise = Promise.promise();

        discovery.publish(record, asyncResult -> {
            if (asyncResult.succeeded()) {
                LOGGER.info("Service [" + record.getName() + "] has been published");
                registeredRecords.add(record);
                promise.complete();
            } else {
                LOGGER.error("Failed to publish service [" + record.getName() + "]", asyncResult.cause());
                promise.fail(asyncResult.cause());
            }
        });

        return promise;
    }
}
