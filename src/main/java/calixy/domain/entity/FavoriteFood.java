package calixy.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "foods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
