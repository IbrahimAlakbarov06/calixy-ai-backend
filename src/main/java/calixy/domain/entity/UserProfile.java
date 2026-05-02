package calixy.domain.entity;

import calixy.model.enums.ActivityLevel;
import calixy.model.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String firstName;

    private String lastName;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;

    private Double height;

    private Double weight;

    private Double targetWeight;

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    private Integer dailyCalorieGoal;
    private Integer dailyProteinGoal;
    private Integer dailyCarbGoal;
    private Integer dailyFatGoal;

    @Builder.Default
    private Integer dailyWaterGoalMl = 2500;

    @Column(length = 5)
    @Builder.Default
    private String language = "en";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public String getFullName() {
        if (firstName == null && lastName == null) return null;
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}