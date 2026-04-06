package calixy.model.dto.response;

import calixy.model.enums.MealSource;
import calixy.model.enums.MealType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MealLogResponse {

    private Long id;
    private MealType mealType;

    private Long foodId;
    private String foodName;
    private String foodNameAz;

    private Long dietPlanId;
    private String dietPlanName;

    private Double portionGrams;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;

    private MealSource source;
    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loggedAt;
}