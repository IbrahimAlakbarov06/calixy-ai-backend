package calixy.service;

import calixy.domain.entity.User;
import calixy.domain.entity.UserGoal;
import calixy.domain.entity.UserProfile;
import calixy.domain.repo.UserGoalRepository;
import calixy.domain.repo.UserProfileRepository;
import calixy.domain.repo.UserRepository;
import calixy.exception.NotFoundException;
import calixy.mapper.UserMapper;
import calixy.model.dto.request.SetupProfileRequest;
import calixy.model.dto.request.UpdateUserRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserGoalRepository userGoalRepository;
    private final UserMapper userMapper;
    private final CalorieCalculator calorieCalculator;

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

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElse(UserProfile.builder().user(user).build());

        int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
        CalorieCalculator.MacroResult macros = calorieCalculator.calculateAll(
                request.getGender(), request.getWeight(), request.getHeight(),
                age, request.getActivityLevel(), request.getGoals());

        userMapper.applySetupProfile(profile, request, macros);
        userProfileRepository.save(profile);

        userGoalRepository.deleteByUserId(user.getId());
        userGoalRepository.flush();

        List<UserGoal> userGoals = request.getGoals().stream()
                .map(goal -> UserGoal.builder()
                        .user(user)
                        .goal(goal)
                        .build())
                .collect(Collectors.toList());
        userGoalRepository.saveAllAndFlush(userGoals);

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
                && profile.getGender() != null && profile.getDateOfBirth() != null) {

            int age = Period.between(profile.getDateOfBirth(), LocalDate.now()).getYears();
            List<Goal> goals = userGoalRepository.findByUserId(user.getId())
                    .stream().map(UserGoal::getGoal).collect(Collectors.toList());

            macros = calorieCalculator.calculateAll(
                    profile.getGender(), profile.getWeight(), profile.getHeight(),
                    age, profile.getActivityLevel(), goals);
        }

        userMapper.updateFromRequest(user, profile, request, macros);
        userRepository.save(user);
        userProfileRepository.save(profile);

        User updatedUser = userRepository.findByIdWithProfileAndGoals(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toProfileResponse(updatedUser);
    }

    @Cacheable(value = "adminUsers", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<UserProfileResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toProfileResponse);
    }

    @Cacheable(value = "adminUser", key = "#id")
    public UserProfileResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return userMapper.toProfileResponse(user);
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        user.setStatus(status);
        user.setActive(status == UserStatus.ACTIVE);
        userRepository.save(user);
        return userMapper.toProfileResponse(user);
    }

    @Transactional
    @CacheEvict(value = {"adminUser", "adminUsers"}, allEntries = true)
    public UserProfileResponse updateUserRole(Long id, UserRole role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        user.setRole(role);
        userRepository.save(user);
        return userMapper.toProfileResponse(user);
    }

    @Transactional
    @CacheEvict(value = {"adminUser", "adminUsers", "userProfile"}, allEntries = true)
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        user.setStatus(UserStatus.DELETED);
        user.setActive(false);
        userRepository.save(user);
    }
}