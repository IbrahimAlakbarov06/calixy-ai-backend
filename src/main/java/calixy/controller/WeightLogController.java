package calixy.controller;

import calixy.domain.entity.User;
import calixy.model.dto.request.WeightLogRequest;
import calixy.model.dto.response.MessageResponse;
import calixy.model.dto.response.WeightLogResponse;
import calixy.model.dto.response.WeightTrendResponse;
import calixy.service.WeightLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/weight-logs")
@RequiredArgsConstructor
public class WeightLogController {

    private final WeightLogService weightLogService;

    @PostMapping
    public ResponseEntity<WeightLogResponse> logWeight(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody WeightLogRequest request) {
        return ResponseEntity.ok(weightLogService.logWeight(user, request));
    }

    @GetMapping("/trend")
    public ResponseEntity<WeightTrendResponse> getAllTrend(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(weightLogService.getAllTrend(user));
    }

    @GetMapping("/trend/range")
    public ResponseEntity<WeightTrendResponse> getTrendByRange(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(weightLogService.getTrend(user, from, to));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteLog(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        weightLogService.deleteLog(user, id);
        return ResponseEntity.ok(new MessageResponse("Weight log deleted successfully"));
    }
}