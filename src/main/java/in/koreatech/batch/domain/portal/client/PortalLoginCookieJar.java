package in.koreatech.batch.domain.portal.client;

import java.net.CookieHandler;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.JavaNetCookieJar;

@Component
public class PortalLoginCookieJar implements CookieJar {

    private static final String TARGET_URL_PATTERN = "https://kut90.koreatech.ac.kr/nexacroController.do";

    private final CookieJar javaNetCookieJar;

    public PortalLoginCookieJar(CookieHandler cookieHandler) {
        this.javaNetCookieJar = new JavaNetCookieJar(cookieHandler);
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
        if (url.toString().startsWith(TARGET_URL_PATTERN)) {
            return;
        }

        javaNetCookieJar.saveFromResponse(url, cookies);
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
        return javaNetCookieJar.loadForRequest(url);
    }
}
