package calixy.model.dto.response;

import calixy.model.enums.FoodCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodResponse {
    private Long id;
    private String name;

    private Double caloriesPer100g;
    private Double proteinPer100g;
    private Double carbsPer100g;
    private Double fatsPer100g;

    private FoodCategory category;
    private Boolean isAzerbaijani;
    private String imageUrl;
    private Boolean isActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
