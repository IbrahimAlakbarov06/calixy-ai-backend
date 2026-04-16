package calixy.model.dto.response;

import calixy.model.enums.ActivityLevel;
import calixy.model.enums.AllergyType;
import calixy.model.enums.DietaryRule;
import calixy.model.enums.Gender;
import calixy.model.enums.Goal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String profileImage;

    private Gender gender;
    private LocalDate dateOfBirth;
    private Double height;
    private Double weight;
    private ActivityLevel activityLevel;
    private Integer dailyCalorieGoal;
    private Integer dailyProteinGoal;
    private Integer dailyCarbGoal;
    private Integer dailyFatGoal;
    private Integer dailyWaterGoalMl;

    private List<Goal> goals;
    private List<AllergyType> allergies;
    private List<String> customAllergies;
    private List<DietaryRule> dietaryRules;

    private LocalDateTime createdAt;
}