package in.koreatech.batch.domain.bus.processor;

import static in.koreatech.batch.domain.bus.dto.CityBusRouteInfoApiResponse.CityBusRouteInfo;
import static in.koreatech.batch.domain.bus.model.CityBusTimetable.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import in.koreatech.batch.domain.bus.exception.CityBusRouteApiException;
import in.koreatech.batch.domain.bus.model.CityBusTimetable;
import in.koreatech.batch.integration.config.BusProperties;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CityBusTimetableProcessor implements ItemProcessor<CityBusRouteInfo, CityBusTimetable> {

    private static final List<String> DAY_OF_WEEKS = List.of("평일", "주말", "공휴일", "임시");
    private static final String TIME_TABLE_SELECTOR_FORMAT = "body > div.timeTalbeWrap > div > div.timeTable-wrap > div:nth-child(%d) > dl > dd";

    private final BusProperties busProperties;

    @Override
    public CityBusTimetable process(CityBusRouteInfo item) {
        Document document = fetchDocument(item);
        List<BusTimetable> timetables = parseTimetables(document);

        return CityBusTimetable.builder()
            .routeId(item.routeId().toString())
            .updatedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
            .busInfo(
                BusInfo.builder()
                    .number(Long.parseLong(item.routeName()))
                    .depart(item.stName())
                    .arrival(item.edName())
                    .build()
            )
            .busTimetables(timetables)
            .build();
    }

    private Document fetchDocument(CityBusRouteInfo item) {
        try {
            return Jsoup.connect(busProperties.url().timetable())
                .data("routeName", item.routeName())
                .data("routeDirection", item.routeDirection())
                .data("relayAreacode", item.relayAreaCode())
                .data("routeExplain", item.routeExplain())
                .data("stName", item.stName())
                .data("edName", item.edName())
                .ignoreContentType(true)
                .timeout(5000)
                .get();
        } catch (IOException e) {
            throw CityBusRouteApiException.withDetail("버스 시간표 요청 중 오류가 발생했습니다.");
        }
    }

    private List<BusTimetable> parseTimetables(Document document) {
        List<BusTimetable> result = new ArrayList<>();

        for (int i = 0; i < DAY_OF_WEEKS.size(); i++) {
            String selector = String.format(TIME_TABLE_SELECTOR_FORMAT, i + 1);
            Elements departInfoElem = document.select(selector);

            List<String> departInfo = departInfoElem.stream()
                .map(element -> element.text().substring(0, 5))
                .toList();

            result.add(BusTimetable.builder()
                .dayOfWeek(DAY_OF_WEEKS.get(i))
                .departInfo(departInfo)
                .build()
            );
        }

        return result;
    }
}
