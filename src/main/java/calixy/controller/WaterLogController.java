package calixy.controller;

import calixy.domain.entity.User;
import calixy.model.dto.request.WaterLogRequest;
import calixy.model.dto.response.WaterDailySummaryResponse;
import calixy.model.dto.response.WaterLogResponse;
import calixy.model.dto.response.MessageResponse;
import calixy.service.WaterLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/water")
@RequiredArgsConstructor
public class WaterLogController {

    private final WaterLogService waterLogService;

    @PostMapping("/log")
    public ResponseEntity<WaterLogResponse> logWater(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody WaterLogRequest request) {
        return ResponseEntity.ok(waterLogService.logWater(user, request));
    }

    @GetMapping("/today")
    public ResponseEntity<WaterDailySummaryResponse> getToday(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(waterLogService.getToday(user));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<WaterDailySummaryResponse> getByDate(
            @AuthenticationPrincipal User user,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(waterLogService.getByDate(user, date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteLog(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        waterLogService.deleteLog(user, id);
        return ResponseEntity.ok(new MessageResponse("Water log deleted successfully"));
    }

    @GetMapping("/history")
    public ResponseEntity<List<WaterLogResponse>> getHistory(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(waterLogService.getHistory(user, from, to));
    }
}