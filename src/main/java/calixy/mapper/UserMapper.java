package calixy.mapper;

import calixy.domain.entity.*;
import calixy.domain.repo.UserAllergyRepository;
import calixy.domain.repo.UserDietaryRuleRepository;
import calixy.model.dto.request.RegisterRequest;
import calixy.model.dto.request.SetupProfileRequest;
import calixy.model.dto.request.UpdateUserRequest;
import calixy.model.dto.response.UserProfileResponse;
import calixy.model.dto.response.UserResponse;
import calixy.model.enums.*;
import calixy.util.CalorieCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final UserAllergyRepository userAllergyRepository;
    private final UserDietaryRuleRepository userDietaryRuleRepository;

    public UserResponse toResponse(User user) {
        String firstName = null;
        String lastName = null;
        String profileImage = null;
        try {
            UserProfile profile = user.getProfile();
            if (profile != null) {
                firstName = profile.getFirstName();
                lastName = profile.getLastName();
                profileImage = profile.getProfileImage();
            }
        } catch (Exception ignored) {}

        return UserResponse.builder()
                .id(user.getId())
                .firstName(firstName)
                .lastName(lastName)
                .email(user.getEmail())
                .profileImage(profileImage)
                .status(user.getStatus())
                .role(user.getRole())
                .authProvider(user.getAuthProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UserProfileResponse toProfileResponse(User user) {
        UserProfile profile = null;
        try { profile = user.getProfile(); } catch (Exception ignored) {}

        List<Goal> goals = List.of();
        try {
            if (user.getGoals() != null) {
                goals = user.getGoals().stream()
                        .map(UserGoal::getGoal)
                        .collect(Collectors.toList());
            }
        } catch (Exception ignored) {}

        List<UserAllergy> allergyEntities = userAllergyRepository.findByUserId(user.getId());
        List<AllergyType> allergies = allergyEntities.stream()
                .filter(a -> a.getAllergy() != null)
                .map(UserAllergy::getAllergy)
                .collect(Collectors.toList());
        List<String> customAllergies = allergyEntities.stream()
                .filter(a -> a.getCustomAllergy() != null)
                .map(UserAllergy::getCustomAllergy)
                .collect(Collectors.toList());

        List<DietaryRule> dietaryRules = userDietaryRuleRepository.findByUserId(user.getId())
                .stream()
                .map(UserDietaryRule::getRule)
                .collect(Collectors.toList());

        Goal currentGoal = goals.isEmpty() ? null : goals.get(0);

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(profile != null ? profile.getFullName() : null)
                .email(user.getEmail())
                .profileImage(profile != null ? profile.getProfileImage() : null)
                .gender(profile != null ? profile.getGender() : null)
                .age(profile != null ? profile.getAge() : null)
                .height(profile != null ? profile.getHeight() : null)
                .weight(profile != null ? profile.getWeight() : null)
                .targetWeight(profile != null ? profile.getTargetWeight() : null)
                .activityLevel(profile != null ? profile.getActivityLevel() : null)
                .dailyCalorieGoal(profile != null ? profile.getDailyCalorieGoal() : null)
                .dailyProteinGoal(profile != null ? profile.getDailyProteinGoal() : null)
                .dailyCarbGoal(profile != null ? profile.getDailyCarbGoal() : null)
                .dailyFatGoal(profile != null ? profile.getDailyFatGoal() : null)
                .dailyWaterGoalMl(profile != null ? profile.getDailyWaterGoalMl() : null)
                .goal(currentGoal)
                .allergies(allergies)
                .customAllergies(customAllergies)
                .dietaryRules(dietaryRules)
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
                .profileImage(profileImage)
                .build();

        user.setProfile(profile);
        return user;
    }

    public void applySetupProfile(UserProfile profile, SetupProfileRequest request,
                                  CalorieCalculator.MacroResult macros) {
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setGender(request.getGender());
        profile.setAge(request.getAge());
        profile.setHeight(request.getHeight());
        profile.setWeight(request.getWeight());
        profile.setTargetWeight(request.getTargetWeight());
        profile.setActivityLevel(request.getActivityLevel());
        profile.setDailyCalorieGoal(macros.getCalories());
        profile.setDailyProteinGoal(macros.getProtein());
        profile.setDailyCarbGoal(macros.getCarbs());
        profile.setDailyFatGoal(macros.getFat());

        if (request.getLanguage() != null && !request.getLanguage().isBlank()) {
            profile.setLanguage(request.getLanguage());
        }

        if (request.getProfileImage() != null && !request.getProfileImage().isBlank()) {
            profile.setProfileImage(request.getProfileImage());
        }
    }

    public void updateFromRequest(User user, UserProfile profile,
                                  UpdateUserRequest request,
                                  CalorieCalculator.MacroResult macros) {
        if (request.getProfileImage() != null && !request.getProfileImage().isBlank())
            profile.setProfileImage(request.getProfileImage());
        if (request.getFirstName() != null)     profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null)      profile.setLastName(request.getLastName());
        if (request.getGender() != null)        profile.setGender(request.getGender());
        if (request.getAge() != null)           profile.setAge(request.getAge());
        if (request.getHeight() != null)        profile.setHeight(request.getHeight());
        if (request.getWeight() != null)        profile.setWeight(request.getWeight());
        if (request.getActivityLevel() != null) profile.setActivityLevel(request.getActivityLevel());

        if (request.getLanguage() != null && !request.getLanguage().isBlank()) {
            profile.setLanguage(request.getLanguage());
        }

        if (macros != null) {
            profile.setDailyCalorieGoal(macros.getCalories());
            profile.setDailyProteinGoal(macros.getProtein());
            profile.setDailyCarbGoal(macros.getCarbs());
            profile.setDailyFatGoal(macros.getFat());
        }
    }

    public List<UserResponse> toListResponse(List<User> users) {
        return users.stream().map(this::toResponse).collect(Collectors.toList());
    }
}