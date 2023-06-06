package org.doogle.cdc.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DebeziumService {

    public void logEvents() {
        Log.info("Event Received");
    }
}
