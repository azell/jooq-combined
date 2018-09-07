package com.github.azell.jooq.app;

import static org.jooq.SQLDialect.HSQLDB;

import com.github.azell.jooq.transactions.JooqFactory;
import com.github.azell.jooq.transactions.JooqTransactionFactory;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class HyperAppTest extends AppTest {
  @Configuration
  @EnableTransactionManagement
  static class ContextConfiguration {
    @Bean
    public App app(JooqFactory factory) {
      return new App(factory);
    }

    @Bean
    public DataSource dataSource() {
      EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();

      return builder.setType(EmbeddedDatabaseType.HSQL).build();
    }

    @Bean
    public JooqFactory jooqFactory(DataSource ds) {
      return new JooqTransactionFactory(ds, HSQLDB);
    }

    @Bean
    public SpringLiquibase springLiquibase(DataSource ds) {
      return load(ds);
    }

    @Bean
    public PlatformTransactionManager txManager(DataSource ds) {
      return new DataSourceTransactionManager(ds);
    }
  }
}
