package com.example.currencychecker.httpclient;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "giphy", url="${giphyUrl}")
public interface GiphyFeignClient {

    @GetMapping
    String getGif(@RequestParam String tag
                 , @RequestParam String api_key
                 , @RequestParam String rating
                    );

}
