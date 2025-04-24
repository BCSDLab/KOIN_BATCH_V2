package in.koreatech.batch.integration.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bus.city")
public record BusProperties(
    List<Integer> routes,
    Url url
) {
    public record Url(
        String routeId,
        String routeInfo,
        String timetable
    ) {

    }
}
