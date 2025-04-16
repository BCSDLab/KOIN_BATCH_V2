package in.koreatech.batch._common.util;

import static java.net.CookiePolicy.ACCEPT_ALL;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import in.koreatech.batch._common.auth.exception.CookieNotFoundException;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Component
public class PortalLoginUtil {

    @Value("${portal.cookie.login}")
    private String loginCookieName;

    @Value("${portal.id}")
    private String loginId;

    @Value("${portal.pw}")
    private String loginPw;

    @Value("${portal.ip}")
    private String ip;

    @Value("${portal.url.check-first-login}")
    private String checkFirstLoginUrl;

    @Value("${portal.home}")
    private String home;

    @Value("${portal.url.check-second-login}")
    private String checkSecondLoginUrl;

    @Value("${portal.url.sso}")
    private String ssoUrl;

    @Value("${portal.url.sso-login}")
    private String ssoLoginUrl;

    private final OkHttpClient okHttpClient;
    private final CookieManager cookieManager;

    public PortalLoginUtil() {
        this.cookieManager = new CookieManager(null, ACCEPT_ALL);
        this.okHttpClient = new OkHttpClient.Builder()
            .cookieJar(new JavaNetCookieJar(cookieManager))
            .build();
    }

    public String getLoginCookie() throws IOException {
        Headers headers = new Headers.Builder()
            .add("X-Forwarded-For", ip)
            .add("X-Real-IP", ip)
            .build();

        RequestBody loginBody = new FormBody.Builder()
            .add("login_id", loginId)
            .add("login_pwd", loginPw)
            .build();

        okHttpClient.newCall(new Request.Builder()
            .url(checkFirstLoginUrl)
            .headers(headers)
            .post(loginBody)
            .build()
        ).execute().close();

        cookieManager.getCookieStore().add(URI.create(home), new HttpCookie("kut_login_type", "id"));

        RequestBody secondAuth = new FormBody.Builder()
            .add("login_id", loginId)
            .build();

        okHttpClient.newCall(new Request.Builder()
            .url(checkSecondLoginUrl)
            .headers(headers)
            .post(secondAuth)
            .build()
        ).execute().close();

        okHttpClient.newCall(new Request.Builder()
            .url(ssoUrl)
            .headers(headers)
            .post(RequestBody.create("", null))
            .build()
        ).execute().close();

        okHttpClient.newCall(new Request.Builder()
            .url(ssoLoginUrl)
            .headers(headers)
            .get()
            .build()
        ).execute().close();

        List<HttpCookie> cookies = cookieManager.getCookieStore().get(URI.create(home));
        return cookies.stream()
            .filter(cookie -> loginCookieName.equals(cookie.getName()))
            .map(HttpCookie::getValue)
            .findFirst()
            .orElseThrow(() -> CookieNotFoundException.withDetail("로그인 쿠키를 찾을 수 없습니다."));
    }
}
