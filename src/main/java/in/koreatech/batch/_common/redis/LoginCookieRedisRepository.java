package in.koreatech.batch._common.redis;

import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import in.koreatech.batch._common.config.PortalProperties;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginCookieRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private final PortalProperties portalProperties;

    public Optional<String> getLoginCookie() {
        return Optional.ofNullable(redisTemplate.opsForValue().get(portalProperties.cookieLogin()));
    }

    public void setLoginCookie(String cookieValue) {
        redisTemplate.opsForValue().set(portalProperties.cookieLogin(), cookieValue);
    }
}
