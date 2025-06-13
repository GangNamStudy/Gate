package com.hunnit_beasts.gate.controller;

import com.hunnit_beasts.gate.repository.PaidVehicleRepository;
import com.hunnit_beasts.gate.service.ParkingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/parking")
@RequiredArgsConstructor
public class ParkingController {
    private final ParkingService parkingService;
    private final PaidVehicleRepository paidVehicleRepository;

    @PostMapping("/entrance")
    public ResponseEntity<GateResponse> entryCar(@Valid @RequestBody EntryRequest request) {
        // 1. 결제 확인
        if (!paidVehicleRepository.isPaidVehicle(request.plate())) {
            throw new IllegalArgumentException("결제되지 않은 차량입니다: " + request.plate());
        }

        // 2. 입차 처리
        parkingService.entryCar(request.plate());

        GateResponse response = new GateResponse(
                request.plate(),
                "entry",
                "입차 완료",
                true,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{plate}/departure")
    public ResponseEntity<GateResponse> exitCar(@PathVariable String plate) {
        // 1. 출차 처리
        parkingService.exitCar(plate);

        // 2. 결제 목록에서 제거
        paidVehicleRepository.removePaidVehicle(plate);

        GateResponse response = new GateResponse(
                plate,
                "exit",
                "출차 완료",
                false,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    // Request DTO
    record EntryRequest(@NotBlank String plate) {}

    // Response DTO
    record GateResponse(
            String plate,
            String car_status,
            String message,
            boolean isPaid,
            LocalDateTime time
    ) {}
}