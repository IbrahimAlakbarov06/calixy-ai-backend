package calixy.util;

import calixy.domain.entity.User;
import calixy.domain.repo.UserProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@RequiredArgsConstructor
public class LanguageUtil {

    private final UserProfileRepository userProfileRepository;

    public String getLang() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attrs.getRequest();
            String rawHeader = request.getHeader("Accept-Language");

            log.info(">>> Accept-Language header: '{}'", rawHeader); // ← əlavə et

            if (rawHeader != null && !rawHeader.isBlank()) {
                String lang = rawHeader.split("[,;]")[0].trim().toLowerCase();
                log.info(">>> Parsed lang: '{}'", lang); // ← əlavə et
                if (lang.equals("az") || lang.equals("ru") ||
                        lang.equals("tr") || lang.equals("en")) {
                    return lang;
                }
            }
        } catch (Exception e) {
            log.error(">>> Header read error: {}", e.getMessage());
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User user) {
                String dbLang = userProfileRepository.findByUserId(user.getId())
                        .map(p -> p.getLanguage() != null ? p.getLanguage() : "en")
                        .orElse("en");
                log.info(">>> DB lang: '{}'", dbLang); // ← əlavə et
                return dbLang;
            }
        } catch (Exception e) {
            log.error(">>> DB lang error: {}", e.getMessage());
        }

        return "en";
    }

    public String resolve(String nameEn, String nameAz, String nameRu, String nameTr) {
        String lang = getLang();
        return switch (lang) {
            case "az" -> nameAz != null && !nameAz.isBlank() ? nameAz : nameEn;
            case "ru" -> nameRu != null && !nameRu.isBlank() ? nameRu : nameEn;
            case "tr" -> nameTr != null && !nameTr.isBlank() ? nameTr : nameEn;
            default   -> nameEn != null ? nameEn : "";
        };
    }
}