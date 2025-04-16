package in.koreatech.batch.domain.dining.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.koreatech.batch.domain.dining.model.DiningMenu;
import in.koreatech.batch.domain.dining.model.Meal;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DiningResponseParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${image.url.1000-won-dining}")
    private String thousandWonMealImageUrl;

    public DiningMenu parse(Response response) {
        try (InputStream stream = response.body().byteStream()) {
            Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(stream);

            NodeList rowNodes = document.getElementsByTagName("Row");

            List<DiningMenu> parsedMenus = new ArrayList<>();

            for (int i = 0; i < rowNodes.getLength(); i++) {
                Element row = (Element) rowNodes.item(i);
                DiningMenu entity = parseRow(row);
                if (entity != null) {
                    parsedMenus.add(entity);
                }
            }

            if (parsedMenus.isEmpty()) return null;

            DiningMenu menu = parsedMenus.get(0);
            if (menu.getMenu().contains("천원의아침") || menu.getMenu().contains("천원의 아침")) {
                menu.setImageUrl(thousandWonMealImageUrl);
            }

            return menu;

        } catch (Exception e) {
            throw new RuntimeException("응답 파싱 실패", e);
        }
    }

    private DiningMenu parseRow(Element row) {
        try {
            String dateStr = getColValue(row, "EAT_DATE");
            String typeStr = getColValue(row, "EAT_TYPE");
            String place = getColValue(row, "RESTURANT");
            String campus = getColValue(row, "CAMPUS");
            String dishText = getColValue(row, "DISH");
            String kcalStr = getColValue(row, "KCAL");
            String priceText = getColValue(row, "PRICE");

            // 가격 파싱
            String[] prices = priceText.replaceAll(",", "").replaceAll("[^0-9 ]", "").split(" ");
            Integer priceCard = prices.length > 0 ? parseIntSafe(prices[0]) : null;
            Integer priceCash = prices.length > 1 ? parseIntSafe(prices[1]) : priceCard;

            // 메뉴 파싱
            List<String> menuList = parseMenuText(dishText);
            String menuJson = objectMapper.writeValueAsString(menuList);

            // kcal
            Integer kcal = parseIntSafe(kcalStr.replaceAll("[^0-9]", ""));

            // Campus2일 경우 place override
            if ("Campus2".equals(campus)) {
                place = "2캠퍼스";
            }

            return DiningMenu.builder()
                .date(LocalDate.parse(dateStr))
                .type(Meal.valueOf(typeStr.toUpperCase()))
                .place(place)
                .priceCard(priceCard)
                .priceCash(priceCash)
                .kcal(kcal)
                .menu(menuJson)
                .build();

        } catch (Exception e) {
            return null; // 파싱 실패한 Row는 건너뜀
        }
    }

    private String getColValue(Element row, String id) {
        NodeList cols = row.getElementsByTagName("Col");
        for (int i = 0; i < cols.getLength(); i++) {
            Element col = (Element) cols.item(i);
            if (id.equals(col.getAttribute("id"))) {
                return col.getTextContent().trim();
            }
        }
        return "";
    }

    private List<String> parseMenuText(String text) {
        List<String> result = new ArrayList<>();
        for (String line : text.split("\n")) {
            String cleaned = line
                .replaceAll("\\d+ kcal.*", "")
                .replaceAll("\\d+ 원.*", "")
                .replaceAll("[\\t\\r]", "")
                .trim();
            if (!cleaned.isEmpty()) {
                result.add(cleaned);
            }
        }
        return result;
    }

    private Integer parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
