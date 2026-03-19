package calixy.model.dto.request;

import calixy.model.enums.FoodCategory;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateFoodRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 100)
    private String nameAz;

    @DecimalMin(value = "0.0", message = "Calories cannot be negative")
    @DecimalMax(value = "9000.0", message = "Calories seem too high")
    private Double caloriesPer100g;

    @DecimalMin(value = "0.0")
    private Double proteinPer100g;

    @DecimalMin(value = "0.0")
    private Double carbsPer100g;

    @DecimalMin(value = "0.0")
    private Double fatsPer100g;

    private FoodCategory category;

    private Boolean isAzerbaijani;

    private String imageUrl;

}