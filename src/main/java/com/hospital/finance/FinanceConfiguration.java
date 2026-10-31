package com.hospital.finance;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class FinanceConfiguration {
    /** Disabled by default. Explicit opt-in for reviewed migrations or isolated demos only. */
    @Bean
    @ConditionalOnProperty(name="hospital.finance.initialize-schema", havingValue="true")
    public DataSourceInitializer financeSchemaInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(
                new ClassPathResource("db/finance/V1__finance.sql"),
                new ClassPathResource("db/finance/V2__demo_tax.sql")));
        return initializer;
    }
}
