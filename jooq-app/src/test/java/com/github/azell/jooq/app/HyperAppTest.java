package com.github.azell.jooq.app;

import javax.sql.DataSource;

import com.github.azell.jooq.transactions.JooqFactory;
import com.github.azell.jooq.transactions.JooqTransactionFactory;

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

import static org.jooq.SQLDialect.HSQLDB;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class HyperAppTest extends AppTest {
  @Configuration
  @EnableTransactionManagement
  static class ContextConfiguration {
    @Bean
    public DataSource dataSource() {
      EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();

      return builder.setType(EmbeddedDatabaseType.HSQL).build();
    }

    @Bean
    public JooqFactory jooqFactory() {
      return new JooqTransactionFactory(dataSource(), HSQLDB);
    }

    @Bean
    public SpringLiquibase springLiquibase() {
      return load(dataSource());
    }

    @Bean
    public PlatformTransactionManager txManager() {
      return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public App app() {
      return new App(jooqFactory());
    }
  }
}
