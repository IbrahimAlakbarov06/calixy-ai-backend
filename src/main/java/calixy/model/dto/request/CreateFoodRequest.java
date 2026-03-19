package calixy.model.dto.request;

import calixy.model.enums.FoodCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateFoodRequest {

    @NotBlank(message = "Food name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 100, message = "Azerbaijani name must be max 100 characters")
    private String nameAz;

    @NotNull(message = "Calories per 100g is required")
    @DecimalMin(value = "0.0", message = "Calories cannot be negative")
    @DecimalMax(value = "9000.0", message = "Calories seem too high")
    private Double caloriesPer100g;

    @NotNull(message = "Protein per 100g is required")
    @DecimalMin(value = "0.0", message = "Protein cannot be negative")
    private Double proteinPer100g;

    @NotNull(message = "Carbs per 100g is required")
    @DecimalMin(value = "0.0", message = "Carbs cannot be negative")
    private Double carbsPer100g;

    @NotNull(message = "Fats per 100g is required")
    @DecimalMin(value = "0.0", message = "Fats cannot be negative")
    private Double fatsPer100g;

    @NotNull(message = "Category is required")
    private FoodCategory category;

    private Boolean isAzerbaijani = false;

    private String imageUrl;
}
