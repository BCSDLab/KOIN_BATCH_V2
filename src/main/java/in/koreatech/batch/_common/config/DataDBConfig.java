package in.koreatech.batch._common.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableJpaRepositories(
    basePackages = "in.koreatech.batch",
    entityManagerFactoryRef = "dataEntityManager",
    transactionManagerRef = "dataTransactionManager"
)
@RequiredArgsConstructor
public class DataDBConfig {

    private final DataDBProperties dataDBProperties;

    @Bean(name = "dataDBSource")
    @ConfigurationProperties(prefix = "spring.datasource.koin")
    public DataSource dataDBSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dataEntityManager")
    public LocalContainerEntityManagerFactoryBean dataEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataDBSource());
        em.setPackagesToScan(dataDBProperties.packagesToScan());
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.show_sql", dataDBProperties.showSql());
        properties.put("hibernate.ddl.auto", dataDBProperties.ddlAuto());
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "dataTransactionManager")
    public PlatformTransactionManager dataTransactionManager(
        @Qualifier(value = "dataEntityManager") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
