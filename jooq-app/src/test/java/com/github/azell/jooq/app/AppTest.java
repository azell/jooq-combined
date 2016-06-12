package com.github.azell.jooq.app;

import javax.inject.Inject;

import javax.sql.DataSource;

import com.github.azell.jooq.transactions.JooqFactory;
import com.github.azell.jooq.transactions.JooqTransactionFactory;

import liquibase.integration.spring.SpringLiquibase;

import org.h2.jdbcx.JdbcConnectionPool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng
  .AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.testng.annotations.Test;

import static org.jooq.SQLDialect.H2;
import static org.testng.Assert.assertTrue;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@Test
public class AppTest
        extends AbstractTransactionalTestNGSpringContextTests {
  @Inject
  private App app;

  public void shouldCreateNewAuthor() {
    long id = app.createAuthor("James", "Gosling");

    assertTrue(id >= 0);
  }

  @Configuration
  @EnableTransactionManagement
  static class ContextConfiguration {
    @Bean(destroyMethod = "dispose")
    public DataSource dataSource() {
      return JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                                       "sa",
                                       "");
    }

    @Bean
    public JooqFactory jooqFactory() {
      return new JooqTransactionFactory(dataSource(), H2);
    }

    @Bean
    public SpringLiquibase springLiquibase() {
      SpringLiquibase obj = new SpringLiquibase();

      obj.setDataSource(dataSource());
      obj.setChangeLog("classpath:changelog/jooq.changelog-master.xml");
      obj.setContexts("test");

      return obj;
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
