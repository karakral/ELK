package com.example.elkdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@Slf4j
public class ElasticsearchLogger {

    private final String ELASTICSEARCH_URL = "http://localhost:9200/application-logs/_doc";

    public void logToElasticsearch(String action, String status, Object... details) {
        try {
            JSONObject logJson = new JSONObject();
            logJson.put("action", action);
            logJson.put("status", status);
            logJson.put("timestamp", System.currentTimeMillis());

            for (int i = 0; i < details.length; i += 2) {
                if (i + 1 < details.length && details[i] instanceof String) {
                    logJson.put((String) details[i], details[i + 1]);
                }
            }
            sendToElasticsearch(logJson);
        } catch (Exception e) {
            log.error("Failed to log to Elasticsearch", e);
        }
    }

    private void sendToElasticsearch(JSONObject logJson) {
        try {
            URL url = new URL(ELASTICSEARCH_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(logJson.toString());
            }
            int responseCode = connection.getResponseCode();
            if (responseCode != 201) {
                log.error("Failed to send log to Elasticsearch. Response code: {}", responseCode);
            }
        } catch (Exception e) {
            log.error("Error sending log to Elasticsearch", e);
        }
    }
}

