package com.example.currencychecker.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "fixer", url="${openexchangeUrl}")
public interface OpenexchangeFeignClient {

    @GetMapping(path = "{date}.json")
    String getRates(@RequestParam String app_id
                    , @PathVariable String date
                    );
}
