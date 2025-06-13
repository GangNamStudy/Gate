package com.hunnit_beasts.gate.controller;

import com.hunnit_beasts.gate.service.PaidVehicleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/paid-vehicles")
@RequiredArgsConstructor
public class PaidVehicleController {
    private final PaidVehicleService paidVehicleService;

    @PostMapping
    public ResponseEntity<Void> registerPaidVehicle(@Valid @RequestBody PaidVehicleRequest request) {
        paidVehicleService.registerPaidVehicle(request.plate());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{plate}")
    public ResponseEntity<PaidVehicleResponse> checkPaidVehicle(@PathVariable String plate) {
        boolean isPaid = paidVehicleService.isPaidVehicle(plate);
        String message = isPaid ? "결제 완료된 차량입니다" : "결제되지 않은 차량입니다";

        PaidVehicleResponse response = new PaidVehicleResponse(
                plate,
                "check",
                message,
                isPaid,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{plate}")
    public ResponseEntity<Void> removePaidVehicle(@PathVariable String plate) {
        paidVehicleService.removePaidVehicle(plate);
        return ResponseEntity.ok().build();
    }

    // Request DTO
    record PaidVehicleRequest(@NotBlank String plate) {}

    // Response DTO
    record PaidVehicleResponse(
            String plate,
            String car_status,
            String message,
            boolean isPaid,
            LocalDateTime time
    ) {}
}
