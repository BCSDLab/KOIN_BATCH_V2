package in.koreatech.batch._common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.datasource-data.hibernate")
public record DataDBProperties(
    String ddlAuto,
    Boolean showSql,
    String packagesToScan
) {
}
