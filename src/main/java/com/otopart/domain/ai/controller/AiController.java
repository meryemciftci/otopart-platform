package com.otopart.domain.ai.controller;

import com.otopart.domain.ai.service.AiAssistantService;
import com.otopart.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "AI Assistant", description = "AI destekli sase analizi ve canli destek")
public class AiController {

    private final AiAssistantService aiAssistantService;

    @PostMapping("/chassis-analyze")
    @Operation(summary = "Sase analizi + uyumlu parca onerisi (uye olmadan)")
    public ResponseEntity<ApiResponse<AiAssistantService.AiResponse>> analyzeAndSuggest(
            @RequestBody ChassisRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                aiAssistantService.analyzeChassisAndSuggest(
                        request.getChassisNumber(),
                        request.getUserQuery())));
    }

    @PostMapping("/support")
    @Operation(summary = "Canli destek - otomotiv sorusu")
    public ResponseEntity<ApiResponse<String>> support(
            @RequestBody SupportRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                aiAssistantService.askSupport(
                        request.getQuestion(),
                        request.getVehicleInfo())));
    }

    @Data
    public static class ChassisRequest {
        private String chassisNumber;
        private String userQuery;
    }

    @Data
    public static class SupportRequest {
        private String question;
        private String vehicleInfo;
    }
}