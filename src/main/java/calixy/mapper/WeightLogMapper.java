package calixy.mapper;

import calixy.domain.entity.User;
import calixy.domain.entity.WeightLog;
import calixy.model.dto.request.WeightLogRequest;
import calixy.model.dto.response.WeightLogResponse;
import calixy.model.dto.response.WeightTrendResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WeightLogMapper {

    public WeightLog toEntity(User user, WeightLogRequest request) {
        return WeightLog.builder()
                .user(user)
                .weightKg(request.getWeightKg())
                .note(request.getNote())
                .date(LocalDate.now())
                .build();
    }

    public WeightLogResponse toResponse(WeightLog log) {
        if (log == null) return null;
        return WeightLogResponse.builder()
                .id(log.getId())
                .weightKg(log.getWeightKg())
                .note(log.getNote())
                .date(log.getDate())
                .loggedAt(log.getLoggedAt())
                .build();
    }

    public List<WeightLogResponse> toResponseList(List<WeightLog> logs) {
        return logs.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public WeightTrendResponse toTrendResponse(List<WeightLog> logs) {
        if (logs.isEmpty()) {
            return WeightTrendResponse.builder()
                    .currentWeight(null)
                    .startWeight(null)
                    .change(null)
                    .logs(List.of())
                    .build();
        }

        Double currentWeight = logs.get(0).getWeightKg();
        Double startWeight   = logs.get(logs.size() - 1).getWeightKg();
        Double change        = Math.round((currentWeight - startWeight) * 10.0) / 10.0;

        return WeightTrendResponse.builder()
                .currentWeight(currentWeight)
                .startWeight(startWeight)
                .change(change)
                .logs(toResponseList(logs))
                .build();
    }
}