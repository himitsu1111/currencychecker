package com.example.currencychecker.service;

import com.example.currencychecker.httpclient.OpenexchangeFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncFeign {

    private OpenexchangeFeignClient openexchangeFeignClient;

    @Value("${currency}")
    String currency;

    @Value("${openexchangekey}")
    String key;

    @Autowired
    public AsyncFeign(OpenexchangeFeignClient openexchangeFeignClient) {
        this.openexchangeFeignClient = openexchangeFeignClient;
    }

    @Async
    public CompletableFuture<BigDecimal> getRatesAsync (String date) {

        String retStr = "";
        BigDecimal retDec = null;
        try {
            JsonNode response
                    = new ObjectMapper().readTree(openexchangeFeignClient.getRates(key, date));
            retStr = response.get("rates")
                    .get(currency)
                    .asText();
            retDec = new BigDecimal(retStr);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(retDec);
    }
}
