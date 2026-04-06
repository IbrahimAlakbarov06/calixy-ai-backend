package calixy.mapper;

import calixy.domain.entity.DietPlan;
import calixy.domain.entity.UserDietPlan;
import calixy.model.dto.request.CreateDietPlanRequest;
import calixy.model.dto.response.DietPlanResponse;
import calixy.model.dto.response.UserDietPlanResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DietPlanMapper {

    public DietPlan toEntity(CreateDietPlanRequest request) {
        if (request == null) return null;
        return DietPlan.builder()
                .name(request.getName())
                .nameAz(request.getNameAz())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .calories(request.getCalories())
                .protein(request.getProtein())
                .carbs(request.getCarbs())
                .fat(request.getFat())
                .targetGoal(request.getTargetGoal())
                .mealType(request.getMealType())
                .isActive(true)
                .build();
    }

    public void applyUpdate(DietPlan plan, CreateDietPlanRequest request) {
        if (request.getName() != null)        plan.setName(request.getName());
        if (request.getNameAz() != null)      plan.setNameAz(request.getNameAz());
        if (request.getDescription() != null) plan.setDescription(request.getDescription());
        if (request.getImageUrl() != null)    plan.setImageUrl(request.getImageUrl());
        if (request.getCalories() != null)    plan.setCalories(request.getCalories());
        if (request.getProtein() != null)     plan.setProtein(request.getProtein());
        if (request.getCarbs() != null)       plan.setCarbs(request.getCarbs());
        if (request.getFat() != null)         plan.setFat(request.getFat());
        if (request.getTargetGoal() != null)  plan.setTargetGoal(request.getTargetGoal());
        if (request.getMealType() != null)    plan.setMealType(request.getMealType());
    }

    public DietPlanResponse toResponse(DietPlan plan) {
        if (plan == null) return null;
        return DietPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .nameAz(plan.getNameAz())
                .description(plan.getDescription())
                .imageUrl(plan.getImageUrl())
                .calories(plan.getCalories())
                .protein(plan.getProtein())
                .carbs(plan.getCarbs())
                .fat(plan.getFat())
                .targetGoal(plan.getTargetGoal())
                .mealType(plan.getMealType())
                .isActive(plan.getIsActive())
                .createdAt(plan.getCreatedAt())
                .build();
    }

    public UserDietPlanResponse toUserDietPlanResponse(UserDietPlan udp) {
        if (udp == null) return null;
        return UserDietPlanResponse.builder()
                .id(udp.getId())
                .dietPlan(udp.getDietPlan() != null ? toResponse(udp.getDietPlan()) : null)
                .customName(udp.getCustomName())
                .isActive(udp.getIsActive())
                .addedAt(udp.getAddedAt())
                .build();
    }

    public List<DietPlanResponse> toResponseList(List<DietPlan> plans) {
        return plans.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<UserDietPlanResponse> toUserDietPlanResponseList(List<UserDietPlan> list) {
        return list.stream().map(this::toUserDietPlanResponse).collect(Collectors.toList());
    }
}