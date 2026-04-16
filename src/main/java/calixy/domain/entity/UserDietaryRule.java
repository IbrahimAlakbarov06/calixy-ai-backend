package calixy.domain.entity;

import calixy.model.enums.DietaryRule;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_dietary_rules",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "rule"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDietaryRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DietaryRule rule;

    @CreationTimestamp
    private LocalDateTime createdAt;
}