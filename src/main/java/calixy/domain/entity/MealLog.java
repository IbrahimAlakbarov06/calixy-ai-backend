package calixy.domain.entity;

import calixy.model.enums.MealSource;
import calixy.model.enums.MealType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_plan_id")
    private DietPlan dietPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

    @Column(nullable = false)
    private Double portionGrams;

    @Column(nullable = false)
    private Double calories;

    @Column(nullable = false)
    private Double protein;

    @Column(nullable = false)
    private Double carbs;

    @Column(nullable = false)
    private Double fat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealSource source;

    @Column(nullable = false)
    private LocalDate date;

    @CreationTimestamp
    private LocalDateTime loggedAt;
}