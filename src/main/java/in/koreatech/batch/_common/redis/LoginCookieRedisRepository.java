package in.koreatech.batch._common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginCookieRedisRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String COOKIE_KEY = "__KSMSID__";
    private static final Duration COOKIE_TTL = Duration.ofHours(1);

    public Optional<String> getLoginCookie() {
        return Optional.ofNullable(redisTemplate.opsForValue().get(COOKIE_KEY));
    }

    public void setLoginCookie(String cookieValue) {
        redisTemplate.opsForValue().set(COOKIE_KEY, cookieValue, COOKIE_TTL);
    }

    public void clearLoginCookie() {
        redisTemplate.delete(COOKIE_KEY);
    }
}
