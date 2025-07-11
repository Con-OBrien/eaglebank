package com.eaglebank.bankapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.LinkedHashMap;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, Object> discover() {
        Map<String, Object> links = new LinkedHashMap<>();

        links.put("users", "/api/users");
        links.put("accounts", "/api/accounts");
        links.put("transactions", "/api/transactions");
        links.put("health", "/actuator/health");

        return Map.of(
                "links", links,
                "message", "Welcome to Eagle Bank API"
        );
    }
}
