package com.example.currencychecker.service;

import com.example.currencychecker.httpclient.GiphyFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GiphyFeign {

    private GiphyFeignClient giphyFeignClient;

    @Value("${giphykey}")
    private String key;

    @Autowired
    public GiphyFeign(GiphyFeignClient giphyFeignClient) {
        this.giphyFeignClient = giphyFeignClient;
    }

    public String getGif(String gifType) {
        JsonNode parent = null;
        try {
            parent = new ObjectMapper().readTree(giphyFeignClient.getGif(gifType, key, "g"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String gifUrl = parent.get("data")
                .get("images")
                .get("fixed_height")
                .get("url").asText();
        return gifUrl;
    }
}
