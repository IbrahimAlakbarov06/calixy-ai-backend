package calixy.domain.entity;

import calixy.model.enums.FoodCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "foods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {

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

    @Column(nullable = false)
    private Double caloriesPer100g;

    @Column(nullable = false)
    private Double proteinPer100g;

    @Column(nullable = false)
    private Double carbsPer100g;

    @Column(nullable = false)
    private Double fatsPer100g;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodCategory category;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAzerbaijani = false;

    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MealLog> mealLogs = new ArrayList<>();

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<FavoriteFood> favoriteFoods = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}