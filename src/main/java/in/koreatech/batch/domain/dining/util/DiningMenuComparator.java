package in.koreatech.batch.domain.dining.util;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import in.koreatech.batch.domain.dining.model.DiningMenu;

public class DiningMenuComparator {

    private static final Clock KST_CLOCK = Clock.system(ZoneId.of("Asia/Seoul"));

    public static List<DiningMenu> detectChanges(List<DiningMenu> existingMenus, List<DiningMenu> newMenus) {
        Map<String, DiningMenu> existingMap = new HashMap<>();
        for (DiningMenu menu : existingMenus) {
            String key = makeKey(menu);
            existingMap.put(key, menu);
        }

        List<DiningMenu> result = new ArrayList<>();

        for (DiningMenu newMenu : newMenus) {
            String key = makeKey(newMenu);
            DiningMenu oldMenu = existingMap.get(key);

            // 새 메뉴 (기존에 없던 조합)
            if (oldMenu == null) {
                result.add(newMenu);
                continue;
            }

            // 기존과 다른 경우
            if (!isSameMenu(oldMenu, newMenu)) {
                if (!Objects.equals(oldMenu.getMenu(), newMenu.getMenu())) {
                    newMenu.setIsChanged(LocalDate.now(KST_CLOCK));
                }
                result.add(newMenu);
            }
        }

        return result;
    }

    private static String makeKey(DiningMenu menu) {
        return menu.getDate() + "|" + menu.getType().name().toUpperCase() + "|" + menu.getPlace();
    }

    private static boolean isSameMenu(DiningMenu m1, DiningMenu m2) {
        return Objects.equals(m1.getDate(), m2.getDate()) &&
               Objects.equals(m1.getType(), m2.getType()) &&
               Objects.equals(m1.getPlace(), m2.getPlace()) &&
               Objects.equals(m1.getPriceCard(), m2.getPriceCard()) &&
               Objects.equals(m1.getPriceCash(), m2.getPriceCash()) &&
               Objects.equals(m1.getKcal(), m2.getKcal()) &&
               Objects.equals(m1.getMenu(), m2.getMenu());
    }
}
