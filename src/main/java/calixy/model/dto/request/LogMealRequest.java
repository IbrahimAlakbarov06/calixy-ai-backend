package calixy.model.dto.request;

import calixy.model.enums.MealSource;
import calixy.model.enums.MealType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogMealRequest {

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    private Long foodId;

    private Long dietPlanId;

    @NotNull(message = "Portion grams is required")
    @DecimalMin(value = "1.0", message = "Portion must be at least 1 gram")
    private Double portionGrams;

    @NotNull(message = "Source is required")
    private MealSource source;
}