package in.koreatech.batch.domain.dining.reader;

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

    private final DiningCrawlerClient client;
    private final Clock clock;
    private Iterator<CrawledDiningMenu> crawledData = null;

    // TODO. remainingDiningType.isEmpty인 경우 크롤링 실행 x
    public TodayDiningCrawlerReader(Clock clock, DiningCrawlerClient client) throws IOException {
        this.clock = clock;
        this.client = client;
    }

    @Override
    public CrawledDiningMenu read() throws Exception {
        if (crawledData == null) {
            List<DiningType> remainingDiningTypes = DiningType.remainingDiningType(clock);
            List<CrawledDiningMenu> menus = new ArrayList<>();
            for (CampusType campus : CampusType.values()) {
                menus.addAll(crawlingDining(remainingDiningTypes, campus));
            }
            this.crawledData = menus.iterator();
        }

        return crawledData.hasNext() ? crawledData.next() : null;
    }

    private List<CrawledDiningMenu> crawlingDining(List<DiningType> remainingDiningTypes, CampusType campusType) throws IOException {
        List<CrawledDiningMenu> menus = new ArrayList<>();
        List<RestaurantType> restaurants = campusType.getRestaurants();

        for (DiningType diningType : remainingDiningTypes) {
            for (RestaurantType restaurant : restaurants) {
                String xml = client.sendRequest(
                    LocalDate.now(clock),
                    diningType.name().toLowerCase(),
                    restaurant.getName(),
                    campusType.getDisplayEnglishName()
                );
                if (xml != null) {
                    menus.add(DiningCrawlingParser.parse(xml));
                }
            }
        }

        return menus;
    }
}
