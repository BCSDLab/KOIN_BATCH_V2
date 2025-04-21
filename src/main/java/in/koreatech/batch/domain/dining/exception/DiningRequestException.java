package in.koreatech.batch.domain.dining.exception;

import in.koreatech.batch._common.exception.custom.ExternalServiceException;

public class DiningRequestException extends ExternalServiceException {
  private static final String DEFAULT_MESSAGE = "식단 요청 중 오류가 발생했습니다.";

  public DiningRequestException(String message) {
    super(message);
  }

  public DiningRequestException(String message, String detail) {
    super(message, detail);
  }

  public static DiningRequestException withDetail(String detail) {
    return new DiningRequestException(DEFAULT_MESSAGE, detail);
  }
}
