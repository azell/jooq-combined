package com.github.azell.jooq.app;

import java.io.IOException;
import java.io.UncheckedIOException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.jooq.SQLDialect.POSTGRES_9_4;

import com.github.azell.jooq.transactions.JooqFactory;
import com.github.azell.jooq.transactions.JooqTransactionFactory;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import liquibase.integration.spring.SpringLiquibase;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class PostgresAppTest extends AppTest {
  @Configuration
  @EnableTransactionManagement
  static class ContextConfiguration {
    @Bean
    public App app() {
      return new App(jooqFactory());
    }

    @Bean
    public DataSource dataSource() {
      return pg().getPostgresDatabase();
    }

    @Bean
    public JooqFactory jooqFactory() {
      return new JooqTransactionFactory(dataSource(), POSTGRES_9_4);
    }

    @Bean(destroyMethod = "close")
    public EmbeddedPostgres pg() {
      try {
        return EmbeddedPostgres.start();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    @Bean
    public SpringLiquibase springLiquibase() {
      return load(dataSource());
    }

    @Bean
    public PlatformTransactionManager txManager() {
      return new DataSourceTransactionManager(dataSource());
    }
  }
}
