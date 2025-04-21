package in.koreatech.batch.domain.dining.model;

public record CrawledDiningMenu(
    String date,
    String diningTime,
    String place,
    String priceCard,
    String priceCash,
    String kcal,
    String menuJson
) {

}
