package in.koreatech.batch.domain.dining.reader;

import static in.koreatech.batch.domain.dining.model.CampusType.CAMPUS1;
import static in.koreatech.batch.domain.dining.model.CampusType.CAMPUS2;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import in.koreatech.batch.domain.dining.client.DiningCrawlerClient;
import in.koreatech.batch.domain.dining.model.CampusType;
import in.koreatech.batch.domain.dining.model.CrawledDiningMenu;
import in.koreatech.batch.domain.dining.model.DiningType;
import in.koreatech.batch.domain.dining.model.RestaurantType;
import in.koreatech.batch.domain.dining.parser.DiningCrawlingParser;

@Component
public class TodayDiningCrawlerReader implements ItemReader<CrawledDiningMenu> {

    private final Iterator<CrawledDiningMenu> crawledData;
    private final DiningCrawlerClient client;
    private final Clock clock;

    // TODO. remainingDiningType.isEmpty인 경우 크롤링 실행 x
    public TodayDiningCrawlerReader(Clock clock, DiningCrawlerClient client) throws IOException {
        this.clock = clock;
        this.client = client;

        List<DiningType> remainingDiningTypes = DiningType.remainingDiningType(clock);
        List<CrawledDiningMenu> menus = new ArrayList<>();
        for (CampusType campus : List.of(CAMPUS1, CAMPUS2)) {
            menus.addAll(crawlDining(remainingDiningTypes, campus));
        }

        this.crawledData = menus.iterator();
    }

    private List<CrawledDiningMenu> crawlDining(List<DiningType> remainingDiningTypes, CampusType campusType) throws IOException {
        List<CrawledDiningMenu> menus = new ArrayList<>();
        List<RestaurantType> restaurants = campusType.getRestaurants();

        for (DiningType remainingDiningType : remainingDiningTypes) {
            for (RestaurantType restaurantType : restaurants) {
                String xml = client.sendRequest(
                    LocalDate.now(clock),
                    remainingDiningType.name().toLowerCase(),
                    restaurantType.getDisplayName(),
                    campusType.getDisplayName()
                );
                if (xml != null) {
                    menus.add(DiningCrawlingParser.parse(xml));
                }
            }
        }

        return menus;
    }

    @Override
    public CrawledDiningMenu read() {
        return crawledData.hasNext() ? crawledData.next() : null;
    }
}
