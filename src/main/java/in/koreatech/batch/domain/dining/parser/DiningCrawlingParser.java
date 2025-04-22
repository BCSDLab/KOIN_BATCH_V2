package in.koreatech.batch.domain.dining.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import in.koreatech.batch.domain.dining.model.CrawledDiningMenu;

public class DiningCrawlingParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static CrawledDiningMenu parse(String responseXml) {
        Document document = Jsoup.parse(responseXml, "", org.jsoup.parser.Parser.xmlParser());

        Elements rows = document.select("Row");
        if (rows.isEmpty()) return null;

        Element row = rows.get(0);

        List<Integer> prices = extractPrice(row);

        return new CrawledDiningMenu(
            Objects.requireNonNull(row.selectFirst("Col[id=EAT_DATE]")).text(),
            Objects.requireNonNull(row.selectFirst("Col[id=EAT_TYPE]")).text(),
            Objects.requireNonNull(row.selectFirst("Col[id=RESTURANT]")).text(),
            Objects.requireNonNullElseGet(prices.get(0), null),
            Objects.requireNonNullElseGet(prices.get(1), null),
            Objects.requireNonNullElseGet(extractKcal(row), null),
            null
        );
    }

    private static Integer extractKcal(Element row) {
        return Integer.parseInt(row.selectFirst("Col[id=KCAL]").text());
    }

    private static List<Integer> extractPrice(Element row) {
        List<Integer> prices = Arrays.stream(row.selectFirst("Col[id=PRICE]").text().split(","))
            .map(Integer::parseInt)
            .toList();

        if (prices.size() >= 2) return prices;
        else if (prices.isEmpty()) return null;
        else return List.of(prices.get(0), prices.get(0));
    }
}
