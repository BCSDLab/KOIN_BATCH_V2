package in.koreatech.batch.domain.bus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CityBusRouteIdApiResponse(
    List<InnerRoute> resultList
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record InnerRoute(
        @JsonProperty("ROUTE_ID")
        Integer routeId
    ) {

    }
}
