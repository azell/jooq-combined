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
    public App app(JooqFactory factory) {
      return new App(factory);
    }

    @Bean
    public DataSource dataSource(EmbeddedPostgres pg) {
      return pg.getPostgresDatabase();
    }

    @Bean
    public JooqFactory jooqFactory(DataSource ds) {
      return new JooqTransactionFactory(ds, POSTGRES_9_4);
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
    public SpringLiquibase springLiquibase(DataSource ds) {
      return load(ds);
    }

    @Bean
    public PlatformTransactionManager txManager(DataSource ds) {
      return new DataSourceTransactionManager(ds);
    }
  }
}
