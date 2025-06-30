package in.koreatech.batch.domain.dining.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import in.koreatech.batch.domain.dining.model.Menu;

public class MenuComparator {

    private MenuComparator() {
    }

    public static List<Menu> checkDuplicationMenu(List<Menu> existedMenus, List<Menu> newMenus) {
        // 1. 복합키 기반으로 기존 메뉴를 Map에 저장
        Map<MenuKey, Menu> existedMap = existedMenus.stream()
                .collect(Collectors.toMap(MenuKey::from, menu -> menu));

        List<Menu> result = new ArrayList<>();

        for (Menu newMenu : newMenus) {
            MenuKey key = MenuKey.from(newMenu);
            Menu oldMenu = existedMap.get(key);

            // 2. 새로운 메뉴인 경우
            if (oldMenu == null) {
                result.add(newMenu);
                continue;
            }

            // 3. 기존과 menu 내용(menu 필드)만 다른 경우
            if (!Objects.equals(oldMenu.menu(), newMenu.menu())) {
                // record는 불변이므로 새로 복사
                Menu updatedMenu = new Menu(
                        newMenu.id(), newMenu.date(), newMenu.type(), newMenu.place(),
                        newMenu.priceCard(), newMenu.priceCash(), newMenu.kcal(),
                        newMenu.menu(), newMenu.imageUrl(), newMenu.soldOut(),
                        LocalDateTime.now(), // isChanged set
                        newMenu.likes()
                );
                result.add(updatedMenu);
            }
        }

        return result;
    }

    // 복합키 비교용 도우미 클래스
    private record MenuKey(LocalDate date, String type, String place) {
        public static MenuKey from(Menu menu) {
            return new MenuKey(menu.date(), menu.type().toUpperCase(), menu.place());
        }
    }
}
