package com.hunnit_beasts.gate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.gate.service.PaidVehicleService;
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

@WebMvcTest(PaidVehicleController.class)
class PaidVehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaidVehicleService paidVehicleService;

    @Test
    @DisplayName("결제 차량 등록 성공")
    void registerPaidVehicle_Success() throws Exception {
        // given
        String plate = "12가 3456";
        PaidVehicleController.PaidVehicleRequest request =
                new PaidVehicleController.PaidVehicleRequest(plate);

        doNothing().when(paidVehicleService).registerPaidVehicle(plate);

        // when & then
        mockMvc.perform(post("/api/v1/paid-vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(paidVehicleService).registerPaidVehicle(plate);
    }

    @Test
    @DisplayName("결제 차량 등록 실패 - 이미 등록된 차량")
    void registerPaidVehicle_Fail_AlreadyRegistered() throws Exception {
        // given
        String plate = "12가 3456";
        PaidVehicleController.PaidVehicleRequest request =
                new PaidVehicleController.PaidVehicleRequest(plate);

        doThrow(new IllegalArgumentException("이미 등록된 차량입니다: " + plate))
                .when(paidVehicleService).registerPaidVehicle(plate);

        // when & then
        mockMvc.perform(post("/api/v1/paid-vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("이미 등록된 차량입니다: " + plate));

        verify(paidVehicleService).registerPaidVehicle(plate);
    }

    @Test
    @DisplayName("결제 차량 등록 실패 - 차량번호 누락")
    void registerPaidVehicle_Fail_EmptyPlate() throws Exception {
        // given
        PaidVehicleController.PaidVehicleRequest request =
                new PaidVehicleController.PaidVehicleRequest("");

        // when & then
        mockMvc.perform(post("/api/v1/paid-vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(paidVehicleService, never()).registerPaidVehicle(anyString());
    }

    @Test
    @DisplayName("결제 차량 조회 성공 - 결제된 차량")
    void checkPaidVehicle_Success_PaidVehicle() throws Exception {
        // given
        String plate = "12가 3456";

        when(paidVehicleService.isPaidVehicle(plate)).thenReturn(true);

        // when & then
        mockMvc.perform(get("/api/v1/paid-vehicles/{plate}", plate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate").value(plate))
                .andExpect(jsonPath("$.car_status").value("check"))
                .andExpect(jsonPath("$.message").value("결제 완료된 차량입니다"))
                .andExpect(jsonPath("$.isPaid").value(true));

        verify(paidVehicleService).isPaidVehicle(plate);
    }

    @Test
    @DisplayName("결제 차량 조회 성공 - 결제되지 않은 차량")
    void checkPaidVehicle_Success_NotPaidVehicle() throws Exception {
        // given
        String plate = "12가 3456";

        when(paidVehicleService.isPaidVehicle(plate)).thenReturn(false);

        // when & then
        mockMvc.perform(get("/api/v1/paid-vehicles/{plate}", plate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate").value(plate))
                .andExpect(jsonPath("$.car_status").value("check"))
                .andExpect(jsonPath("$.message").value("결제되지 않은 차량입니다"))
                .andExpect(jsonPath("$.isPaid").value(false));

        verify(paidVehicleService).isPaidVehicle(plate);
    }

    @Test
    @DisplayName("결제 차량 삭제 성공")
    void removePaidVehicle_Success() throws Exception {
        // given
        String plate = "12가 3456";

        doNothing().when(paidVehicleService).removePaidVehicle(plate);

        // when & then
        mockMvc.perform(delete("/api/v1/paid-vehicles/{plate}", plate))
                .andDo(print())
                .andExpect(status().isOk());

        verify(paidVehicleService).removePaidVehicle(plate);
    }

    @Test
    @DisplayName("결제 차량 삭제 실패 - 등록되지 않은 차량")
    void removePaidVehicle_Fail_NotRegistered() throws Exception {
        // given
        String plate = "12가 3456";

        doThrow(new IllegalArgumentException("등록되지 않은 차량입니다: " + plate))
                .when(paidVehicleService).removePaidVehicle(plate);

        // when & then
        mockMvc.perform(delete("/api/v1/paid-vehicles/{plate}", plate))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("등록되지 않은 차량입니다: " + plate));

        verify(paidVehicleService).removePaidVehicle(plate);
    }
}
