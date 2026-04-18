package calixy.mapper;

import calixy.domain.entity.*;
import calixy.model.dto.response.*;
import calixy.model.enums.SupplementStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SupplementMapper {

    public SupplementResponse toResponse(Supplement s) {
        if (s == null) return null;
        return SupplementResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .nameAz(s.getNameAz())
                .description(s.getDescription())
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

        Map<Long, SupplementLog> logMap = logs.stream()
                .collect(Collectors.toMap(
                        l -> l.getUserSupplement().getId(),
                        l -> l,
                        (a, b) -> a
                ));

        List<SupplementChecklistItem> items = userSupplements.stream().map(us -> {
            SupplementLog log = logMap.get(us.getId());
            return SupplementChecklistItem.builder()
                    .userSupplementId(us.getId())
                    .supplementLogId(log != null ? log.getId() : null)
                    .supplementName(us.getSupplement().getName())
                    .supplementNameAz(us.getSupplement().getNameAz())
                    .isCustom(us.getSupplement().getIsCustom())
                    .timing(us.getTiming())
                    .reminderTime(us.getReminderTime())
                    .status(log != null ? log.getStatus() : null)
                    .build();
        }).collect(Collectors.toList());

        long taken   = items.stream().filter(i -> i.getStatus() == SupplementStatus.TAKEN).count();
        long skipped = items.stream().filter(i -> i.getStatus() == SupplementStatus.SKIPPED).count();
        long pending = items.stream().filter(i -> i.getStatus() == null).count();

        return DailySupplementChecklistResponse.builder()
                .date(date)
                .items(items)
                .total(items.size())
                .taken((int) taken)
                .skipped((int) skipped)
                .pending((int) pending)
                .build();
    }
}