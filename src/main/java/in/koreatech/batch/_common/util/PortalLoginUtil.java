package in.koreatech.batch._common.util;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Component;

import in.koreatech.batch._common.auth.exception.CookieNotFoundException;
import in.koreatech.batch._common.config.PortalProperties;
import in.koreatech.batch._common.redis.LoginCookieRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortalLoginUtil {

    private final LoginCookieRedisRepository loginCookieRedisRepository;
    private final CookieManager cookieManager;
    private final OkHttpClient okHttpClient;
    private final PortalProperties portalProperties;

    public String getOrRefreshLoginCookie() {
        return loginCookieRedisRepository.getLoginCookie()
            .orElseGet(() -> {
                String newCookie = null;
                try {
                    newCookie = getLoginCookie();
                } catch (IOException e) {
                    log.warn("포털 로그인에 실패했습니다.");
                }
                loginCookieRedisRepository.setLoginCookie(newCookie);
                return newCookie;
            });
    }

    private String getLoginCookie() throws IOException {
        Headers headers = new Headers.Builder()
            .add("X-Forwarded-For", portalProperties.ip())
            .add("X-Real-IP", portalProperties.ip())
            .build();

        okHttpClient.newCall(new Request.Builder()
            .url(portalProperties.url().checkFirstLogin())
            .headers(headers)
            .post(
                new FormBody.Builder()
                .add("login_id", portalProperties.id())
                .add("login_pwd", portalProperties.pw())
                .build()
            )
            .build()
        ).execute().close();

        cookieManager.getCookieStore().add(URI.create(portalProperties.home()), new HttpCookie("kut_login_type", "id"));

        okHttpClient.newCall(new Request.Builder()
            .url(portalProperties.url().checkSecondLogin())
            .headers(headers)
            .post(
                new FormBody.Builder()
                    .add("login_id", portalProperties.id())
                    .build()
            )
            .build()
        ).execute().close();

        okHttpClient.newCall(new Request.Builder()
            .url(portalProperties.url().sso())
            .headers(headers)
            .post(RequestBody.create("", null))
            .build()
        ).execute().close();

        okHttpClient.newCall(new Request.Builder()
            .url(portalProperties.url().ssoLogin())
            .headers(headers)
            .get()
            .build()
        ).execute().close();

        List<HttpCookie> cookies = cookieManager.getCookieStore().get(URI.create(portalProperties.home()));
        return cookies.stream()
            .filter(cookie -> portalProperties.cookie().login().equals(cookie.getName()))
            .map(HttpCookie::getValue)
            .findFirst()
            .orElseThrow(() -> CookieNotFoundException.withDetail("로그인 쿠키를 찾을 수 없습니다."));
    }
}
