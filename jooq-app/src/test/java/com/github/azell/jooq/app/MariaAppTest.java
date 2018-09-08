package com.github.azell.jooq.app;

import static java.util.Locale.ENGLISH;
import static org.jooq.SQLDialect.MARIADB;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.github.azell.jooq.transactions.JooqFactory;
import com.github.azell.jooq.transactions.JooqTransactionFactory;
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

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class MariaAppTest extends AppTest {
  @Configuration
  @EnableTransactionManagement
  static class ContextConfiguration {
    @Bean
    public App app(JooqFactory factory) {
      return new App(factory);
    }

    @Bean
    public DataSource dataSource(DBConfiguration config, DB db) {
      DriverManagerDataSource ds = new DriverManagerDataSource();

      ds.setDriverClassName("org.mariadb.jdbc.Driver");
      ds.setUrl(
          String.format(
              ENGLISH, "jdbc:mariadb://localhost:%d/mydb?useCompression=true", config.getPort()));
      ds.setUsername(APP_USER);
      ds.setPassword(APP_PASS);

      return ds;
    }

    @Bean(destroyMethod = "stop")
    public DB db(DBConfiguration config) throws ManagedProcessException {
      DB db = DB.newEmbeddedDB(config);

      db.start();
      db.createDB(APP_DB);

      return db;
    }

    @Bean
    public DBConfiguration dbConfiguration() {
      return DBConfigurationBuilder.newBuilder().setPort(0).build();
    }

    @Bean
    public JooqFactory jooqFactory(DataSource ds) {
      return new JooqTransactionFactory(ds, MARIADB);
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
