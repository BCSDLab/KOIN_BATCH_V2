package in.koreatech.batch.domain.portal.repository;

import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import in.koreatech.batch.integration.config.PortalProperties;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PortalLoginCookieRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final PortalProperties portalProperties;

    public Optional<String> getLoginCookie() {
        return Optional.ofNullable(redisTemplate.opsForValue().get(portalProperties.cookie()));
    }

    public void saveLoginCookie(String cookieValue) {
        redisTemplate.opsForValue().set(portalProperties.cookie(), cookieValue);
    }
}
