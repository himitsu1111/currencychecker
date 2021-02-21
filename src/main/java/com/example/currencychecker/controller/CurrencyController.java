package com.example.currencychecker.controller;


import com.example.currencychecker.service.AsyncFeign;
import com.example.currencychecker.service.GiphyFeign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class CurrencyController {

    Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    private GiphyFeign giphyFeignClient;

    private AsyncFeign openexchangeFeignClient;

    @Autowired
    public CurrencyController(GiphyFeign giphyFeignClient, AsyncFeign openexchangeFeignClient) {
        this.giphyFeignClient = giphyFeignClient;
        this.openexchangeFeignClient = openexchangeFeignClient;
    }

    @GetMapping(path = "/")
    public String getTestStr() {

        Instant today = Instant.now();
        ZoneId euroMsk = ZoneId.of("Europe/Moscow");
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(euroMsk);
        ZonedDateTime todayMsk = ZonedDateTime.ofInstant(today, euroMsk);
        ZonedDateTime yesterday = todayMsk.minus(1, ChronoUnit.DAYS);
        logger.info("request at " + todayMsk);

        CompletableFuture<BigDecimal> todayRates;
        CompletableFuture<BigDecimal> yesterdayRates;

        String gifUrl = "";
        try {
            yesterdayRates = openexchangeFeignClient.getRatesAsync(DATE_TIME_FORMATTER.format(yesterday));
            todayRates = openexchangeFeignClient.getRatesAsync(DATE_TIME_FORMATTER.format(today));

            CompletableFuture<Void> cf = CompletableFuture.allOf(yesterdayRates, todayRates);
             cf.get();

            BigDecimal decYesRate = yesterdayRates.get();
            logger.info("yesterday rates - " + decYesRate);
            BigDecimal decTodRate = todayRates.get();
            logger.info("today rates - " + decTodRate);

            int ratesUp = decTodRate.compareTo(decYesRate);
            String gifType = ratesUp >= 0 ? "rich" : "broke";
            logger.info("getting " + gifType + " gif");
            gifUrl = giphyFeignClient.getGif(gifType);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return "<img src=\"" + gifUrl + "\" alt=\"there should be gif\"  width=250/>";
    }
}
