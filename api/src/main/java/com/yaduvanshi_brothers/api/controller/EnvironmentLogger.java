package com.yaduvanshi_brothers.api.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EnvironmentLogger implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.getenv().forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });
    }
}
