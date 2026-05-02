package calixy.mapper;

import calixy.domain.entity.*;
import calixy.model.dto.request.LogMealRequest;
import calixy.model.dto.response.DailySummaryResponse;
import calixy.model.dto.response.MealLogResponse;
import calixy.util.LanguageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MealLogMapper {

    private final LanguageUtil languageUtil;

    public MealLog toEntity(User user, Food food, DietPlan dietPlan,
                            LogMealRequest request,
                            double calories, double protein, double carbs, double fat) {
        return MealLog.builder()
                .user(user)
                .food(food)
                .dietPlan(dietPlan)
                .mealType(request.getMealType())
                .portionGrams(request.getPortionGrams())
                .calories(calories)
                .protein(protein)
                .carbs(carbs)
                .fat(fat)
                .source(request.getSource())
                .date(LocalDate.now())
                .build();
    }

    public MealLogResponse toResponse(MealLog log) {
        if (log == null) return null;

        String foodName = null;
        if (log.getFood() != null) {
            foodName = languageUtil.resolve(
                    log.getFood().getName(),
                    log.getFood().getNameAz(),
                    log.getFood().getNameRu(),
                    log.getFood().getNameTr()
            );
        }

        String dietPlanName = null;
        if (log.getDietPlan() != null) {
            dietPlanName = languageUtil.resolve(
                    log.getDietPlan().getName(),
                    log.getDietPlan().getNameAz(),
                    log.getDietPlan().getNameRu(),
                    log.getDietPlan().getNameTr()
            );
        }

        return MealLogResponse.builder()
                .id(log.getId())
                .mealType(log.getMealType())
                .foodId(log.getFood() != null ? log.getFood().getId() : null)
                .foodName(foodName)
                .dietPlanId(log.getDietPlan() != null ? log.getDietPlan().getId() : null)
                .dietPlanName(dietPlanName)
                .portionGrams(log.getPortionGrams())
                .calories(log.getCalories())
                .protein(log.getProtein())
                .carbs(log.getCarbs())
                .fat(log.getFat())
                .source(log.getSource())
                .date(log.getDate())
                .loggedAt(log.getLoggedAt())
                .build();
    }

    public List<MealLogResponse> toResponseList(List<MealLog> logs) {
        return logs.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public DailySummaryResponse toDailySummaryResponse(DailySummary summary,
                                                       List<MealLog> logs,
                                                       LocalDate date) {
        Map<String, List<MealLogResponse>> mealsByType = logs.stream()
                .map(this::toResponse)
                .collect(Collectors.groupingBy(r -> r.getMealType().name()));

        return DailySummaryResponse.builder()
                .date(date)
                .totalCalories(summary != null ? summary.getTotalCalories() : 0.0)
                .totalProtein(summary != null ? summary.getTotalProtein() : 0.0)
                .totalCarbs(summary != null ? summary.getTotalCarbs() : 0.0)
                .totalFat(summary != null ? summary.getTotalFat() : 0.0)
                .calorieGoal(summary != null ? summary.getCalorieGoal() : null)
                .proteinGoal(summary != null ? summary.getProteinGoal() : null)
                .carbGoal(summary != null ? summary.getCarbGoal() : null)
                .fatGoal(summary != null ? summary.getFatGoal() : null)
                .remainingCalories(summary != null ? summary.getRemainingCalories() : null)
                .mealsByType(mealsByType)
                .build();
    }
}