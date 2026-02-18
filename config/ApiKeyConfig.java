package edu.usc.csci310.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyHolder {

    @Value("${api.key}")
    private String apiKey;

//    VWaLngE0ePnBA7PZECEEzk3QritRKGaHmRTWnXCK

    // Getter
    public String getApiKey() {
        return apiKey;
    }
}
