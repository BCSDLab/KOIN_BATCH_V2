package in.koreatech.batch.domain.dining.parser;

import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import in.koreatech.batch.domain.dining.model.CrawledDiningMenu;

@Component
public class DiningCrawlingParser {

    private final ObjectMapper objectMapper;
    private final String thousandWonDiningImageUrl;

    public DiningCrawlingParser(
        @Value("${image.url.thousand-won-dining}") String thousandWonDiningImageUrl
    ) {
        this.objectMapper = new ObjectMapper();
        this.thousandWonDiningImageUrl = thousandWonDiningImageUrl;
    }

    public CrawledDiningMenu parse(String responseXml) {
        Document doc = Jsoup.parse(responseXml, "", org.jsoup.parser.Parser.xmlParser());

        Elements rows = doc.select("Row");
        if (rows.isEmpty()) return null;

        Element row = rows.get(0);
        if (row == null) return null;

        ObjectNode menuJson = objectMapper.createObjectNode();
        for (Element col : row.select("Col")) {
            String key = col.attr("id");
            if (key.startsWith("menu")) {
                menuJson.put(key, col.text());
            }
        }

        String menuDump = menuJson.toPrettyString();

        // 천원의 아침 이미지 확인
        String imageUrl = null;
        if (menuDump.contains("천원의아침") || menuDump.contains("천원의 아침")) {
            imageUrl = thousandWonDiningImageUrl;
        }

        return new CrawledDiningMenu(
            Objects.requireNonNull(row.selectFirst("Col[id=EAT_DATE]")).text(),
            Objects.requireNonNull(row.selectFirst("Col[id=EAT_TYPE]")).text(),
            Objects.requireNonNull(row.selectFirst("Col[id=RESTURANT]")).text(),
            Objects.requireNonNull(row.selectFirst("Col[id=PRICE_CARD]")).text(),
            Objects.requireNonNull(row.selectFirst("Col[id=PRICE_CASH]")).text(),
            Objects.requireNonNull(row.selectFirst("Col[id=KCAL]")).text(),
            menuDump,
            imageUrl
        );
    }
}
