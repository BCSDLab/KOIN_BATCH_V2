package in.koreatech.batch.domain.portal.exception;

import in.koreatech.batch._common.exception.custom.ExternalServiceException;

public class PortalLoginException extends ExternalServiceException {
    private static final String DEFAULT_MESSAGE = "아우누리 포털 로그인 과정에서 오류가 발생했습니다.";

    public PortalLoginException(String message) {
        super(message);
    }

    public PortalLoginException(String message, String detail) {
        super(message, detail);
    }

    public static PortalLoginException withDetail(String detail) {
        return new PortalLoginException(DEFAULT_MESSAGE, detail);
    }
}
