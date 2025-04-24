package in.koreatech.batch.domain.bus.reader;

import static in.koreatech.batch.domain.bus.dto.CityBusRouteInfoApiResponse.CityBusRouteInfo;

import java.util.LinkedList;
import java.util.Queue;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import in.koreatech.batch.domain.bus.client.CityBusClient;
import in.koreatech.batch.integration.config.BusProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteIdReader implements ItemReader<CityBusRouteInfo> {

    private final Queue<Integer> routeIds = new LinkedList<>();
    private final Queue<CityBusRouteInfo> routeInfos = new LinkedList<>();
    private final BusProperties busProperties;
    private final CityBusClient cityBusClient;

    public void init() {
        for (Integer route : busProperties.routes()) {
            routeIds.addAll(cityBusClient.requestCityBusRouteIds(route));
        }
        log.info("Route Ids : {}", routeIds);

        for (Integer routeId : routeIds) {
            routeInfos.addAll(cityBusClient.requestCityBusRouteInfos(routeId));
        }
        log.info("Route Infos : {}", routeInfos);
    }

    @Override
    public CityBusRouteInfo read() {
        return routeInfos.isEmpty() ? null : routeInfos.poll();
    }
}
