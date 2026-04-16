package calixy.model.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsResponse {

    private LocalDate from;
    private LocalDate to;

    private Double avgDailyCalories;
    private Double totalCalories;
    private Integer calorieGoal;

    private Double avgProtein;
    private Double avgCarbs;
    private Double avgFat;
    private Integer proteinGoal;
    private Integer carbGoal;
    private Integer fatGoal;

    private Double avgWaterMl;
    private Integer waterGoalMl;

    private Double currentWeight;
    private Double startWeight;
    private Double weightChange;

    private List<DailySummaryResponse> dailyBreakdown;
}