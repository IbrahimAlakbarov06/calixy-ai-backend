package calixy.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "supplements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplement {

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

    private String iconUrl;

    @Builder.Default
    private Boolean isCustom = false;

    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}