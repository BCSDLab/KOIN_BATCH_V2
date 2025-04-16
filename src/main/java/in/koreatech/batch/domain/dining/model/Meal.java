package in.koreatech.batch.domain.dining.model;

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

    public static Meal now() {
        LocalTime now = LocalTime.now();
        for (Meal meal : values()) {
            if (!now.isBefore(meal.startTime) && !now.isAfter(meal.endTime)) {
                return meal;
            }
        }
        return null;
    }

    public static List<Meal> remainingMeals() {
        List<Meal> meals = new ArrayList<>();
        LocalTime now = LocalTime.now();
        for (Meal meal : values()) {
            if (now.isBefore(meal.startTime)) {
                meals.add(meal);
            }
        }
        return meals;
    }
}
