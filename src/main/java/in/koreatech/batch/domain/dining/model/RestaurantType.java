package in.koreatech.batch.domain.dining.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RestaurantType {
    KOREAN("한식"),
    ILPOOM("일품"),
    SPECIAL("특식-전골/뚝배기"),
    NEUNGSUGWAN("능수관"),
    CORNER1("코너1"),
    ;

    private final String displayName;
}
