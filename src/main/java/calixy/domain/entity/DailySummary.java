package calixy.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_summaries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Builder.Default
    private Double totalCalories = 0.0;

    @Builder.Default
    private Double totalProtein = 0.0;

    @Builder.Default
    private Double totalCarbs = 0.0;

    @Builder.Default
    private Double totalFat = 0.0;

    private Integer calorieGoal;
    private Integer proteinGoal;
    private Integer carbGoal;
    private Integer fatGoal;

    private Double remainingCalories;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}