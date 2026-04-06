package calixy.mapper;

import calixy.domain.entity.User;
import calixy.domain.entity.WaterLog;
import calixy.model.dto.request.WaterLogRequest;
import calixy.model.dto.response.WaterDailySummaryResponse;
import calixy.model.dto.response.WaterLogResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WaterLogMapper {

    public WaterLog toEntity(User user, WaterLogRequest request) {
        return WaterLog.builder()
                .user(user)
                .amountMl(request.getAmountMl())
                .date(LocalDate.now())
                .build();
    }

    public WaterLogResponse toResponse(WaterLog log) {
        if (log == null) return null;
        return WaterLogResponse.builder()
                .id(log.getId())
                .amountMl(log.getAmountMl())
                .date(log.getDate())
                .loggedAt(log.getLoggedAt())
                .build();
    }

    public List<WaterLogResponse> toResponseList(List<WaterLog> logs) {
        return logs.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public WaterDailySummaryResponse toSummaryResponse(LocalDate date,
                                                       Integer totalMl,
                                                       Integer goalMl,
                                                       List<WaterLog> logs) {
        int remaining = goalMl != null ? Math.max(0, goalMl - totalMl) : 0;
        double progress = goalMl != null && goalMl > 0
                ? Math.min(100.0, (totalMl * 100.0) / goalMl)
                : 0.0;

        return WaterDailySummaryResponse.builder()
                .date(date)
                .totalMl(totalMl)
                .goalMl(goalMl)
                .remainingMl(remaining)
                .progressPercent(Math.round(progress * 10.0) / 10.0)
                .logs(toResponseList(logs))
                .build();
    }
}