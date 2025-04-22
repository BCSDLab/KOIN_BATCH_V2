package in.koreatech.batch.domain.dining.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * name : HTTP 요청 파라미터에서 사용
 * place : DB에 저장할 때 사용
 */

@Getter
@RequiredArgsConstructor
public enum RestaurantType {
    KOREAN("한식", "A코너"),
    ILPOOM("일품", "B코너"),
    SPECIAL("특식-전골/뚝배기", "C코너"),
    NEUNGSUGWAN("능수관", "능수관"),
    CORNER1("코너1", "코너1"),
    ;

    private final String name;
    private final String place;
}
