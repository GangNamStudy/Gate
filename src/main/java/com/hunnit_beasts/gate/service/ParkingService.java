package com.hunnit_beasts.gate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingService {
    private final WebClient webClient;

    @Value("${api.base-url}")
    private String baseUrl;

    @Value("${api.parking-management.entrance}")
    private String entrancePath;

    @Value("${api.parking-management.departure}")
    private String departurePath;

    public void entryCar(String plate) {
        EntryRequest request = new EntryRequest(plate, LocalDateTime.now());

        webClient.post()
                .uri(baseUrl + entrancePath)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void exitCar(String plate) {
        ExitRequest request = new ExitRequest(plate, LocalDateTime.now());

        webClient.patch()
                .uri(baseUrl + departurePath.replace("{plate}", plate))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    record EntryRequest(String plate, LocalDateTime entry_time) {}
    record ExitRequest(String plate, LocalDateTime exit_time) {}
}