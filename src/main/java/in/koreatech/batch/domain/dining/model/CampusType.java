package in.koreatech.batch.domain.dining.model;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CampusType {
    CAMPUS1("1캠퍼스", List.of(
        RestaurantType.KOREAN,
        RestaurantType.ILPOOM,
        RestaurantType.SPECIAL,
        RestaurantType.NEUNGSUGWAN
    )),
    CAMPUS2("2캠퍼스", List.of(
        RestaurantType.CORNER1
    ));

    private final String displayName;
    private final List<RestaurantType> restaurants;
}
