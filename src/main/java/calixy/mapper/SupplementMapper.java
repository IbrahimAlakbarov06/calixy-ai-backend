package calixy.mapper;

import calixy.domain.entity.*;
import calixy.model.dto.request.CreateSupplementRequest;
import calixy.model.dto.response.*;
import calixy.model.enums.SupplementStatus;
import calixy.util.LanguageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SupplementMapper {

    private final LanguageUtil languageUtil;

    public Supplement toEntity(CreateSupplementRequest request) {
        return Supplement.builder()
                .name(request.getName())
                .nameAz(request.getNameAz())
                .nameRu(request.getNameRu())
                .nameTr(request.getNameTr())
                .description(request.getDescription())
                .descriptionAz(request.getDescriptionAz())
                .descriptionRu(request.getDescriptionRu())
                .descriptionTr(request.getDescriptionTr())
                .iconUrl(request.getIconUrl())
                .isCustom(false)
                .isActive(true)
                .build();
    }

    public SupplementResponse toResponse(Supplement s) {
        if (s == null) return null;
        return SupplementResponse.builder()
                .id(s.getId())
                .name(languageUtil.resolve(
                        s.getName(),
                        s.getNameAz(),
                        s.getNameRu(),
                        s.getNameTr()))
                .description(languageUtil.resolve(
                        s.getDescription(),
                        s.getDescriptionAz(),
                        s.getDescriptionRu(),
                        s.getDescriptionTr()))
                .iconUrl(s.getIconUrl())
                .isCustom(s.getIsCustom())
                .build();
    }

    public UserSupplementResponse toUserSupplementResponse(UserSupplement us) {
        if (us == null) return null;
        return UserSupplementResponse.builder()
                .id(us.getId())
                .supplement(toResponse(us.getSupplement()))
                .timing(us.getTiming())
                .reminderTime(us.getReminderTime())
                .isActive(us.getIsActive())
                .createdAt(us.getCreatedAt())
                .build();
    }

    public SupplementLogResponse toLogResponse(SupplementLog log) {
        if (log == null) return null;
        return SupplementLogResponse.builder()
                .id(log.getId())
                .userSupplement(toUserSupplementResponse(log.getUserSupplement()))
                .status(log.getStatus())
                .date(log.getDate())
                .loggedAt(log.getLoggedAt())
                .build();
    }

    public DailySupplementChecklistResponse toChecklist(
            List<UserSupplement> userSupplements,
            List<SupplementLog> logs,
            LocalDate date) {

        LocalDate today = LocalDate.now();
        boolean isPast   = date.isBefore(today);
        boolean isFuture = date.isAfter(today);

        Map<Long, SupplementLog> logMap = logs.stream()
                .collect(Collectors.toMap(
                        l -> l.getUserSupplement().getId(),
                        l -> l,
                        (a, b) -> a
                ));

        List<SupplementChecklistItem> items = userSupplements.stream().map(us -> {
            SupplementLog log = logMap.get(us.getId());

            SupplementStatus status;
            if (log != null) {
                status = log.getStatus();
            } else if (isPast) {
                status = SupplementStatus.SKIPPED;
            } else if (isFuture) {
                status = SupplementStatus.PENDING;
            } else {
                status = SupplementStatus.PENDING;
            }

            return SupplementChecklistItem.builder()
                    .userSupplementId(us.getId())
                    .supplementLogId(log != null ? log.getId() : null)
                    .supplementName(languageUtil.resolve(
                            us.getSupplement().getName(),
                            us.getSupplement().getNameAz(),
                            us.getSupplement().getNameRu(),
                            us.getSupplement().getNameTr()))
                    .isCustom(us.getSupplement().getIsCustom())
                    .timing(us.getTiming())
                    .reminderTime(us.getReminderTime())
                    .status(status)
                    .build();
        }).collect(Collectors.toList());

        long taken     = items.stream().filter(i -> i.getStatus() == SupplementStatus.TAKEN).count();
        long skipped   = items.stream().filter(i -> i.getStatus() == SupplementStatus.SKIPPED).count();
        long postponed = items.stream().filter(i -> i.getStatus() == SupplementStatus.POSTPONED).count();
        long pending   = items.stream().filter(i -> i.getStatus() == SupplementStatus.PENDING).count();

        return DailySupplementChecklistResponse.builder()
                .date(date)
                .items(items)
                .total(items.size())
                .taken((int) taken)
                .skipped((int) skipped)
                .postponed((int) postponed)
                .pending((int) pending)
                .build();
    }
}