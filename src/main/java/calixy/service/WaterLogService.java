package calixy.service;

import calixy.domain.entity.User;
import calixy.domain.entity.WaterLog;
import calixy.domain.repo.UserProfileRepository;
import calixy.domain.repo.WaterLogRepository;
import calixy.exception.BusinessException;
import calixy.exception.NotFoundException;
import calixy.mapper.WaterLogMapper;
import calixy.model.dto.request.WaterLogRequest;
import calixy.model.dto.response.WaterDailySummaryResponse;
import calixy.model.dto.response.WaterLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterLogService {

    private final WaterLogRepository waterLogRepository;
    private final UserProfileRepository userProfileRepository;
    private final WaterLogMapper waterLogMapper;

    @Transactional
    @CacheEvict(value = "waterSummary", allEntries = true)
    public WaterLogResponse logWater(User user, WaterLogRequest request) {
        WaterLog log = waterLogMapper.toEntity(user, request);
        waterLogRepository.save(log);
        return waterLogMapper.toResponse(log);
    }

    @Cacheable(value = "waterSummary", key = "#user.id + '_today'")
    @Transactional(readOnly = true)
    public WaterDailySummaryResponse getToday(User user) {
        return buildSummary(user, LocalDate.now());
    }

    @Cacheable(value = "waterSummary", key = "#user.id + '_' + #date")
    @Transactional(readOnly = true)
    public WaterDailySummaryResponse getByDate(User user, LocalDate date) {
        return buildSummary(user, date);
    }

    @Transactional
    @CacheEvict(value = "waterSummary", allEntries = true)
    public void deleteLog(User user, Long logId) {
        WaterLog log = waterLogRepository.findById(logId)
                .orElseThrow(() -> new NotFoundException("Water log not found: " + logId));

        if (!log.getUser().getId().equals(user.getId())) {
            throw new BusinessException("You can only delete your own water logs");
        }

        waterLogRepository.delete(log);
    }

    @Transactional(readOnly = true)
    public List<WaterLogResponse> getHistory(User user, LocalDate from, LocalDate to) {
        return waterLogMapper.toResponseList(
                waterLogRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), from, to)
        );
    }

    private WaterDailySummaryResponse buildSummary(User user, LocalDate date) {
        Integer totalMl = waterLogRepository.sumAmountByUserAndDate(user.getId(), date);
        Integer goalMl  = userProfileRepository.findByUserId(user.getId())
                .map(p -> p.getDailyWaterGoalMl())
                .orElse(2500);
        List<WaterLog> logs =
                waterLogRepository.findByUserIdAndDateOrderByLoggedAtDesc(user.getId(), date);

        return waterLogMapper.toSummaryResponse(date, totalMl, goalMl, logs);
    }
}