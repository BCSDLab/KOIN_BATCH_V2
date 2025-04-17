package in.koreatech.batch.domain.dining.model;

import java.time.Clock;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public enum DiningType {
    BREAKFAST(
        "아침",
        LocalTime.of(8, 0, 0, 0),
        LocalTime.of(9, 30, 0, 0)
    ),
    LUNCH(
        "점심",
        LocalTime.of(11, 30, 0, 0),
        LocalTime.of(13, 30, 0, 0)
    ),
    DINNER(
        "저녁",
        LocalTime.of(17, 30, 0, 0),
        LocalTime.of(18, 30, 0, 0)
    ),
    ;

    private final String diningName;
    private final LocalTime startTime;
    private final LocalTime endTime;

    DiningType(String diningName, LocalTime startTime, LocalTime endTime) {
        this.diningName = diningName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 현재 시각 기준으로 해당하는 식사 시간을 반환합니다.
     *
     * @param clock 한국 시간대(Asia/Seoul)가 설정된 Clock 인스턴스
     * @return 현재 시각이 속하는 Meal, 해당하지 않으면 null
     */
    public static DiningType now(Clock clock) {
        LocalTime now = LocalTime.now(clock);
        for (DiningType diningType : values()) {
            if (!now.isBefore(diningType.startTime) && !now.isAfter(diningType.endTime)) {
                return diningType;
            }
        }
        return null;
    }

    /**
     * 현재 시각 기준으로 남은 식사 시간을 반환합니다.
     *
     * @param clock 한국 시간대(Asia/Seoul)가 설정된 Clock 인스턴스
     * @return 남은 식사 시간을 가진 Meal 리스트
     */
    public static List<DiningType> remainingDiningType(Clock clock) {
        List<DiningType> diningTypes = new ArrayList<>();
        LocalTime now = LocalTime.now(clock);
        for (DiningType diningType : values()) {
            if (now.isBefore(diningType.startTime)) {
                diningTypes.add(diningType);
            }
        }
        return diningTypes;
    }
}
