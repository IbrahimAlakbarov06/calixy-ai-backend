package calixy.domain.entity;

import calixy.model.enums.SupplementTiming;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "user_supplements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSupplement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplement_id", nullable = false)
    private Supplement supplement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplementTiming timing;

    private LocalTime reminderTime;

    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}