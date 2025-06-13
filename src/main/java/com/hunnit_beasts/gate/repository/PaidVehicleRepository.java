package com.hunnit_beasts.gate.repository;

import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

@Repository
public class PaidVehicleRepository {
    private final Set<String> paidVehicles = ConcurrentHashMap.newKeySet();

    public void addPaidVehicle(String plate) {
        if (paidVehicles.contains(plate))
            throw new IllegalArgumentException("이미 등록된 차량입니다: " + plate);
        paidVehicles.add(plate);
    }

    public boolean isPaidVehicle(String plate) {
        return paidVehicles.contains(plate);
    }

    public void removePaidVehicle(String plate) {
        if (!paidVehicles.contains(plate))
            throw new IllegalArgumentException("등록되지 않은 차량입니다: " + plate);
        paidVehicles.remove(plate);
    }

    public Set<String> getAllPaidVehicles() {
        return Set.copyOf(paidVehicles);
    }
}