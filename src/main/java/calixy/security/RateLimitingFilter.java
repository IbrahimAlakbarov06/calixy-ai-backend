package calixy.security;

import io.github.bucket4j.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String ip = getClientIp(req);
        String path = req.getRequestURI();

        Bucket bucket = path.startsWith("/api/auth")
                ? authBuckets.computeIfAbsent(ip, k -> buildAuthBucket())
                : buckets.computeIfAbsent(ip, k -> buildGeneralBucket());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            res.setHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            chain.doFilter(req, res);
        } else {
            long waitSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            res.setHeader("Retry-After", String.valueOf(waitSeconds));
            res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            res.setContentType("application/json");
            res.getWriter().write(
                    "{\"error\":\"Too many requests\",\"retryAfterSeconds\":" + waitSeconds + "}"
            );
        }
    }

    private Bucket buildAuthBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(10,
                        Refill.intervally(10, Duration.ofMinutes(1))))
                .build();
    }

    private Bucket buildGeneralBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(100,
                        Refill.greedy(100, Duration.ofMinutes(1))))
                .build();
    }

    private String getClientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}