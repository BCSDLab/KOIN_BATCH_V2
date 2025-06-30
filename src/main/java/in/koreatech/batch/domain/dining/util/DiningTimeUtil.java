package in.koreatech.batch.domain.dining.util;

import java.time.LocalTime;

public class DiningTimeUtil {

    private DiningTimeUtil() {
    }

    public static boolean isMealTimeNow() {
        LocalTime now = LocalTime.now();
        return (now.isAfter(LocalTime.of(7, 0)) && now.isBefore(LocalTime.of(9, 0))) || // 아침
                (now.isAfter(LocalTime.of(11, 30)) && now.isBefore(LocalTime.of(13, 30))) || // 점심
                (now.isAfter(LocalTime.of(17, 30)) && now.isBefore(LocalTime.of(19, 0))); // 저녁
    }
}
