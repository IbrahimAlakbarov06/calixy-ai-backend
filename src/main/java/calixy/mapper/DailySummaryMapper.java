package calixy.mapper;

import calixy.domain.entity.DailySummary;
import calixy.domain.entity.User;
import calixy.domain.entity.UserProfile;
import calixy.domain.repo.DailySummaryRepository;
import calixy.domain.repo.MealLogRepository;
import calixy.domain.repo.UserProfileRepository;
import calixy.domain.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DailySummaryMapper {

    private final DailySummaryRepository dailySummaryRepository;
    private final UserProfileRepository userProfileRepository;
    private final MealLogRepository mealLogRepository;

    public DailySummary toEntity(User user, LocalDate date,
                                 Double totalCalories, Double totalProtein,
                                 Double totalCarbs, Double totalFat,
                                 UserProfile profile,
                                 DailySummary existing) {

        DailySummary summary = existing != null
                ? existing
                : DailySummary.builder().user(user).date(date).build();

        summary.setTotalCalories(totalCalories);
        summary.setTotalProtein(totalProtein);
        summary.setTotalCarbs(totalCarbs);
        summary.setTotalFat(totalFat);
        summary.setCalorieGoal(profile != null ? profile.getDailyCalorieGoal() : null);
        summary.setProteinGoal(profile != null ? profile.getDailyProteinGoal() : null);
        summary.setCarbGoal(profile != null ? profile.getDailyCarbGoal() : null);
        summary.setFatGoal(profile != null ? profile.getDailyFatGoal() : null);
        summary.setRemainingCalories(
                profile != null && profile.getDailyCalorieGoal() != null
                        ? profile.getDailyCalorieGoal() - totalCalories
                        : null
        );

        return summary;
    }

    public void updateDailySummary(User user, LocalDate date) {
        DailySummary existing = dailySummaryRepository
                .findByUserIdAndDate(user.getId(), date).orElse(null);

        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);

        DailySummary summary = toEntity(
                user, date,
                mealLogRepository.sumCaloriesByUserAndDate(user.getId(), date),
                mealLogRepository.sumProteinByUserAndDate(user.getId(), date),
                mealLogRepository.sumCarbsByUserAndDate(user.getId(), date),
                mealLogRepository.sumFatByUserAndDate(user.getId(), date),
                profile,
                existing
        );

        dailySummaryRepository.save(summary);
    }
}