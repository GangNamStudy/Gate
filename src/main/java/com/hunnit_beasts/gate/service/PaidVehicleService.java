package com.hunnit_beasts.gate.service;

import com.hunnit_beasts.gate.repository.PaidVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaidVehicleService {
    private final PaidVehicleRepository paidVehicleRepository;

    public void registerPaidVehicle(String plate) {
        paidVehicleRepository.addPaidVehicle(plate);
    }

    public boolean isPaidVehicle(String plate) {
        return paidVehicleRepository.isPaidVehicle(plate);
    }

    public void removePaidVehicle(String plate) {
        paidVehicleRepository.removePaidVehicle(plate);
    }
}