package in.koreatech.batch.domain.dining.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
@RequiredArgsConstructor
public class DiningMenuRequester {

    private static final MediaType XML = MediaType.parse("text/xml; charset=utf-8");

    private final OkHttpClient okHttpClient;

    @Value("${portal.url.menu}")
    private String menuUrl;

    public DiningMenuRequester() {
        okHttpClient = new OkHttpClient();
    }

    public Response sendRequest(String loginCookie, String eatDate, String eatType, String restaurant, String campus) throws IOException {
        String body = loadRequestBody(eatDate, eatType, restaurant, campus);

        Request request = new Request.Builder()
            .url(menuUrl)
            .addHeader("Content-Type", "text/xml; charset=utf-8")
            .addHeader("Cookie", "__KSMSID__=" + loginCookie + "; Domain=koreatech.ac.kr;")
            .post(RequestBody.create(body, XML))
            .build();

        Response response = okHttpClient.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("HTTP 요청 실패: " + response.code());
        }

        if (isValidResponse(response)) {
            return response;
        } else {
            throw new IllegalStateException("식단 요청 실패: 유효하지 않은 쿠키");
        }
    }

    private String loadRequestBody(String eatDate, String eatType, String restaurant, String campus) {
        try (InputStream is = getClass().getResourceAsStream("/xml/menu_request_template.xml")) {
            if (is == null) {
                throw new IllegalStateException("템플릿 XML을 찾을 수 없습니다.");
            }
            String template = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return template
                .replace("${eatDate}", eatDate)
                .replace("${eatType}", eatType)
                .replace("${restaurant}", restaurant)
                .replace("${campus}", campus);
        } catch (IOException e) {
            throw new UncheckedIOException("XML 템플릿 로딩 실패", e);
        }
    }

    private boolean isValidResponse(Response response) {
        try {
            Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(response.body().byteStream());

            NodeList parameters = document.getElementsByTagName("Parameter");

            for (int i = 0; i < parameters.getLength(); i++) {
                Element param = (Element) parameters.item(i);
                if ("ErrorCode".equals(param.getAttribute("id"))) {
                    return "0".equals(param.getTextContent());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("응답 파싱 실패", e);
        }
        return false;
    }
}
