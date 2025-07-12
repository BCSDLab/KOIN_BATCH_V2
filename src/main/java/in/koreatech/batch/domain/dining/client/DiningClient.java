package in.koreatech.batch.domain.dining.client;

import static in.koreatech.batch.domain.dining.model.Restaurant.SECOND_CAMPUS;
import static in.koreatech.batch.domain.dining.model.Restaurant.values;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import in.koreatech.batch.domain.dining.model.DiningType;
import in.koreatech.batch.domain.dining.model.Menu;
import in.koreatech.batch.domain.dining.model.Restaurant;
import in.koreatech.batch.domain.dining.util.MenuParser;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
@RequiredArgsConstructor
public class DiningClient {

    private static final String REQUEST_BODY_FORMAT = """
        <?xml version="1.0" encoding="UTF-8"?>
        <Root xmlns="http://www.nexacroplatform.com/platform/dataset">
            <Parameters>
                <Parameter id="method">getList_sp</Parameter>
                <Parameter id="sqlid">NK_COT_MEAL_PLAN.NP_SELECT_11</Parameter>
                <Parameter id="locale">ko</Parameter>
            </Parameters>
            <Dataset id="input1">
                <ColumnInfo>
                    <Column id="CAMPUS" type="string" size="4000" />
                    <Column id="RESTURANT" type="string" size="4000" />
                    <Column id="EAT_DATE" type="string" size="4000" />
                    <Column id="EAT_TYPE" type="string" size="4000" />
                </ColumnInfo>
                <Rows>
                    <Row>
                        <Col id="EAT_DATE">%s</Col>
                        <Col id="EAT_TYPE">%s</Col>
                        <Col id="RESTURANT">%s</Col>
                        <Col id="CAMPUS">%s</Col>
                    </Row>
                </Rows>
            </Dataset>
        </Root>""";

    private final OkHttpClient okHttpClient;

    public List<Menu> crawlWeekDiningMenus(String loginToken) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(6);
        List<Menu> menus = new ArrayList<>();
        for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
            for (DiningType diningType : DiningType.values()) {
                for (Restaurant restaurant : values()) {
                    try {
                        Menu menu = crawlDiningMenu(loginToken, date, diningType, restaurant, "Campus1");
                        if (menu != null) {
                            menus.add(menu);
                        }
                    } catch (RuntimeException e) {
                        System.err.println("식단 크롤링 중 오류 발생: " + e.getMessage());
                    }
                }
                Menu menu = crawlDiningMenu(loginToken, date, diningType, SECOND_CAMPUS, "Campus2");
                if (menu != null) {
                    menus.add(menu);
                }
            }
        }
        return menus;
    }

    public List<Menu> crawlCurrentDiningMenu(String loginToken, LocalDateTime dateTime) {
        LocalDate today = dateTime.toLocalDate();
        DiningType diningType = DiningType.fromTime(dateTime.toLocalTime());
        List<Menu> menus = new ArrayList<>();
        for (Restaurant restaurant : values()) {
            try {
                Menu menu = crawlDiningMenu(loginToken, today, diningType, restaurant, "Campus1");
                if (menu != null) {
                    menus.add(menu);
                }
            } catch (RuntimeException e) {
                System.err.println("현재 메뉴 크롤링 중 오류 발생: " + e.getMessage());
            }
            Menu menu = crawlDiningMenu(loginToken, today, diningType, SECOND_CAMPUS, "Campus2");
            if (menu != null) {
                menus.add(menu);
            }
        }
        return menus;
    }

    private Menu crawlDiningMenu(String loginToken, LocalDate date, DiningType diningType, Restaurant restaurant,
        String campus) {
        String requestBody = String.format(
            REQUEST_BODY_FORMAT,
            date.toString(),//.replaceAll("-", ""), // EAT_DATE
            diningType.name().toLowerCase(),           // EAT_TYPE
            restaurant.getKorean(),     // RESTURANT
            campus          // CAMPUS
        );

        Request request = new Request.Builder()
            .url("https://kut90.koreatech.ac.kr/nexacroController.do")
            .header("Content-Type", "text/xml; charset=utf-8")
            .addHeader("Cookie", "__KSMSID__=" + loginToken + ";Domain=koreatech.ac.kr;")
            .post(RequestBody.create(
                requestBody,
                MediaType.parse("text/xml; charset=utf-8")
            ))
            .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("식단 요청 실패: " + response.code() + " / " + response.message());
            }
            return MenuParser.parse(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException("식단 요청 중 IOException 발생", e);
        }
    }
}
