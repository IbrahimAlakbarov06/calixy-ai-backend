package calixy.service;

import calixy.domain.entity.DailySummary;
import calixy.domain.entity.User;
import calixy.domain.entity.WeightLog;
import calixy.domain.repo.*;
import calixy.mapper.MealLogMapper;
import calixy.model.dto.response.AnalyticsResponse;
import calixy.model.dto.response.DailySummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final DailySummaryRepository dailySummaryRepository;
    private final WaterLogRepository waterLogRepository;
    private final WeightLogRepository weightLogRepository;
    private final UserProfileRepository userProfileRepository;
    private final MealLogRepository mealLogRepository;
    private final MealLogMapper mealLogMapper;

    @Cacheable(value = "analytics", key = "#user.id + '_daily_' + #date")
    @Transactional(readOnly = true)
    public AnalyticsResponse getDaily(User user, LocalDate date) {
        DailySummary summary = dailySummaryRepository
                .findByUserIdAndDate(user.getId(), date).orElse(null);

        Integer waterTotal = waterLogRepository.sumAmountByUserAndDate(user.getId(), date);
        Integer waterGoal = userProfileRepository.findByUserId(user.getId())
                .map(p -> p.getDailyWaterGoalMl()).orElse(2500);

        WeightLog latestWeight = weightLogRepository
                .findTopByUserIdOrderByDateDesc(user.getId()).orElse(null);

        var logs = mealLogRepository.findByUserIdAndDateOrderByLoggedAtDesc(user.getId(), date);

        return AnalyticsResponse.builder()
                .from(date)
                .to(date)
                .totalCalories(summary != null ? summary.getTotalCalories() : 0.0)
                .avgDailyCalories(summary != null ? summary.getTotalCalories() : 0.0)
                .calorieGoal(summary != null ? summary.getCalorieGoal() : null)
                .avgProtein(summary != null ? summary.getTotalProtein() : 0.0)
                .avgCarbs(summary != null ? summary.getTotalCarbs() : 0.0)
                .avgFat(summary != null ? summary.getTotalFat() : 0.0)
                .proteinGoal(summary != null ? summary.getProteinGoal() : null)
                .carbGoal(summary != null ? summary.getCarbGoal() : null)
                .fatGoal(summary != null ? summary.getFatGoal() : null)
                .avgWaterMl(waterTotal != null ? waterTotal.doubleValue() : 0.0)
                .waterGoalMl(waterGoal)
                .currentWeight(latestWeight != null ? latestWeight.getWeightKg() : null)
                .dailyBreakdown(List.of(
                        mealLogMapper.toDailySummaryResponse(summary, logs, date)
                ))
                .build();
    }

    @Cacheable(value = "analytics", key = "#user.id + '_weekly_' + #from")
    @Transactional(readOnly = true)
    public AnalyticsResponse getWeekly(User user, LocalDate from) {
        LocalDate to = from.plusDays(6);
        return buildRangeAnalytics(user, from, to);
    }

    @Cacheable(value = "analytics", key = "#user.id + '_monthly_' + #from")
    @Transactional(readOnly = true)
    public AnalyticsResponse getMonthly(User user, LocalDate from) {
        LocalDate to = from.plusMonths(1).minusDays(1);
        return buildRangeAnalytics(user, from, to);
    }

    @Cacheable(value = "analytics", key = "#user.id + '_range_' + #from + '_' + #to")
    @Transactional(readOnly = true)
    public AnalyticsResponse getRange(User user, LocalDate from, LocalDate to) {
        return buildRangeAnalytics(user, from, to);
    }

    private AnalyticsResponse buildRangeAnalytics(User user, LocalDate from, LocalDate to) {
        List<DailySummary> summaries = dailySummaryRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), from, to);

        int days = summaries.size();

        double totalCalories = summaries.stream()
                .mapToDouble(s -> s.getTotalCalories() != null ? s.getTotalCalories() : 0.0).sum();
        double totalProtein = summaries.stream()
                .mapToDouble(s -> s.getTotalProtein() != null ? s.getTotalProtein() : 0.0).sum();
        double totalCarbs = summaries.stream()
                .mapToDouble(s -> s.getTotalCarbs() != null ? s.getTotalCarbs() : 0.0).sum();
        double totalFat = summaries.stream()
                .mapToDouble(s -> s.getTotalFat() != null ? s.getTotalFat() : 0.0).sum();

        double avgCalories = days > 0 ? totalCalories / days : 0.0;
        double avgProtein  = days > 0 ? totalProtein  / days : 0.0;
        double avgCarbs    = days > 0 ? totalCarbs    / days : 0.0;
        double avgFat      = days > 0 ? totalFat      / days : 0.0;

        List<calixy.domain.entity.WaterLog> waterLogs = waterLogRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), from, to);
        double totalWater = waterLogs.stream().mapToInt(w -> w.getAmountMl()).sum();
        double avgWater = days > 0 ? totalWater / days : 0.0;

        Integer waterGoal = userProfileRepository.findByUserId(user.getId())
                .map(p -> p.getDailyWaterGoalMl()).orElse(2500);

        List<WeightLog> weightLogs = weightLogRepository
                .findByUserIdAndDateBetweenOrderByDateAsc(user.getId(), from, to);
        Double currentWeight = weightLogs.isEmpty() ? null
                : weightLogs.get(weightLogs.size() - 1).getWeightKg();
        Double startWeight = weightLogs.isEmpty() ? null
                : weightLogs.get(0).getWeightKg();
        Double weightChange = (currentWeight != null && startWeight != null)
                ? Math.round((currentWeight - startWeight) * 10.0) / 10.0
                : null;

        Integer calorieGoal = summaries.isEmpty() ? null : summaries.get(0).getCalorieGoal();
        Integer proteinGoal = summaries.isEmpty() ? null : summaries.get(0).getProteinGoal();
        Integer carbGoal    = summaries.isEmpty() ? null : summaries.get(0).getCarbGoal();
        Integer fatGoal     = summaries.isEmpty() ? null : summaries.get(0).getFatGoal();

        List<DailySummaryResponse> breakdown = summaries.stream().map(summary -> {
            var logs = mealLogRepository.findByUserIdAndDateOrderByLoggedAtDesc(
                    user.getId(), summary.getDate());
            return mealLogMapper.toDailySummaryResponse(summary, logs, summary.getDate());
        }).collect(Collectors.toList());

        return AnalyticsResponse.builder()
                .from(from)
                .to(to)
                .totalCalories(round(totalCalories))
                .avgDailyCalories(round(avgCalories))
                .calorieGoal(calorieGoal)
                .avgProtein(round(avgProtein))
                .avgCarbs(round(avgCarbs))
                .avgFat(round(avgFat))
                .proteinGoal(proteinGoal)
                .carbGoal(carbGoal)
                .fatGoal(fatGoal)
                .avgWaterMl(round(avgWater))
                .waterGoalMl(waterGoal)
                .currentWeight(currentWeight)
                .startWeight(startWeight)
                .weightChange(weightChange)
                .dailyBreakdown(breakdown)
                .build();
    }

    private Double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}