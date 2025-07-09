package in.koreatech.batch.domain.dining.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.koreatech.batch.domain.dining.model.Menu;
import in.koreatech.batch.domain.dining.model.Restaurant;

public class MenuParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern KCAL_WON_PATTERN = Pattern.compile("\\d+\\s*kcal\\s+[^\\s]+\\s*원");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d+");

    public static Menu parse(String xmlResponse) {
        Document doc = Jsoup.parse(xmlResponse, "", Parser.xmlParser());
        Elements rows = doc.select("Row");

        if (rows.isEmpty()) {
            return null;
        }

        Element firstRow = rows.get(0);
        Map<String, String> row = parseRow(firstRow);

        if (row == null || row.isEmpty()) {
            return null;
        }

        // 필수 필드 추출
        LocalDate date = parseLocalDate(row.get("EAT_DATE")); // e.g., "2025-06-29"
        String type = row.get("EAT_TYPE");
        String place = Restaurant.parseDBName(row.get("RESTURANT"));
        PriceResult priceResult = parseCash(row.get("PRICE"));
        Integer priceCard = priceResult.priceCard;
        Integer priceCash = priceResult.priceCash;
        Integer kcal = parseInt(row.get("KCAL"));
        List<String> menuItems = parseMenu(row.get("DISH"));

        String menuJson;
        try {
            menuJson = objectMapper.writeValueAsString(menuItems);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("메뉴 JSON 직렬화 실패", e);
        }

        // 천원의아침 이미지 조건
        String imageUrl = null;
        if (menuJson.contains("천원의아침")) {
            imageUrl = "https://team-kap-koin-storage.s3.ap-northeast-2.amazonaws.com/dining/%EC%B2%9C%EC%9B%90%EC%9D%98%EC%95%84%EC%B9%A8.png";
        }

        return new Menu(
                null,               // id → 없음
                date,
                type,
                place,
                priceCard,
                priceCash,
                kcal,
                menuJson,
                imageUrl,
                null,               // soldOut → 없음
                null,               // isChanged → 없음
                null                // likes → 없음
        );
    }

    private static Map<String, String> parseRow(Element row) {
        Map<String, String> result = new HashMap<>();
        for (Element col : row.select("Col")) {
            result.put(col.attr("id"), col.text());
        }
        return result;
    }

    private static LocalDate parseLocalDate(String value) {
        try {
            return value == null ? null : LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static Integer parseInt(String value) {
        try {
            return value == null ? null : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static List<String> parseMenu(String menu) {
        if (menu == null || menu.isBlank()) return List.of();

        String cleaned = KCAL_WON_PATTERN.matcher(menu).replaceAll("").trim();

        return Arrays.stream(cleaned.split("\\s+"))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .collect(Collectors.toList());
    }

    private static PriceResult parseCash(String priceText) {
        try {
            if (priceText == null || priceText.isBlank()) {
                return new PriceResult(null, null);
            }

            String cleaned = priceText.replace(",", "").replaceAll("\\s+", "");
            Matcher matcher = DIGIT_PATTERN.matcher(cleaned);

            Integer first = null;
            Integer second = null;

            if (matcher.find()) {
                first = Integer.parseInt(matcher.group());
            }
            if (matcher.find()) {
                second = Integer.parseInt(matcher.group());
            }

            if (first != null && second != null) {
                return new PriceResult(first, second);
            } else if (first != null) {
                return new PriceResult(first, first);
            } else {
                return new PriceResult(null, null);
            }

        } catch (Exception e) {
            return new PriceResult(null, null);
        }
    }

    public static class PriceResult {
        public final Integer priceCard;
        public final Integer priceCash;

        public PriceResult(Integer priceCard, Integer priceCash) {
            this.priceCard = priceCard;
            this.priceCash = priceCash;
        }
    }
}
