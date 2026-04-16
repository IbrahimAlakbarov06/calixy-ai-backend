package calixy.domain.entity;

import calixy.model.enums.AllergyType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_allergies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAllergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private AllergyType allergy;

    private String customAllergy;

    @CreationTimestamp
    private LocalDateTime createdAt;
}