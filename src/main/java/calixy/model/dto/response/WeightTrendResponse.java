package calixy.model.dto.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeightTrendResponse {
    private Double currentWeight;
    private Double startWeight;
    private Double change;
    private List<WeightLogResponse> logs;
}