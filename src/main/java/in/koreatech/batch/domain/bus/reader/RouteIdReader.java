package in.koreatech.batch.domain.bus.reader;

import static in.koreatech.batch.domain.bus.dto.CityBusRouteInfoApiResponse.CityBusRouteInfo;

import java.util.LinkedList;
import java.util.Queue;

import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.stereotype.Component;

import in.koreatech.batch.domain.bus.client.CityBusClient;
import in.koreatech.batch.integration.config.BusProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteIdReader implements ItemStreamReader<CityBusRouteInfo> {

    private final BusProperties busProperties;
    private final CityBusClient cityBusClient;

    private Queue<CityBusRouteInfo> routeInfos;

    @Override
    public void open(@NotNull ExecutionContext executionContext) {
        routeInfos = new LinkedList<>();

        for (Integer route : busProperties.routes()) {
            var routeIds = cityBusClient.requestCityBusRouteIds(route);
            for (Integer routeId : routeIds) {
                routeInfos.addAll(cityBusClient.requestCityBusRouteInfos(routeId));
            }
        }

        log.info("Loaded {} routeInfos", routeInfos.size());
    }

    @Override
    public CityBusRouteInfo read() {
        return (routeInfos == null || routeInfos.isEmpty()) ? null : routeInfos.poll();
    }

    @Override public void update(@NotNull ExecutionContext executionContext) {}
    @Override public void close() {}
}
