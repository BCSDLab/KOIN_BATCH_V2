package in.koreatech.batch._common.exception.custom;

public class RequestTooFastException extends KoinException {

    public RequestTooFastException(String message) {
        super(message);
    }
}
