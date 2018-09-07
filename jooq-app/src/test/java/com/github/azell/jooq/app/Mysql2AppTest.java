package com.github.azell.jooq.app;

import static org.jooq.SQLDialect.MYSQL_5_7;

import com.github.azell.jooq.transactions.JooqFactory;
import com.github.azell.jooq.transactions.JooqTransactionFactory;
import io.airlift.testing.mysql.TestingMySqlServer;
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
public class Mysql2AppTest extends AppTest {
  @Configuration
  @EnableTransactionManagement
  static class ContextConfiguration {
    @Bean
    public App app(JooqFactory factory) {
      return new App(factory);
    }

    @Bean
    public DataSource dataSource(TestingMySqlServer mysqld) {
      DriverManagerDataSource ds = new DriverManagerDataSource();

      ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
      ds.setUrl(mysqld.getJdbcUrl("mydb"));

      return ds;
    }

    @Bean(destroyMethod = "close")
    public TestingMySqlServer mysqld() throws Exception {
      return new TestingMySqlServer("root", "", "mydb");
    }

    @Bean
    public JooqFactory jooqFactory(DataSource ds) {
      return new JooqTransactionFactory(ds, MYSQL_5_7);
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
