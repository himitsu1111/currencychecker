package com.example.currencychecker;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void imgShouldBeReturned() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/RUB", String.class)).contains("<img src=\"https");
    }

    @Test
    public void mainShouldBeReturned() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/", String.class)).contains("Please set one of a");
    }

}
