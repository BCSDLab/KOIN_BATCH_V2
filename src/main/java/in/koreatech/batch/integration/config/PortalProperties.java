package in.koreatech.batch.integration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "portal")
public record PortalProperties(
    String id,
    String pw,
    String ip,
    String cookie,
    Url url
) {
    public record Url(
        String home,
        String checkLoginId,
        String checkSecondLogin,
        String sso,
        String ssoLogin
    ) {

    }
}
