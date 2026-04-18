package calixy.domain.entity;

import calixy.model.enums.SupplementStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplement_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplementLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_supplement_id", nullable = false)
    private UserSupplement userSupplement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplementStatus status;

    @Column(nullable = false)
    private LocalDate date;

    @CreationTimestamp
    private LocalDateTime loggedAt;
}