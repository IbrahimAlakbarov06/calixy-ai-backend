package calixy.service;

import calixy.domain.entity.User;
import calixy.domain.entity.UserProfile;
import calixy.domain.entity.WeightLog;
import calixy.domain.repo.UserProfileRepository;
import calixy.domain.repo.WeightLogRepository;
import calixy.exception.BusinessException;
import calixy.exception.NotFoundException;
import calixy.mapper.WeightLogMapper;
import calixy.model.dto.request.WeightLogRequest;
import calixy.model.dto.response.WeightLogResponse;
import calixy.model.dto.response.WeightTrendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeightLogService {

    private final WeightLogRepository weightLogRepository;
    private final UserProfileRepository userProfileRepository;
    private final WeightLogMapper weightLogMapper;

    @Transactional
    @CacheEvict(value = "weightTrend", allEntries = true)
    public WeightLogResponse logWeight(User user, WeightLogRequest request) {
        WeightLog log = weightLogMapper.toEntity(user, request);
        weightLogRepository.save(log);

        userProfileRepository.findByUserId(user.getId()).ifPresent(profile -> {
            profile.setWeight(request.getWeightKg());
            userProfileRepository.save(profile);
        });

        return weightLogMapper.toResponse(log);
    }

    @Cacheable(value = "weightTrend", key = "#user.id + '_' + #from + '_' + #to")
    @Transactional(readOnly = true)
    public WeightTrendResponse getTrend(User user, LocalDate from, LocalDate to) {
        List<WeightLog> logs = weightLogRepository
                .findByUserIdAndDateBetweenOrderByDateAsc(user.getId(), from, to);
        return weightLogMapper.toTrendResponse(logs);
    }

    @Cacheable(value = "weightTrend", key = "#user.id + '_all'")
    @Transactional(readOnly = true)
    public WeightTrendResponse getAllTrend(User user) {
        List<WeightLog> logs = weightLogRepository.findByUserIdOrderByDateDesc(user.getId());
        return weightLogMapper.toTrendResponse(logs);
    }

    @Transactional
    @CacheEvict(value = "weightTrend", allEntries = true)
    public void deleteLog(User user, Long logId) {
        WeightLog log = weightLogRepository.findById(logId)
                .orElseThrow(() -> new NotFoundException("Weight log not found: " + logId));

        if (!log.getUser().getId().equals(user.getId())) {
            throw new BusinessException("You can only delete your own weight logs");
        }

        weightLogRepository.delete(log);

        weightLogRepository.findTopByUserIdOrderByDateDesc(user.getId())
                .ifPresent(latest -> {
                    userProfileRepository.findByUserId(user.getId()).ifPresent(profile -> {
                        profile.setWeight(latest.getWeightKg());
                        userProfileRepository.save(profile);
                    });
                });
    }
}