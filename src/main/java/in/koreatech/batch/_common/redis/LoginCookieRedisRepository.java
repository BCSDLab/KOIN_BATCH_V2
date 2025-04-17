package in.koreatech.batch._common.redis;

import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginCookieRedisRepository {

    private static final String COOKIE_KEY = "__KSMSID__";

    private final StringRedisTemplate redisTemplate;

    public Optional<String> getLoginCookie() {
        return Optional.ofNullable(redisTemplate.opsForValue().get(COOKIE_KEY));
    }

    public void setLoginCookie(String cookieValue) {
        redisTemplate.opsForValue().set(COOKIE_KEY, cookieValue);
    }
}
