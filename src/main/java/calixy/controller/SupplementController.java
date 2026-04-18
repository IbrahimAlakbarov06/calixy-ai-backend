package calixy.controller;

import calixy.domain.entity.User;
import calixy.model.dto.request.AddUserSupplementRequest;
import calixy.model.dto.response.*;
import calixy.model.enums.SupplementStatus;
import calixy.service.SupplementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/supplements")
@RequiredArgsConstructor
public class SupplementController {

    private final SupplementService supplementService;

    @GetMapping("/catalog")
    public ResponseEntity<List<SupplementResponse>> getCatalog() {
        return ResponseEntity.ok(supplementService.getCatalog());
    }

    @GetMapping("/my")
    public ResponseEntity<List<UserSupplementResponse>> getMySupplement(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(supplementService.getMySupplement(user));
    }

    @PostMapping("/my")
    public ResponseEntity<UserSupplementResponse> addToMySupplement(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddUserSupplementRequest request) {
        return ResponseEntity.ok(supplementService.addToMySupplement(user, request));
    }

    @DeleteMapping("/my/{id}")
    public ResponseEntity<MessageResponse> removeFromMySupplement(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        supplementService.removeFromMySupplement(user, id);
        return ResponseEntity.ok(new MessageResponse("Supplement removed successfully"));
    }

    @GetMapping("/checklist")
    public ResponseEntity<DailySupplementChecklistResponse> getTodayChecklist(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(supplementService.getChecklist(user, LocalDate.now()));
    }

    @GetMapping("/checklist/{date}")
    public ResponseEntity<DailySupplementChecklistResponse> getChecklistByDate(
            @AuthenticationPrincipal User user,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(supplementService.getChecklist(user, date));
    }

    @PostMapping("/my/{userSupplementId}/log")
    public ResponseEntity<SupplementLogResponse> logSupplement(
            @AuthenticationPrincipal User user,
            @PathVariable Long userSupplementId,
            @RequestParam SupplementStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate logDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(
                supplementService.logSupplement(user, userSupplementId, status, logDate));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<SupplementResponse> createSupplement(
            @RequestParam String name,
            @RequestParam(required = false) String nameAz,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String iconUrl) {
        return ResponseEntity.ok(
                supplementService.createSupplement(name, nameAz, description, iconUrl));
    }
}