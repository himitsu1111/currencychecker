package com.example.currencychecker.controller;


import com.example.currencychecker.httpclient.GiphyFeignClient;
import com.example.currencychecker.service.AsyncFeign;
import com.example.currencychecker.service.GiphyFeign;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class CurrencyController {

    private GiphyFeign giphyFeignClient;

    private AsyncFeign openexchangeFeignClient;

    @Autowired
    public CurrencyController(GiphyFeign giphyFeignClient, AsyncFeign openexchangeFeignClient) {
        this.giphyFeignClient = giphyFeignClient;
        this.openexchangeFeignClient = openexchangeFeignClient;
    }

    @GetMapping(path = "/")
    public String getTestStr() {

        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault());

        Instant today = Instant.now();
        Instant yesterday = today.minus(1, ChronoUnit.DAYS);
        System.out.println(today);
        System.out.println(yesterday);

        CompletableFuture<BigDecimal> todayRates;
        CompletableFuture<BigDecimal> yesterdayRates;

        String gifUrl = "";
        try {
            yesterdayRates = openexchangeFeignClient.getRatesAsync(DATE_TIME_FORMATTER.format(yesterday));
            todayRates = openexchangeFeignClient.getRatesAsync(DATE_TIME_FORMATTER.format(today));

            CompletableFuture<Void> cf = CompletableFuture.allOf(yesterdayRates, todayRates);
             cf.get();

            BigDecimal decYesRate = yesterdayRates.get();
            BigDecimal decTodRate = todayRates.get();

            int ratesUp = decTodRate.compareTo(decYesRate);
            String gifType = ratesUp >= 0 ? "rich" : "broke";
            gifUrl = giphyFeignClient.getGif(gifType);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return "<img src=\"" + gifUrl + "\" alt=\"there should be gif\"  width=250/>";
    }
}
