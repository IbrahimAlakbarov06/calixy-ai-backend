package calixy.controller;

import calixy.domain.entity.User;
import calixy.model.dto.response.AnalyticsResponse;
import calixy.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/daily")
    public ResponseEntity<AnalyticsResponse> getDaily(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(analyticsService.getDaily(user, LocalDate.now()));
    }

    @GetMapping("/daily/{date}")
    public ResponseEntity<AnalyticsResponse> getDailyByDate(
            @AuthenticationPrincipal User user,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(analyticsService.getDaily(user, date));
    }

    @GetMapping("/weekly")
    public ResponseEntity<AnalyticsResponse> getWeekly(
            @AuthenticationPrincipal User user) {
        LocalDate monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        return ResponseEntity.ok(analyticsService.getWeekly(user, monday));
    }

    @GetMapping("/weekly/{from}")
    public ResponseEntity<AnalyticsResponse> getWeeklyFrom(
            @AuthenticationPrincipal User user,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from) {
        return ResponseEntity.ok(analyticsService.getWeekly(user, from));
    }

    @GetMapping("/monthly")
    public ResponseEntity<AnalyticsResponse> getMonthly(
            @AuthenticationPrincipal User user) {
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
        return ResponseEntity.ok(analyticsService.getMonthly(user, firstDay));
    }

    @GetMapping("/monthly/{from}")
    public ResponseEntity<AnalyticsResponse> getMonthlyFrom(
            @AuthenticationPrincipal User user,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from) {
        return ResponseEntity.ok(analyticsService.getMonthly(user, from));
    }

    @GetMapping("/range")
    public ResponseEntity<AnalyticsResponse> getRange(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(analyticsService.getRange(user, from, to));
    }
}