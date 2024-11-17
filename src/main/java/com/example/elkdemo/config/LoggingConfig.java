package com.example.elkdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Value("${elk.logging.enabled}")
    private boolean elkLoggingEnabled;

    public boolean isElkLoggingEnabled() {
        return elkLoggingEnabled;
    }
}
