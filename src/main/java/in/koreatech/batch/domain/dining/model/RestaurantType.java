package in.koreatech.batch.domain.dining.model;

import lombok.Getter;

@Getter
public enum RestaurantType {
    KOREAN("한식"),
    ILPOOM("일품"),
    SPECIAL("특식-전골/뚝배기"),
    NEUNGSUGWAN("능수관");

    private final String displayName;

    RestaurantType(String displayName) {
        this.displayName = displayName;
    }
}
