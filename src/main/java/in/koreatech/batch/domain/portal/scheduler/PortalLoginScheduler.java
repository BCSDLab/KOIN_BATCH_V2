package in.koreatech.batch.domain.portal.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import in.koreatech.batch.domain.portal.client.PortalLoginClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortalLoginScheduler {

    private final PortalLoginClient portalLoginClient;

    @Scheduled(fixedRate = 21600000)
    public void refreshLoginCookie() {
        try {
            portalLoginClient.refreshLoginCookie();
        } catch (Exception e) {
            log.error("포털 로그인 중 오류 발생", e);
        }
    }
}
