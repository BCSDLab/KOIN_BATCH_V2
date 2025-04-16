package in.koreatech.batch._common.auth.exception;

import in.koreatech.batch._common.exception.custom.DataNotFoundException;

public class CookieNotFoundException extends DataNotFoundException {
    private static final String DEFAULT_MESSAGE = "쿠키가 존재하지 않습니다.";

    public CookieNotFoundException(String message) {
        super(message);
    }

    public CookieNotFoundException(String message, String detail) {
        super(message, detail);
    }

    public static CookieNotFoundException withDetail(String detail) {
        return new CookieNotFoundException(DEFAULT_MESSAGE, detail);
    }
}
