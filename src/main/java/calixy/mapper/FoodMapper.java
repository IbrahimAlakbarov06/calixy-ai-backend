package calixy.mapper;

import calixy.domain.entity.Food;
import calixy.model.dto.request.CreateFoodRequest;
import calixy.model.dto.request.UpdateFoodRequest;
import calixy.model.dto.response.FoodResponse;
import calixy.util.LanguageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FoodMapper {

    private final LanguageUtil languageUtil;

    public Food toEntity(CreateFoodRequest request) {
        if (request == null) return null;
        return Food.builder()
                .name(request.getName())
                .nameAz(request.getNameAz())
                .nameRu(request.getNameRu())
                .nameTr(request.getNameTr())
                .caloriesPer100g(request.getCaloriesPer100g())
                .proteinPer100g(request.getProteinPer100g())
                .carbsPer100g(request.getCarbsPer100g())
                .fatsPer100g(request.getFatsPer100g())
                .category(request.getCategory())
                .isAzerbaijani(request.getIsAzerbaijani())
                .imageUrl(request.getImageUrl())
                .isActive(true)
                .build();
    }

    public FoodResponse toResponse(Food food) {
        if (food == null) return null;
        return FoodResponse.builder()
                .id(food.getId())
                .name(languageUtil.resolve(
                        food.getName(),
                        food.getNameAz(),
                        food.getNameRu(),
                        food.getNameTr()))
                .caloriesPer100g(food.getCaloriesPer100g())
                .proteinPer100g(food.getProteinPer100g())
                .carbsPer100g(food.getCarbsPer100g())
                .fatsPer100g(food.getFatsPer100g())
                .category(food.getCategory())
                .isAzerbaijani(food.getIsAzerbaijani())
                .imageUrl(food.getImageUrl())
                .isActive(food.getIsActive())
                .createdAt(food.getCreatedAt())
                .build();
    }

    public void applyUpdate(Food food, UpdateFoodRequest request) {
        if (request.getName() != null)         food.setName(request.getName());
        if (request.getNameAz() != null)       food.setNameAz(request.getNameAz());
        if (request.getNameRu() != null)       food.setNameRu(request.getNameRu());
        if (request.getNameTr() != null)       food.setNameTr(request.getNameTr());
        if (request.getCaloriesPer100g() != null) food.setCaloriesPer100g(request.getCaloriesPer100g());
        if (request.getProteinPer100g() != null)  food.setProteinPer100g(request.getProteinPer100g());
        if (request.getCarbsPer100g() != null)    food.setCarbsPer100g(request.getCarbsPer100g());
        if (request.getFatsPer100g() != null)     food.setFatsPer100g(request.getFatsPer100g());
        if (request.getCategory() != null)     food.setCategory(request.getCategory());
        if (request.getIsAzerbaijani() != null) food.setIsAzerbaijani(request.getIsAzerbaijani());
        if (request.getImageUrl() != null)     food.setImageUrl(request.getImageUrl());
    }

    public List<FoodResponse> toListResponse(List<Food> foods) {
        return foods.stream().map(this::toResponse).collect(Collectors.toList());
    }
}