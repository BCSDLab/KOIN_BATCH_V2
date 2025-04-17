package in.koreatech.batch._common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "portal")
public record PortalProperties(
    String cookieLogin,
    String id,
    String pw,
    String ip,
    String urlCheckFirstLogin,
    String home,
    String urlCheckSecondLogin,
    String urlSso,
    String urlSsoLogin
) {

}
