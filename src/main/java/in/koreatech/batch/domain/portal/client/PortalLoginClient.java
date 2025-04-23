package in.koreatech.batch.domain.portal.client;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Component;

import in.koreatech.batch.domain.portal.exception.PortalLoginException;
import in.koreatech.batch.domain.portal.repository.PortalLoginCookieRepository;
import in.koreatech.batch.integration.config.PortalProperties;
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
public class PortalLoginClient {

    private final PortalLoginCookieRepository portalLoginCookieRepository;
    private final PortalProperties portalProperties;
    private final CookieManager cookieManager;
    private final OkHttpClient okHttpClient;

    public String getOrRefreshLoginCookie() {
        return portalLoginCookieRepository.getLoginCookie()
            .orElseGet(() -> {
                String loginCookie;
                try {
                    loginCookie = getLoginCookie();
                } catch (IOException e) {
                    throw PortalLoginException.withDetail("아우누리 로그인 과정에서 문제가 발생했습니다.");
                }
                portalLoginCookieRepository.saveLoginCookie(loginCookie);
                return loginCookie;
            });
    }

    private String getLoginCookie() throws IOException {
        Headers headers = new Headers.Builder()
            .add("X-Forwarded-For", portalProperties.ip())
            .add("X-Real-IP", portalProperties.ip())
            .build();

        okHttpClient.newCall(new Request.Builder()
            .url(portalProperties.url().checkLoginId())
            .headers(headers)
            .post(
                new FormBody.Builder()
                    .add("login_id", portalProperties.id())
                    .add("login_pwd", portalProperties.pw())
                    .build()
            )
            .build()
        ).execute().close();

        cookieManager.getCookieStore().add(
            URI.create(portalProperties.url().home()),
            new HttpCookie("kut_login_type", "id")
        );

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

        List<HttpCookie> cookies = cookieManager.getCookieStore().get(URI.create(portalProperties.url().home()));
        return cookies.stream()
            .filter(cookie -> portalProperties.cookie().equals(cookie.getName()))
            .map(HttpCookie::getValue)
            .findFirst()
            .orElseThrow(() -> PortalLoginException.withDetail("아우누리 로그인 과정에서 문제가 발생했습니다."));
    }
}
