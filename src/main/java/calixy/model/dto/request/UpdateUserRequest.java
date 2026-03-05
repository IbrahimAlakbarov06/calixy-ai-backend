package calixy.model.dto.request;

import calixy.model.enums.ActivityLevel;
import calixy.model.enums.Gender;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    private String phoneNumber;

    private String profileImage;

    private Gender gender;

    private LocalDate dateOfBirth;

    private Double height;

    private Double weight;

    private ActivityLevel activityLevel;
}

