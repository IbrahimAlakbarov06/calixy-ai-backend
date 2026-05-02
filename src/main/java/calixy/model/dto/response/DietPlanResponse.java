package calixy.model.dto.response;

import calixy.model.enums.MealType;
import calixy.model.enums.TargetGoal;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DietPlanResponse {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;

    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fat;

    private TargetGoal targetGoal;
    private MealType mealType;
    private Boolean isActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}