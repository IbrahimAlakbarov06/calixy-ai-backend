package calixy.model.dto.request;

import calixy.model.enums.ActivityLevel;
import calixy.model.enums.AllergyType;
import calixy.model.enums.DietaryRule;
import calixy.model.enums.Gender;
import calixy.model.enums.Goal;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetupProfileRequest {

    private String profileImage;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Age is required")
    @Min(value = 10, message = "Age must be at least 10")
    @Max(value = 120, message = "Age must be at most 120")
    private Integer age;

    @NotNull(message = "Height is required")
    private Double height;

    @NotNull(message = "Weight is required")
    private Double weight;

    @NotNull(message = "Activity level is required")
    private ActivityLevel activityLevel;

    @NotNull(message = "Goal is required")
    private Goal goal;

    @DecimalMin(value = "20.0", message = "Target weight must be at least 20kg")
    @DecimalMax(value = "500.0", message = "Target weight must be at most 500kg")
    private Double targetWeight;

    private List<AllergyType> allergies;
    private List<String> customAllergies;
    private List<DietaryRule> dietaryRules;

    private String language;
}