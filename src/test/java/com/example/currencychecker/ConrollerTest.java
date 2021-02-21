package com.example.currencychecker;


import com.example.currencychecker.controller.CurrencyController;
import com.example.currencychecker.service.AsyncFeign;
import com.example.currencychecker.service.GiphyFeign;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConrollerTest {

    @MockBean
    GiphyFeign giphyFeignClient;

    @MockBean
    AsyncFeign openexchFeignClient;

    @Autowired
    CurrencyController currencyController;

    @Test
    public void isGifReceivedWhenCurrencySame() {

        final String gifType = "rich";
        final String currencyRateToday = "74.035";
        final String currencyRateYesterday = "74.035";
        final String gifUrl = "https://media1.giphy.com/media/1URYREdZievwIFZugg/200.gif?cid=" +
                "af156d6850d09e106eab5d69dd98cdc904ee97a5257526a7&rid=200.gif";

        Instant today = Instant.now();
        Instant yesterday = today.minus(1, ChronoUnit.DAYS);
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault());

        Mockito.when(giphyFeignClient.getGif(gifType))
                                    .thenReturn(gifUrl);

        Mockito.when(openexchFeignClient.getRatesAsync(DATE_TIME_FORMATTER.format(today)))
                .thenReturn(CompletableFuture.completedFuture(new BigDecimal(currencyRateToday)));

        Mockito.when(openexchFeignClient.getRatesAsync(DATE_TIME_FORMATTER.format(yesterday)))
                .thenReturn(CompletableFuture.completedFuture(new BigDecimal(currencyRateYesterday)));

        assertThat(currencyController.getTestStr()).contains(gifUrl);
    }
}
