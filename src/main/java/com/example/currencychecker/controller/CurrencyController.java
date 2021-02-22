package com.example.currencychecker.controller;


import com.example.currencychecker.exceptions.InvalidHTTPParamException;
import com.example.currencychecker.service.AsyncFeign;
import com.example.currencychecker.service.GiphyFeign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String getHomeAddr() {
        return "Please set one of a currencies for example http://localhost:8080/RUB. Base currency " +
                "is always USD. Changing the API `base` currency is available " +
                "for Developer, Enterprise and Unlimited plan clients";
    }

    @GetMapping(path = "/{currencyOption}")
    public String getGif(@PathVariable String currencyOption) {

        Instant today = Instant.now();
        ZoneId euroMsk = ZoneId.of("Europe/Moscow");
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(euroMsk);
        ZonedDateTime todayMsk = ZonedDateTime.ofInstant(today, euroMsk);
        ZonedDateTime yesterday = todayMsk.minus(1, ChronoUnit.DAYS);
        logger.info("request at " + todayMsk);
        logger.info("requested currency is " + currencyOption);

        CompletableFuture<BigDecimal> todayRates;
        CompletableFuture<BigDecimal> yesterdayRates;

        String gifUrl = "";
        try {
            yesterdayRates = openexchangeFeignClient.getRatesAsync(DATE_TIME_FORMATTER.format(yesterday), currencyOption);
            todayRates = openexchangeFeignClient.getRatesAsync(DATE_TIME_FORMATTER.format(today), currencyOption);

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

        } catch (InterruptedException | ExecutionException | InvalidHTTPParamException e) {
            logger.error(e.getMessage());
        }

        return "<img src=\"" + gifUrl + "\" alt=\"there should be gif, please set currency correctly\"  width=250/>";
    }
}
