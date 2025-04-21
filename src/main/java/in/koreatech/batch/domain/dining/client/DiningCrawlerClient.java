package in.koreatech.batch.domain.dining.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import in.koreatech.batch._common.config.PortalProperties;
import in.koreatech.batch._common.redis.LoginCookieRedisRepository;
import in.koreatech.batch.domain.dining.exception.DiningRequestException;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
@RequiredArgsConstructor
public class DiningCrawlerClient {

    private static final MediaType MEDIA_TYPE_XML = MediaType.parse("text/xml; charset=utf-8");

    private final OkHttpClient client;
    private final PortalProperties portalProperties;
    private final LoginCookieRedisRepository loginCookieRedisRepository;

    public String sendRequest(String eatDate, String eatType, String restaurant, String campus) throws IOException {
        String xmlBody = loadAndFileTemplate(Map.of(
            "EAT_DATE", eatDate,
            "EAT_TYPE", eatType,
            "RESTURANT", restaurant,
            "CAMPUS", campus
        ));

        String cookieHeader = String.format("%s=%s; Domain=koreatech.ac.kr;", portalProperties.cookieLogin(), loginCookieRedisRepository.getLoginCookie());

        Request request = new Request.Builder()
            .url(portalProperties.urlDiningMenu())
            .addHeader("Content-Type", "text/xml; charset=utf-8")
            .addHeader("Cookie", cookieHeader)
            .post(RequestBody.create(xmlBody, MEDIA_TYPE_XML))
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw DiningRequestException.withDetail("식단 요청에 실패했습니다");
            }

            String responseBody = response.body().string();
            Document doc = Jsoup.parse(responseBody, "", org.jsoup.parser.Parser.xmlParser());

            if ("0".equals(doc.selectFirst("Parameter[id=ErrorCode]").text())) {
                return responseBody;
            }

            throw DiningRequestException.withDetail("식단 요청에 실패했습니다");
        }
    }

    private String loadAndFileTemplate(Map<String, String> values) throws IOException {
        ClassPathResource resource = new ClassPathResource("xml/menu_request_template.xml");
        String template = Files.readString(resource.getFile().toPath(), UTF_8);

        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("${" + entry.getKey() + "}", entry.getValue());
        }

        return template;
    }
}
