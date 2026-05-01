package calixy.model.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailySupplementChecklistResponse {
    private LocalDate date;
    private List<SupplementChecklistItem> items;
    private Integer total;
    private Integer taken;
    private Integer skipped;
    private Integer postponed;
    private Integer pending;
}