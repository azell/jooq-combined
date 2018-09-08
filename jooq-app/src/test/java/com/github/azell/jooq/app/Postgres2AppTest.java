package com.github.azell.jooq.app;

import static java.util.Locale.ENGLISH;
import static org.jooq.SQLDialect.POSTGRES_10;

import com.github.azell.jooq.transactions.JooqFactory;
import com.github.azell.jooq.transactions.JooqTransactionFactory;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class Postgres2AppTest extends AppTest {
  @Configuration
  @EnableTransactionManagement
  static class ContextConfiguration {
    @Bean
    public App app(JooqFactory factory) {
      return new App(factory);
    }

    @Bean
    public DataSource dataSource(PostgresConfig config, PostgresProcess pg) {
      DriverManagerDataSource ds = new DriverManagerDataSource();

      ds.setDriverClassName("org.postgresql.Driver");
      ds.setUrl(
          String.format(
              ENGLISH,
              "jdbc:postgresql://%s:%s/%s",
              config.net().host(),
              config.net().port(),
              config.storage().dbName()));
      ds.setUsername(config.credentials().username());
      ds.setPassword(config.credentials().password());

      return ds;
    }

    @Bean
    public PostgresConfig config() {
      try {
        return PostgresConfig.defaultWithDbName(APP_DB, "user", "pass");
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    @Bean
    public JooqFactory jooqFactory(DataSource ds) {
      return new JooqTransactionFactory(ds, POSTGRES_10);
    }

    @Bean(destroyMethod = "stop")
    public PostgresProcess pg(PostgresConfig config) {
      try {
        return PostgresStarter.getDefaultInstance().prepare(config).start();
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
