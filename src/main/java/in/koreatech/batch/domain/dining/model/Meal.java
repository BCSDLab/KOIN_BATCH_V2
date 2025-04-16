package in.koreatech.batch.domain.dining.model;

import java.time.Clock;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public enum Meal {
    BREAKFAST(
        LocalTime.of(8, 0, 0, 0),
        LocalTime.of(9, 30, 0, 0)
    ),
    LUNCH(
        LocalTime.of(11, 30, 0, 0),
        LocalTime.of(13, 30, 0, 0)
    ),
    DINNER(LocalTime.of(17, 30, 0, 0),
        LocalTime.of(18, 30, 0, 0)
    ),
    ;

    private final LocalTime startTime;
    private final LocalTime endTime;

    Meal(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 현재 시각 기준으로 해당하는 식사 시간을 반환합니다.
     *
     * @param clock 한국 시간대(Asia/Seoul)가 설정된 Clock 인스턴스
     * @return 현재 시각이 속하는 Meal, 해당하지 않으면 null
     */
    public static Meal now(Clock clock) {
        LocalTime now = LocalTime.now(clock);
        for (Meal meal : values()) {
            if (!now.isBefore(meal.startTime) && !now.isAfter(meal.endTime)) {
                return meal;
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
    public static List<Meal> remainingMeals(Clock clock) {
        List<Meal> meals = new ArrayList<>();
        LocalTime now = LocalTime.now(clock);
        for (Meal meal : values()) {
            if (now.isBefore(meal.startTime)) {
                meals.add(meal);
            }
        }
        return meals;
    }
}
