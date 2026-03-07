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
            throw new BusinessException("At least one goal must be selected");
        }
        if (goals.size() > 3) {
            throw new BusinessException("Maximum 3 goals can be selected");
        }
        if (goals.contains(Goal.LOSE_WEIGHT) && goals.contains(Goal.GAIN_WEIGHT)) {
            throw new BusinessException("Cannot select both Lose Weight and Gain Weight at the same time");
        }
        if (goals.contains(Goal.LOSE_WEIGHT) && goals.contains(Goal.GAIN_MUSCLE)) {
            throw new BusinessException("Cannot select both Lose Weight and Gain Muscle at the same time");
        }
        if (goals.contains(Goal.MAINTAIN_WEIGHT) && goals.contains(Goal.GAIN_WEIGHT)) {
            throw new BusinessException("Cannot select both Maintain Weight and Gain Weight at the same time");
        }

        if (goals.contains(Goal.MAINTAIN_WEIGHT) && goals.contains(Goal.LOSE_WEIGHT)) {
            throw new BusinessException("Cannot select both Maintain Weight and Lose Weight at the same time");
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
        double gramsPerKg;
        Goal primary = getPrimaryGoal(goals);
        gramsPerKg = switch (primary) {
            case GAIN_MUSCLE -> 2.0;
            case LOSE_WEIGHT -> 2.0;
            case GAIN_WEIGHT -> 1.6;
            default -> 1.6;
        };
        return (int) Math.round(weightKg * gramsPerKg);
    }

    public int calculateCarbGoal(int dailyCalories, List<Goal> goals) {
        Goal primary = getPrimaryGoal(goals);
        double carbPercent = switch (primary) {
            case GAIN_WEIGHT -> 0.55;
            case GAIN_MUSCLE -> 0.45;
            case LOSE_WEIGHT -> 0.35;
            default -> 0.50;
        };
        return (int) Math.round((dailyCalories * carbPercent) / 4.0);
    }

    public int calculateFatGoal(int dailyCalories, List<Goal> goals) {
        Goal primary = getPrimaryGoal(goals);
        double fatPercent = switch (primary) {
            case LOSE_WEIGHT -> 0.30;
            default -> 0.25;
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
        int protein = calculateProteinGoal(weightKg, goals);
        int carbs = calculateCarbGoal(calories, goals);
        int fat = calculateFatGoal(calories, goals);
        return new MacroResult(calories, protein, carbs, fat);
    }

    private Goal getPrimaryGoal(List<Goal> goals) {
        if (goals == null || goals.isEmpty()) return Goal.MAINTAIN_WEIGHT;
        if (goals.contains(Goal.LOSE_WEIGHT)) return Goal.LOSE_WEIGHT;
        if (goals.contains(Goal.GAIN_MUSCLE)) return Goal.GAIN_MUSCLE;
        if (goals.contains(Goal.GAIN_WEIGHT)) return Goal.GAIN_WEIGHT;
        if (goals.contains(Goal.MAINTAIN_WEIGHT)) return Goal.MAINTAIN_WEIGHT;
        return Goal.MAINTAIN_WEIGHT;
    }

    private double getCalorieAdjustment(List<Goal> goals) {
        Goal primary = getPrimaryGoal(goals);
        return switch (primary) {
            case LOSE_WEIGHT -> -500;
            case GAIN_WEIGHT -> +500;
            case GAIN_MUSCLE -> +250;
            default -> 0;
        };
    }

    private double getActivityMultiplier(ActivityLevel level) {
        if (level == null) return 1.2;
        return switch (level) {
            case SEDENTARY -> 1.2;
            case LIGHT -> 1.375;
            case MODERATE -> 1.55;
            case ACTIVE -> 1.725;
            case VERY_ACTIVE -> 1.9;
        };
    }
}
