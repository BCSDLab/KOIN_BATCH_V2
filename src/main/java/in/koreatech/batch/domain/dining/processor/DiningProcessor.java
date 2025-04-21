package in.koreatech.batch.domain.dining.processor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import in.koreatech.batch.domain.dining.model.CrawledDiningMenu;
import in.koreatech.batch.domain.dining.model.Dining;
import in.koreatech.batch.domain.dining.model.DiningType;

@Component
public class DiningProcessor implements ItemProcessor<CrawledDiningMenu, Dining> {

    private final String thousandWonDiningImageUrl;

    public DiningProcessor(
        @Value("${image.url.thousand-won-dining}") String thousandWonDiningImageUrl
    ) {
        this.thousandWonDiningImageUrl = thousandWonDiningImageUrl;
    }

    @Override
    public Dining process(CrawledDiningMenu item) {
        return Dining.builder()
            .date(LocalDate.parse(item.date(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .type(DiningType.valueOf(item.diningTime().toUpperCase()))
            .place(item.place().toUpperCase())
            .priceCard(Integer.parseInt(item.priceCard()))
            .priceCash(Integer.parseInt(item.priceCash()))
            .kcal(Integer.parseInt(item.kcal()))
            .menu(item.menuJson())
            .imageUrl(resolveImageUrlFromMenu(item.menuJson()))
            .build();
    }

    private String resolveImageUrlFromMenu(String menuJson) {
        if (menuJson.contains("천원의아침") || menuJson.contains("천원의 아침")) {
            return thousandWonDiningImageUrl;
        }
        return null;
    }
}
