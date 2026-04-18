package calixy.model.dto.response;

import calixy.model.enums.SupplementTiming;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSupplementResponse {
    private Long id;
    private SupplementResponse supplement;
    private SupplementTiming timing;
    private LocalTime reminderTime;
    private Boolean isActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}