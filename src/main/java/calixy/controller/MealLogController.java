package calixy.controller;

import calixy.domain.entity.User;
import calixy.model.dto.request.LogMealRequest;
import calixy.model.dto.response.DailySummaryResponse;
import calixy.model.dto.response.MealLogResponse;
import calixy.model.dto.response.MessageResponse;
import calixy.service.MealLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealLogController {

    private final MealLogService mealLogService;

    @PostMapping("/log")
    public ResponseEntity<MealLogResponse> logMeal(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody LogMealRequest request) {
        return ResponseEntity.ok(mealLogService.logMeal(user, request));
    }

    @GetMapping("/today")
    public ResponseEntity<DailySummaryResponse> getToday(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(mealLogService.getToday(user));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<DailySummaryResponse> getByDate(
            @AuthenticationPrincipal User user,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(mealLogService.getByDate(user, date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteMealLog(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        mealLogService.deleteMealLog(user, id);
        return ResponseEntity.ok(new MessageResponse("Meal log deleted successfully"));
    }

    @GetMapping("/history")
    public ResponseEntity<List<MealLogResponse>> getHistory(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(mealLogService.getHistory(user, from, to));
    }
}