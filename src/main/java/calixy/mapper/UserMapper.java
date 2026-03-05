package calixy.mapper;

import calixy.domain.entity.User;
import calixy.domain.entity.UserGoal;
import calixy.domain.entity.UserProfile;
import calixy.model.dto.request.RegisterRequest;
import calixy.model.dto.request.UpdateUserRequest;
import calixy.model.dto.response.UserProfileResponse;
import calixy.model.dto.response.UserResponse;
import calixy.model.enums.AuthProvider;
import calixy.model.enums.Goal;
import calixy.model.enums.UserRole;
import calixy.model.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        UserProfile profile = user.getProfile();
        return UserResponse.builder()
                .id(user.getId())
                .firstName(profile != null ? profile.getFirstName() : null)
                .lastName(profile != null ? profile.getLastName() : null)
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .status(user.getStatus())
                .role(user.getRole())
                .authProvider(user.getAuthProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }


    public UserProfileResponse toProfileResponse(User user) {
        UserProfile profile = user.getProfile();

        List<Goal> goals = user.getGoals() == null ? List.of() :
                user.getGoals().stream()
                        .map(UserGoal::getGoal)
                        .collect(Collectors.toList());

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(profile != null ? profile.getFullName() : null)
                .email(user.getEmail())
                .gender(profile != null ? profile.getGender() : null)
                .dateOfBirth(profile != null ? profile.getDateOfBirth() : null)
                .height(profile != null ? profile.getHeight() : null)
                .weight(profile != null ? profile.getWeight() : null)
                .activityLevel(profile != null ? profile.getActivityLevel() : null)
                .dailyCalorieGoal(profile != null ? profile.getDailyCalorieGoal() : null)
                .dailyProteinGoal(profile != null ? profile.getDailyProteinGoal() : null)
                .dailyCarbGoal(profile != null ? profile.getDailyCarbGoal() : null)
                .dailyFatGoal(profile != null ? profile.getDailyFatGoal() : null)
                .goals(goals)
                .createdAt(user.getCreatedAt())
                .build();
    }


    public User toEntity(RegisterRequest request) {
        return User.builder()
                .email(request.getEmail())
                .authProvider(AuthProvider.LOCAL)
                .role(UserRole.USER_ROLE)
                .status(UserStatus.UNVERIFIED)
                .active(true)
                .build();
    }


    public User toGoogleUser(String email, String firstName, String lastName,
                             String profileImage, String encodedPassword) {
        User user = User.builder()
                .email(email)
                .profileImage(profileImage)
                .password(encodedPassword)
                .authProvider(AuthProvider.GOOGLE)
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER_ROLE)
                .active(true)
                .build();

        UserProfile profile = UserProfile.builder()
                .user(user)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        user.setProfile(profile);
        return user;
    }


    public void updateFromRequest(User user, UserProfile profile, UpdateUserRequest request) {
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }

        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getHeight() != null) {
            profile.setHeight(request.getHeight());
        }
        if (request.getWeight() != null) {
            profile.setWeight(request.getWeight());
        }
        if (request.getActivityLevel() != null) {
            profile.setActivityLevel(request.getActivityLevel());
        }
    }


    public List<UserResponse> toListResponse(List<User> users) {
        return users.stream().map(this::toResponse).collect(Collectors.toList());
    }
}