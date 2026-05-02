package calixy.model.dto.request;

import calixy.model.enums.FoodCategory;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateFoodRequest {

    @NotBlank(message = "Food name is required")
    @Size(min = 2, max = 100)
    private String name;

    private String nameAz;
    private String nameRu;
    private String nameTr; 

    @NotNull(message = "Calories per 100g is required")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "9000.0")
    private Double caloriesPer100g;

    @NotNull(message = "Protein per 100g is required")
    @DecimalMin(value = "0.0")
    private Double proteinPer100g;

    @NotNull(message = "Carbs per 100g is required")
    @DecimalMin(value = "0.0")
    private Double carbsPer100g;

    @NotNull(message = "Fats per 100g is required")
    @DecimalMin(value = "0.0")
    private Double fatsPer100g;

    @NotNull(message = "Category is required")
    private FoodCategory category;

    private Boolean isAzerbaijani = false;
    private String imageUrl;
}