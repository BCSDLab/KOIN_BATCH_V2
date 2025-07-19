package in.koreatech.batch.domain.bus.model;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(collection = "citybus_timetables")
@NoArgsConstructor(access = PROTECTED)
public class CityBusTimetable {

    /**
     * 1. 한기대 -> 터미널행 시내버스의 기점은 한기대 정류장이 아님
     * 2. 400번은 병천3리, 402번은 황사동, 405번은 유관순열사사적지에서 출발해서 한기대 정류장에 도착
     * 3. 제공받는 시내버스의 운행 시간은 기점 기준이기 때문에 각 버스의 기점에서 한기대 정류장까지 이동 시간을 더해서 보정해야 함
     */
    private static final Integer ADDITIONAL_TIME_DEPART_TO_KOREATECH_400 = 6;
    private static final Integer ADDITIONAL_TIME_DEPART_TO_KOREATECH_402 = 13;
    private static final Integer ADDITIONAL_TIME_DEPART_TO_KOREATECH_405 = 7;
    /**
     * 1. 천안역 -> 한기대행 시내버스의 기점은 천안 터미널 정류장.
     * 2. 제공받는 시내버스의 운행 시간은 기점 기준이기 때문에 터미널에서 천안역 정류장까지 이동 시간을 더해서 보정해야 함
     */
    private static final Integer ADDITIONAL_TIME_DEPART_TO_STATION = 7;

    // 코인 API의 경우 String으로 관리
    @Id
    @Field("_id")
    private Integer routeId;

    @Field("bus_info")
    private BusInfo busInfo;

    @Field("bus_timetables")
    private List<BusTimetable> busTimetables = new ArrayList<>();

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Builder
    private CityBusTimetable(Integer routeId, BusInfo busInfo, List<BusTimetable> busTimetables, LocalDateTime updatedAt) {
        this.routeId = routeId;
        this.busInfo = busInfo;
        this.busTimetables = busTimetables;
        this.updatedAt = updatedAt;
    }

    @Getter
    public static class BusInfo {

        @Field("number")
        private Long number;

        @Field("depart_node")
        private String depart;

        @Field("arrival_node")
        private String arrival;

        @Builder
        private BusInfo(Long number, String depart, String arrival) {
            this.number = number;
            this.depart = depart;
            this.arrival = arrival;
        }
    }

    @Getter
    public static class BusTimetable {
        @Field("day_of_week")
        private String dayOfWeek;

        @Field("depart_info")
        private List<String> departInfo;

        @Builder
        private BusTimetable(String dayOfWeek, List<String> departInfo) {
            this.dayOfWeek = dayOfWeek;
            this.departInfo = departInfo;
        }
    }
}
