package calixy.model.dto.request;

import calixy.model.enums.MealType;
import calixy.model.enums.TargetGoal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDietPlanRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String nameAz;
    private String nameRu;
    private String nameTr;

    private String description;
    private String descriptionAz;
    private String descriptionRu;
    private String descriptionTr;
    private String imageUrl;

    @NotNull(message = "Calories is required")
    private Integer calories;

    @NotNull(message = "Protein is required")
    private Integer protein;

    @NotNull(message = "Carbs is required")
    private Integer carbs;

    @NotNull(message = "Fat is required")
    private Integer fat;

    private TargetGoal targetGoal;
    private MealType mealType;
}