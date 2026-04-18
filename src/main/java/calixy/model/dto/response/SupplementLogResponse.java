package calixy.model.dto.response;

import calixy.model.enums.SupplementStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplementLogResponse {
    private Long id;
    private UserSupplementResponse userSupplement;
    private SupplementStatus status;
    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loggedAt;
}