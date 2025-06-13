package com.hunnit_beasts.gate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.gate.repository.PaidVehicleRepository;
import com.hunnit_beasts.gate.service.ParkingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParkingController.class)
class ParkingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ParkingService parkingService;

    @MockitoBean
    private PaidVehicleRepository paidVehicleRepository;

    @Test
    @DisplayName("입차 성공 - 결제된 차량")
    void entryCar_Success() throws Exception {
        // given
        String plate = "12가 3456";
        ParkingController.EntryRequest request = new ParkingController.EntryRequest(plate);

        when(paidVehicleRepository.isPaidVehicle(plate)).thenReturn(true);
        doNothing().when(parkingService).entryCar(plate);

        // when & then
        mockMvc.perform(post("/api/v1/parking/entrance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate").value(plate))
                .andExpect(jsonPath("$.car_status").value("entry"))
                .andExpect(jsonPath("$.message").value("입차 완료"))
                .andExpect(jsonPath("$.isPaid").value(true));

        verify(paidVehicleRepository).isPaidVehicle(plate);
        verify(parkingService).entryCar(plate);
    }

    @Test
    @DisplayName("입차 실패 - 결제되지 않은 차량")
    void entryCar_Fail_NotPaidVehicle() throws Exception {
        // given
        String plate = "12가 3456";
        ParkingController.EntryRequest request = new ParkingController.EntryRequest(plate);

        when(paidVehicleRepository.isPaidVehicle(plate)).thenReturn(false);

        // when & then
        mockMvc.perform(post("/api/v1/parking/entrance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("결제되지 않은 차량입니다: " + plate));

        verify(paidVehicleRepository).isPaidVehicle(plate);
        verify(parkingService, never()).entryCar(anyString());
    }

    @Test
    @DisplayName("입차 실패 - 차량번호 누락")
    void entryCar_Fail_EmptyPlate() throws Exception {
        // given
        ParkingController.EntryRequest request = new ParkingController.EntryRequest("");

        // when & then
        mockMvc.perform(post("/api/v1/parking/entrance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(paidVehicleRepository, never()).isPaidVehicle(anyString());
        verify(parkingService, never()).entryCar(anyString());
    }

    @Test
    @DisplayName("출차 성공")
    void exitCar_Success() throws Exception {
        // given
        String plate = "12가 3456";

        doNothing().when(parkingService).exitCar(plate);
        doNothing().when(paidVehicleRepository).removePaidVehicle(plate);

        // when & then
        mockMvc.perform(patch("/api/v1/parking/{plate}/departure", plate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate").value(plate))
                .andExpect(jsonPath("$.car_status").value("exit"))
                .andExpect(jsonPath("$.message").value("출차 완료"))
                .andExpect(jsonPath("$.isPaid").value(false));

        verify(parkingService).exitCar(plate);
        verify(paidVehicleRepository).removePaidVehicle(plate);
    }

    @Test
    @DisplayName("출차 실패 - 외부 API 호출 실패")
    void exitCar_Fail_ExternalApiError() throws Exception {
        // given
        String plate = "12가 3456";

        doThrow(new RuntimeException("External API Error"))
                .when(parkingService).exitCar(plate);

        // when & then
        mockMvc.perform(patch("/api/v1/parking/{plate}/departure", plate))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("External API Error"));

        verify(parkingService).exitCar(plate);
        verify(paidVehicleRepository, never()).removePaidVehicle(anyString());
    }
}
