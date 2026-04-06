package calixy.model.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailySummaryResponse {

    private LocalDate date;

    private Double totalCalories;
    private Double totalProtein;
    private Double totalCarbs;
    private Double totalFat;

    private Integer calorieGoal;
    private Integer proteinGoal;
    private Integer carbGoal;
    private Integer fatGoal;

    private Double remainingCalories;

    private Map<String, List<MealLogResponse>> mealsByType;
}