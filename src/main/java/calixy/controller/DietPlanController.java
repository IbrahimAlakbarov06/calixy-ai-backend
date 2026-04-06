package calixy.controller;

import calixy.domain.entity.User;
import calixy.model.dto.request.CreateDietPlanRequest;
import calixy.model.dto.response.DietPlanResponse;
import calixy.model.dto.response.MessageResponse;
import calixy.model.dto.response.UserDietPlanResponse;
import calixy.model.enums.TargetGoal;
import calixy.service.DietPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diet-plans")
@RequiredArgsConstructor
public class DietPlanController {

    private final DietPlanService dietPlanService;

    @GetMapping
    public ResponseEntity<List<DietPlanResponse>> getPlans(
            @RequestParam(required = false) TargetGoal targetGoal) {
        return ResponseEntity.ok(dietPlanService.getPlans(targetGoal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DietPlanResponse> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(dietPlanService.getPlanById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<UserDietPlanResponse>> getMyPlans(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dietPlanService.getMyPlans(user));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<UserDietPlanResponse> addToMyPlans(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(dietPlanService.addToMyPlans(user, id));
    }

    @DeleteMapping("/my/{userDietPlanId}")
    public ResponseEntity<MessageResponse> removeFromMyPlans(
            @AuthenticationPrincipal User user,
            @PathVariable Long userDietPlanId) {
        dietPlanService.removeFromMyPlans(user, userDietPlanId);
        return ResponseEntity.ok(new MessageResponse("Plan removed successfully"));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<DietPlanResponse> createPlan(
            @Valid @RequestBody CreateDietPlanRequest request) {
        return ResponseEntity.ok(dietPlanService.createPlan(request));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<DietPlanResponse> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody CreateDietPlanRequest request) {
        return ResponseEntity.ok(dietPlanService.updatePlan(id, request));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> deletePlan(@PathVariable Long id) {
        dietPlanService.deletePlan(id);
        return ResponseEntity.ok(new MessageResponse("Diet plan deleted successfully"));
    }
}