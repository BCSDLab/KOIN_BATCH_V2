package in.koreatech.batch.domain.bus.exception;

import in.koreatech.batch._common.exception.custom.ExternalServiceException;
import in.koreatech.batch.domain.portal.exception.PortalLoginException;

public class CityBusRouteApiException extends ExternalServiceException {
    private static final String DEFAULT_MESSAGE = "시내버스 API 요청 중 오류가 발생했습니다.";

    public CityBusRouteApiException(String message) {
        super(message);
    }

    public CityBusRouteApiException(String message, String detail) {
        super(message, detail);
    }

    public static PortalLoginException withDetail(String detail) {
        return new PortalLoginException(DEFAULT_MESSAGE, detail);
    }
}
