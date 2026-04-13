package com.epam.gym.workload.client;

import com.epam.gym.workload.configuration.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "${application.clients.auth}",
    url = "${application.auth.url}",
    path = "/internal/v1",
    configuration = FeignConfiguration.class
)
public interface IAuthClient {

    @GetMapping("/token")
    ValidateResponse validate(@RequestHeader("Authorization") String authHeader);
}
