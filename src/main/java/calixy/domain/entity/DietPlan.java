package calixy.domain.entity;

import calixy.model.enums.MealType;
import calixy.model.enums.TargetGoal;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diet_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "name_az")
    private String nameAz;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "name_tr")
    private String nameTr;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "description_az", columnDefinition = "TEXT")
    private String descriptionAz;

    @Column(name = "description_ru", columnDefinition = "TEXT")
    private String descriptionRu;

    @Column(name = "description_tr", columnDefinition = "TEXT")
    private String descriptionTr;

    private String imageUrl;

    @Column(nullable = false)
    private Integer calories;

    @Column(nullable = false)
    private Integer protein;

    @Column(nullable = false)
    private Integer carbs;

    @Column(nullable = false)
    private Integer fat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetGoal targetGoal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "dietPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserDietPlan> userDietPlans = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}