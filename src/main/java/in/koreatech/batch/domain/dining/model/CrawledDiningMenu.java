package in.koreatech.batch.domain.dining.model;

public record CrawledDiningMenu(
    String date,
    String diningTime,
    String place,
    Integer priceCard,
    Integer priceCash,
    Integer kcal,
    String menuJson
) {

}
