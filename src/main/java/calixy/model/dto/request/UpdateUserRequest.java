package calixy.model.dto.request;

import calixy.model.enums.ActivityLevel;
import calixy.model.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    private String profileImage;

    private Gender gender;

    @Min(value = 10, message = "Age must be at least 10")
    @Max(value = 120, message = "Age must be at most 120")
    private Integer age;

    private Double height;
    private Double weight;
    private ActivityLevel activityLevel;

    private String language;
}