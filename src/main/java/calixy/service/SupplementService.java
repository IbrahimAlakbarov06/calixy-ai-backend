package calixy.service;

import calixy.domain.entity.*;
import calixy.domain.repo.*;
import calixy.exception.AlreadyExistsException;
import calixy.exception.BusinessException;
import calixy.exception.NotFoundException;
import calixy.mapper.SupplementMapper;
import calixy.model.dto.request.AddUserSupplementRequest;
import calixy.model.dto.request.CreateSupplementRequest;
import calixy.model.dto.response.*;
import calixy.model.enums.SupplementStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplementService {

    private final SupplementRepository supplementRepository;
    private final UserSupplementRepository userSupplementRepository;
    private final SupplementLogRepository supplementLogRepository;
    private final SupplementMapper supplementMapper;

    @Cacheable(value = "supplements", key = "'catalog_' + #lang")
    @Transactional(readOnly = true)
    public List<SupplementResponse> getCatalog(String lang) {
        return supplementRepository.findByIsCustomFalseAndIsActiveTrue()
                .stream()
                .map(supplementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserSupplementResponse> getMySupplement(User user, String lang) {
        return userSupplementRepository.findByUserIdAndIsActiveTrue(user.getId())
                .stream()
                .map(supplementMapper::toUserSupplementResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserSupplementResponse addToMySupplement(User user, AddUserSupplementRequest request, String lang) {
        if (request.getSupplementId() == null && (request.getCustomName() == null || request.getCustomName().isBlank())) {
            throw new BusinessException("Either supplementId or customName must be provided");
        }

        Supplement supplement;

        if (request.getSupplementId() != null) {
            if (userSupplementRepository.existsByUserIdAndSupplementIdAndIsActiveTrue(
                    user.getId(), request.getSupplementId())) {
                throw new AlreadyExistsException("This supplement is already in your list");
            }
            supplement = supplementRepository.findById(request.getSupplementId())
                    .orElseThrow(() -> new NotFoundException("Supplement not found: " + request.getSupplementId()));
        } else {
            supplement = Supplement.builder()
                    .name(request.getCustomName().trim())
                    .isCustom(true)
                    .isActive(true)
                    .build();
            supplement = supplementRepository.save(supplement);
        }

        UserSupplement us = UserSupplement.builder()
                .user(user)
                .supplement(supplement)
                .timing(request.getTiming())
                .reminderTime(request.getReminderTime())
                .isActive(true)
                .build();

        return supplementMapper.toUserSupplementResponse(userSupplementRepository.save(us));
    }

    @Transactional
    public void removeFromMySupplement(User user, Long userSupplementId) {
        UserSupplement us = userSupplementRepository.findById(userSupplementId)
                .orElseThrow(() -> new NotFoundException("Supplement not found: " + userSupplementId));

        if (!us.getUser().getId().equals(user.getId())) {
            throw new BusinessException("You can only remove your own supplements");
        }

        us.setIsActive(false);
        userSupplementRepository.save(us);
    }

    @Transactional(readOnly = true)
    public DailySupplementChecklistResponse getChecklist(User user, LocalDate date, String lang) {
        List<UserSupplement> userSupplements =
                userSupplementRepository.findByUserIdAndIsActiveTrue(user.getId());
        List<SupplementLog> logs =
                supplementLogRepository.findByUserSupplementUserIdAndDate(user.getId(), date);

        return supplementMapper.toChecklist(userSupplements, logs, date);
    }

    @Transactional
    public SupplementLogResponse logSupplement(User user, Long userSupplementId,
                                               SupplementStatus status, LocalDate date, String lang) {
        UserSupplement us = userSupplementRepository.findById(userSupplementId)
                .orElseThrow(() -> new NotFoundException("Supplement not found: " + userSupplementId));

        if (!us.getUser().getId().equals(user.getId())) {
            throw new BusinessException("You can only log your own supplements");
        }

        List<SupplementLog> existing = supplementLogRepository
                .findByUserSupplementIdAndDateBetweenOrderByDateDesc(
                        userSupplementId, date, date);

        SupplementLog log;
        if (!existing.isEmpty()) {
            log = existing.get(0);
            log.setStatus(status);
        } else {
            log = SupplementLog.builder()
                    .userSupplement(us)
                    .status(status)
                    .date(date)
                    .build();
        }

        return supplementMapper.toLogResponse(supplementLogRepository.save(log));
    }

    @CacheEvict(value = "supplements", allEntries = true)
    @Transactional
    public SupplementResponse createSupplement(CreateSupplementRequest request, String lang) {
        Supplement s = supplementMapper.toEntity(request);

        return supplementMapper.toResponse(supplementRepository.save(s));
    }
}