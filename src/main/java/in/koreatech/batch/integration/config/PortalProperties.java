package in.koreatech.batch.integration.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "portal")
public record PortalProperties(
    String id,
    String pw,
    String ip,
    String cookie,
    Url url
) {
    public String securePw() {
        String credsDir = System.getenv("CREDENTIALS_DIRECTORY");
        if (credsDir == null) {
            return this.pw;
        }

        try {
            Path path = Paths.get(credsDir, "portal_password");
            return Files.readString(path).trim();
        } catch (IOException e) {
            throw new RuntimeException("운영 환경에서 Portal 자격 증명을 로드하지 못했습니다.", e);
        }
    }

    public record Url(
        String home,
        String checkLoginId,
        String checkSecondLogin,
        String sso,
        String ssoLogin
    ) {

    }
}
