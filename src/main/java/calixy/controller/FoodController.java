package calixy.controller;

import calixy.model.dto.request.CreateFoodRequest;
import calixy.model.dto.request.UpdateFoodRequest;
import calixy.model.dto.response.FoodResponse;
import calixy.model.dto.response.MessageResponse;
import calixy.model.enums.FoodCategory;
import calixy.service.FoodService;
import calixy.util.LanguageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;
    private final LanguageUtil languageUtil;

    @GetMapping
    public ResponseEntity<List<FoodResponse>> getFoods(
            @RequestParam(required = false) FoodCategory category,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(foodService.getFoods(category, query, languageUtil.getLang()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFoodById(@PathVariable Long id) {
        return ResponseEntity.ok(foodService.getFoodById(id, languageUtil.getLang()));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<FoodResponse> createFood(
            @Valid @RequestBody CreateFoodRequest request) {
        return ResponseEntity.ok(foodService.createFood(request));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<FoodResponse> updateFood(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFoodRequest request) {
        return ResponseEntity.ok(foodService.updateFood(id, request));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.ok(new MessageResponse("Food deleted successfully"));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<List<FoodResponse>> getAllFoodsForAdmin() {
        return ResponseEntity.ok(foodService.getAllFoodsForAdmin());
    }
}