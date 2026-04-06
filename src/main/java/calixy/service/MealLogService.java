package calixy.service;

import calixy.domain.entity.*;
import calixy.domain.repo.*;
import calixy.exception.BusinessException;
import calixy.exception.NotFoundException;
import calixy.mapper.DailySummaryMapper;
import calixy.mapper.MealLogMapper;
import calixy.model.dto.request.LogMealRequest;
import calixy.model.dto.response.DailySummaryResponse;
import calixy.model.dto.response.MealLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealLogService {

    private final MealLogRepository mealLogRepository;
    private final DailySummaryRepository dailySummaryRepository;
    private final FoodRepository foodRepository;
    private final DietPlanRepository dietPlanRepository;
    private final UserProfileRepository userProfileRepository;
    private final MealLogMapper mealLogMapper;
    private final DailySummaryMapper dailySummaryMapper;

    @Transactional
    @CacheEvict(value = "dailySummary", allEntries = true)
    public MealLogResponse logMeal(User user, LogMealRequest request) {
        if (request.getFoodId() == null && request.getDietPlanId() == null) {
            throw new BusinessException("Either foodId or dietPlanId must be provided");
        }

        Food food = null;
        DietPlan dietPlan = null;
        double calories, protein, carbs, fat;

        if (request.getFoodId() != null) {
            food = foodRepository.findById(request.getFoodId())
                    .orElseThrow(() -> new NotFoundException("Food not found: " + request.getFoodId()));

            double ratio = request.getPortionGrams() / 100.0;
            calories = food.getCaloriesPer100g() * ratio;
            protein  = food.getProteinPer100g()  * ratio;
            carbs    = food.getCarbsPer100g()     * ratio;
            fat      = food.getFatsPer100g()      * ratio;
        } else {
            dietPlan = dietPlanRepository.findById(request.getDietPlanId())
                    .orElseThrow(() -> new NotFoundException("Diet plan not found: " + request.getDietPlanId()));

            calories = dietPlan.getCalories();
            protein  = dietPlan.getProtein();
            carbs    = dietPlan.getCarbs();
            fat      = dietPlan.getFat();
        }

        MealLog log = mealLogMapper.toEntity(user, food, dietPlan, request, calories, protein, carbs, fat);
        mealLogRepository.save(log);
        dailySummaryMapper.updateDailySummary(user, LocalDate.now());

        return mealLogMapper.toResponse(log);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "dailySummary", key = "#user.id + '_today'")
    public DailySummaryResponse getToday(User user) {
        return buildDailySummary(user, LocalDate.now());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "dailySummary", key = "#user.id + '_' + #date")
    public DailySummaryResponse getByDate(User user, LocalDate date) {
        return buildDailySummary(user, date);
    }

    @Transactional
    @CacheEvict(value = "dailySummary", allEntries = true)
    public void deleteMealLog(User user, Long logId) {
        MealLog log = mealLogRepository.findById(logId)
                .orElseThrow(() -> new NotFoundException("Meal log not found: " + logId));

        if (!log.getUser().getId().equals(user.getId())) {
            throw new BusinessException("You can only delete your own meal logs");
        }

        LocalDate date = log.getDate();
        mealLogRepository.delete(log);
        dailySummaryMapper.updateDailySummary(user, date);
    }

    @Transactional(readOnly = true)
    public List<MealLogResponse> getHistory(User user, LocalDate from, LocalDate to) {
        return mealLogMapper.toResponseList(
                mealLogRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), from, to)
        );
    }

    private DailySummaryResponse buildDailySummary(User user, LocalDate date) {
        List<MealLog> logs = mealLogRepository.findByUserIdAndDateOrderByLoggedAtDesc(user.getId(), date);
        DailySummary summary = dailySummaryRepository.findByUserIdAndDate(user.getId(), date).orElse(null);
        return mealLogMapper.toDailySummaryResponse(summary, logs, date);
    }
}