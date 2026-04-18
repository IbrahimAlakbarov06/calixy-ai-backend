package calixy.model.dto.response;

import calixy.model.enums.SupplementStatus;
import calixy.model.enums.SupplementTiming;
import lombok.*;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplementChecklistItem {
    private Long userSupplementId;
    private Long supplementLogId;
    private String supplementName;
    private String supplementNameAz;
    private Boolean isCustom;
    private SupplementTiming timing;
    private LocalTime reminderTime;
    private SupplementStatus status;
}