package in.koreatech.batch._common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "portal")
public record PortalProperties(
    String id,
    String pw,
    String ip,
    String home,
    Cookie cookie,
    Url url
) {
    public record Cookie(
        String login
    ) {

    }

    public record Url(
        String checkFirstLogin,
        String checkSecondLogin,
        String sso,
        String ssoLogin,
        String diningMenu
    ) {

    }
}
