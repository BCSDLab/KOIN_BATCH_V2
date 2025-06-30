package in.koreatech.batch.domain.dining.model;

import java.time.LocalTime;

public enum DiningType {

    BREAKFAST(LocalTime.of(8, 0), LocalTime.of(9, 30)),
    LUNCH(LocalTime.of(11, 30), LocalTime.of(13, 30)),
    DINNER(LocalTime.of(17, 30), LocalTime.of(18, 30));

    private final LocalTime startTime;
    private final LocalTime endTime;

    DiningType(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static DiningType fromTime(LocalTime time) {
        for (DiningType diningType : values()) {
            if (time.isAfter(diningType.startTime) && time.isBefore(diningType.endTime)) {
                return diningType;
            }
        }
        throw new IllegalArgumentException("No DiningType found for the given time: " + time);
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
