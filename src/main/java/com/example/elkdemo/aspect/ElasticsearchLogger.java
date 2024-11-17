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

    public void logToElasticsearch(String action, String status,String httpMethod, Object... bodyData) {

            try {
                //TODO Mahsun  refactor this class
                JSONObject logJson = new JSONObject();
                logJson.put("action", action);
                logJson.put("status", status);
                logJson.put("timestamp", System.currentTimeMillis());

                for (int i = 0; i < bodyData.length; i += 2) {
                    if (i + 1 < bodyData.length && bodyData[i] instanceof String) {
                        logJson.put((String) bodyData[i], bodyData[i + 1]);
                    }
                }
                sendToElasticsearch(logJson, httpMethod);
            } catch (Exception e) {
                log.error("Failed to log to Elasticsearch", e);
            }

    }
    private void sendToElasticsearch(JSONObject logJson, String httpMethod) {
        try {
            URL url = new URL(ELASTICSEARCH_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(httpMethod);
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

