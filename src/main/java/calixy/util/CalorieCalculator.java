package calixy.util;

import calixy.exception.BusinessException;
import calixy.model.enums.ActivityLevel;
import calixy.model.enums.Gender;
import calixy.model.enums.Goal;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalorieCalculator {

    public void validateGoals(List<Goal> goals) {
        if (goals == null || goals.isEmpty()) {
            throw new BusinessException("Exactly one goal must be selected");
        }
        if (goals.size() > 1) {
            throw new BusinessException("Only one goal can be selected at a time");
        }
    }

    public void validateTargetWeight(List<Goal> goals, Double targetWeight, double currentWeight) {
        if (goals == null || goals.isEmpty()) return;

        Goal goal = goals.get(0);

        boolean needsTarget = goal == Goal.LOSE_WEIGHT || goal == Goal.GAIN_WEIGHT;
        if (needsTarget && targetWeight == null) {
            throw new BusinessException("Target weight is required when goal is Lose Weight or Gain Weight");
        }

        if (targetWeight != null) {
            if (goal == Goal.LOSE_WEIGHT && targetWeight >= currentWeight) {
                throw new BusinessException("Target weight must be less than current weight for Lose Weight goal");
            }
            if (goal == Goal.GAIN_WEIGHT && targetWeight <= currentWeight) {
                throw new BusinessException("Target weight must be greater than current weight for Gain Weight goal");
            }
        }
    }

    public int calculateDailyCalories(Gender gender, double weightKg, double heightCm,
                                      int age, ActivityLevel activityLevel, List<Goal> goals) {
        double bmr;
        if (gender == Gender.FEMALE) {
            bmr = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) - 161;
        } else {
            bmr = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) + 5;
        }

        double tdee = bmr * getActivityMultiplier(activityLevel);
        double adjustment = getCalorieAdjustment(goals);

        return (int) Math.round(tdee + adjustment);
    }

    public int calculateProteinGoal(double weightKg, List<Goal> goals) {
        Goal primary = getPrimaryGoal(goals);
        double gramsPerKg = switch (primary) {
            case LOSE_WEIGHT     -> 2.2;
            case BUILD_MUSCLE    -> 2.0;
            case GAIN_WEIGHT     -> 1.8;
            case MAINTAIN_WEIGHT -> 1.6;
            case JUST_BE_HEALTHIER -> 1.4;
        };
        return (int) Math.round(weightKg * gramsPerKg);
    }

    public int calculateCarbGoal(int dailyCalories, List<Goal> goals) {
        Goal primary = getPrimaryGoal(goals);
        double carbPercent = switch (primary) {
            case GAIN_WEIGHT       -> 0.55;
            case BUILD_MUSCLE      -> 0.45;
            case MAINTAIN_WEIGHT   -> 0.50;
            case JUST_BE_HEALTHIER -> 0.50;
            case LOSE_WEIGHT       -> 0.35;
        };
        return (int) Math.round((dailyCalories * carbPercent) / 4.0);
    }

    public int calculateFatGoal(int dailyCalories, List<Goal> goals) {
        Goal primary = getPrimaryGoal(goals);
        double fatPercent = switch (primary) {
            case LOSE_WEIGHT       -> 0.30;
            case JUST_BE_HEALTHIER -> 0.30;
            default                -> 0.25;
        };
        return (int) Math.round((dailyCalories * fatPercent) / 9.0);
    }

    @Getter
    public static class MacroResult {
        private final int calories;
        private final int protein;
        private final int carbs;
        private final int fat;

        public MacroResult(int calories, int protein, int carbs, int fat) {
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
        }
    }

    public MacroResult calculateAll(Gender gender, double weightKg, double heightCm,
                                    int age, ActivityLevel activityLevel, List<Goal> goals) {
        int calories = calculateDailyCalories(gender, weightKg, heightCm, age, activityLevel, goals);
        int protein  = calculateProteinGoal(weightKg, goals);
        int carbs    = calculateCarbGoal(calories, goals);
        int fat      = calculateFatGoal(calories, goals);
        return new MacroResult(calories, protein, carbs, fat);
    }

    private Goal getPrimaryGoal(List<Goal> goals) {
        if (goals == null || goals.isEmpty()) return Goal.MAINTAIN_WEIGHT;
        return goals.get(0);
    }

    private double getCalorieAdjustment(List<Goal> goals) {
        Goal primary = getPrimaryGoal(goals);
        return switch (primary) {
            case LOSE_WEIGHT       -> -500;
            case GAIN_WEIGHT       -> +500;
            case BUILD_MUSCLE      -> +250;
            case MAINTAIN_WEIGHT   ->    0;
            case JUST_BE_HEALTHIER ->    0;
        };
    }

    private double getActivityMultiplier(ActivityLevel level) {
        if (level == null) return 1.2;
        return switch (level) {
            case SEDENTARY         -> 1.2;
            case LIGHT_WALKER      -> 1.375;
            case MODERATELY_ACTIVE -> 1.55;
            case GYM_REGULAR       -> 1.725;
            case YOGA_ZEN          -> 1.375;
        };
    }
}