package calixy.model.dto.request;

import calixy.model.enums.SupplementTiming;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddUserSupplementRequest {

    private Long supplementId;

    private String customName;

    @NotNull(message = "Timing is required")
    private SupplementTiming timing;

    private LocalTime reminderTime;
}