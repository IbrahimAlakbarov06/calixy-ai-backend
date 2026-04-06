package calixy.model.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaterDailySummaryResponse {
    private LocalDate date;
    private Integer totalMl;
    private Integer goalMl;
    private Integer remainingMl;
    private Double progressPercent;
    private List<WaterLogResponse> logs;
}