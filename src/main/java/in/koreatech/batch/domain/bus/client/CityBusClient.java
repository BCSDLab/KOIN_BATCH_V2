package in.koreatech.batch.domain.bus.client;

import static in.koreatech.batch.domain.bus.dto.CityBusRouteIdApiResponse.InnerRoute;
import static in.koreatech.batch.domain.bus.dto.CityBusRouteInfoApiResponse.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.koreatech.batch.domain.bus.dto.CityBusRouteIdApiResponse;
import in.koreatech.batch.domain.bus.dto.CityBusRouteInfoApiResponse;
import in.koreatech.batch.domain.bus.exception.CityBusRouteApiException;
import in.koreatech.batch.integration.config.BusProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@Component
@RequiredArgsConstructor
public class CityBusClient {

    private final ObjectMapper objectMapper;
    private final BusProperties busProperties;
    private final OkHttpClient okHttpClient;

    public List<Integer> requestCityBusRouteIds(Integer cityBusRoute) {
        Request request = new Request.Builder()
            .url(String.format(busProperties.url().routeId(), cityBusRoute))
            .build();
        List<Integer> routeIds = new ArrayList<>();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() != null) {
                String responseBody = response.body().string();
                routeIds.addAll(objectMapper.readValue(responseBody, CityBusRouteIdApiResponse.class)
                    .resultList()
                    .stream()
                    .map(InnerRoute::routeId)
                    .toList()
                );
            }
        } catch (IOException e) {
            throw CityBusRouteApiException.withDetail("버스 노선 ID 요청 중 오류가 발생했습니다.");
        }

        return routeIds;
    }

    public List<CityBusRouteInfo> requestCityBusRouteInfos(Integer cityBusRouteId) {
        Request request = new Request.Builder()
            .url(String.format(busProperties.url().routeInfo(), cityBusRouteId))
            .build();
        List<CityBusRouteInfo> routeInfos = new ArrayList<>();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() != null) {
                String responseBody = response.body().string();
                routeInfos.addAll(objectMapper.readValue(responseBody, CityBusRouteInfoApiResponse.class).resultList());
            }
        } catch (IOException e) {
            throw CityBusRouteApiException.withDetail("버스 노선 정보 요청 중 오류가 발생했습니다.");
        }

        return routeInfos;
    }
}
