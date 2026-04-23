package calixy.service;

import calixy.domain.entity.*;
import calixy.domain.repo.*;
import calixy.exception.InvalidInputException;
import calixy.exception.NotFoundException;
import calixy.mapper.UserMapper;
import calixy.model.dto.request.ChangePasswordRequest;
import calixy.model.dto.request.SetupProfileRequest;
import calixy.model.dto.request.UpdateUserRequest;
import calixy.model.dto.response.MessageResponse;
import calixy.model.dto.response.UserProfileResponse;
import calixy.model.enums.ActivityLevel;
import calixy.model.enums.Goal;
import calixy.model.enums.UserRole;
import calixy.model.enums.UserStatus;
import calixy.util.CalorieCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserGoalRepository userGoalRepository;
    private final UserAllergyRepository userAllergyRepository;
    private final UserDietaryRuleRepository userDietaryRuleRepository;
    private final UserMapper userMapper;
    private final CalorieCalculator calorieCalculator;
    private final PasswordEncoder passwordEncoder;

    @Cacheable(value = "userProfile", key = "#user.email")
    public UserProfileResponse getMyProfile(User user) {
        User fullUser = userRepository.findByIdWithProfileAndGoals(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toProfileResponse(fullUser);
    }

    @Transactional
    @CacheEvict(value = "userProfile", key = "#user.email")
    public UserProfileResponse setupProfile(User user, SetupProfileRequest request) {
        if (request.getActivityLevel() == null) {
            request.setActivityLevel(ActivityLevel.MODERATE);
        }
        if (request.getGoals() == null || request.getGoals().isEmpty()) {
            request.setGoals(List.of(Goal.MAINTAIN_WEIGHT));
        }

        calorieCalculator.validateGoals(request.getGoals());
        calorieCalculator.validateTargetWeight(
                request.getGoals(), request.getTargetWeight(), request.getWeight());

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElse(UserProfile.builder().user(user).build());

        CalorieCalculator.MacroResult macros = calorieCalculator.calculateAll(
                request.getGender(), request.getWeight(), request.getHeight(),
                request.getAge(), request.getActivityLevel(), request.getGoals());

        userMapper.applySetupProfile(profile, request, macros);
        userProfileRepository.save(profile);

        userGoalRepository.deleteByUserId(user.getId());
        userGoalRepository.flush();
        List<UserGoal> userGoals = request.getGoals().stream()
                .map(goal -> UserGoal.builder().user(user).goal(goal).build())
                .collect(Collectors.toList());
        userGoalRepository.saveAllAndFlush(userGoals);

        userAllergyRepository.deleteByUserId(user.getId());
        userAllergyRepository.flush();
        List<UserAllergy> allergies = new ArrayList<>();
        if (request.getAllergies() != null) {
            request.getAllergies().forEach(a ->
                    allergies.add(UserAllergy.builder().user(user).allergy(a).build()));
        }
        if (request.getCustomAllergies() != null) {
            request.getCustomAllergies().stream()
                    .filter(c -> c != null && !c.isBlank())
                    .forEach(c ->
                            allergies.add(UserAllergy.builder().user(user).customAllergy(c.trim()).build()));
        }
        if (!allergies.isEmpty()) {
            userAllergyRepository.saveAll(allergies);
        }

        userDietaryRuleRepository.deleteByUserId(user.getId());
        userDietaryRuleRepository.flush();
        if (request.getDietaryRules() != null && !request.getDietaryRules().isEmpty()) {
            List<UserDietaryRule> rules = request.getDietaryRules().stream()
                    .map(r -> UserDietaryRule.builder().user(user).rule(r).build())
                    .collect(Collectors.toList());
            userDietaryRuleRepository.saveAll(rules);
        }

        User updatedUser = userRepository.findByIdWithProfileAndGoals(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toProfileResponse(updatedUser);
    }

    @Transactional
    @CacheEvict(value = "userProfile", key = "#user.email")
    public UserProfileResponse updateProfile(User user, UpdateUserRequest request) {
        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElse(UserProfile.builder().user(user).build());

        CalorieCalculator.MacroResult macros = null;
        if (profile.getHeight() != null && profile.getWeight() != null
                && profile.getGender() != null && profile.getAge() != null) {

            List<Goal> goals = userGoalRepository.findByUserId(user.getId())
                    .stream().map(UserGoal::getGoal).collect(Collectors.toList());

            macros = calorieCalculator.calculateAll(
                    profile.getGender(), profile.getWeight(), profile.getHeight(),
                    profile.getAge(), profile.getActivityLevel(), goals);
        }

        userMapper.updateFromRequest(user, profile, request, macros);
        userRepository.save(user);
        userProfileRepository.save(profile);

        User updatedUser = userRepository.findByIdWithProfileAndGoals(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toProfileResponse(updatedUser);
    }

    @Transactional
    @CacheEvict(value = "userProfile", key = "#user.id")
    public MessageResponse changePassword(Long id, ChangePasswordRequest request) {
        User user = findUserById(id);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidInputException("Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidInputException("New password and confirmation do not match");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidInputException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return new MessageResponse("Password updated successfully");
    }

    @Cacheable(value = "adminUsers", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<UserProfileResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toProfileResponse);
    }

    @Cacheable(value = "adminUser", key = "#id")
    public UserProfileResponse getUserById(Long id) {
        return userMapper.toProfileResponse(findUserById(id));
    }

    @Cacheable(value = "adminUser", key = "#email")
    public UserProfileResponse getUserByEmail(String email) {
        User user = userRepository.findByEmailWithProfileAndGoals(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        return userMapper.toProfileResponse(user);
    }

    @Transactional
    @CacheEvict(value = {"adminUser", "adminUsers"}, allEntries = true)
    public UserProfileResponse updateUserStatus(Long id, UserStatus status) {
        User user = findUserById(id);
        user.setStatus(status);
        user.setActive(status == UserStatus.ACTIVE);
        userRepository.save(user);
        return userMapper.toProfileResponse(user);
    }

    @Transactional
    @CacheEvict(value = {"adminUser", "adminUsers"}, allEntries = true)
    public UserProfileResponse updateUserRole(Long id, UserRole role) {
        User user = findUserById(id);
        user.setRole(role);
        userRepository.save(user);
        return userMapper.toProfileResponse(user);
    }

    @Transactional
    @CacheEvict(value = {"adminUser", "adminUsers", "userProfile"}, allEntries = true)
    public void deleteUser(Long id) {
        User user = findUserById(id);
        user.setStatus(UserStatus.DELETED);
        user.setActive(false);
        userRepository.save(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }
}