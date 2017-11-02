package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class DemoClient {

    private final RestTemplate restTemplate;

    @Value("${demo.api.hostname}")
    private String hostname;

    @Value("${demo.api.endpoint}")
    private String endpoint;

    public DemoClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(1000)
                .setReadTimeout(1000)
                .build();
    }

    public DemoApiResponse get() {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(hostname + endpoint).build();
        return restTemplate.getForObject(uriComponents.toUri(), DemoApiResponse.class);
    }
}
