package com.cryptomind.userservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/health")
    public String health() {
        return "User Service OK!";
    }

    // This endpoint is for testing the health of the service
    @GetMapping("/hello")
    public String hello() {
        return "Hello from User Service!";
    }
}
