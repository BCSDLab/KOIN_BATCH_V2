package in.koreatech.batch._common.config;

import static java.net.CookiePolicy.ACCEPT_ALL;

import java.net.CookieManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

@Configuration
@RequiredArgsConstructor
public class OkHttpClientConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
            .cookieJar(new JavaNetCookieJar(cookieManager()))
            .build();
    }

    @Bean
    public CookieManager cookieManager() {
       return new CookieManager(null, ACCEPT_ALL);
    }
}
