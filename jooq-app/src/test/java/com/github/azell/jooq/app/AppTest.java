package com.github.azell.jooq.app;

import java.util.List;

import javax.inject.Inject;

import javax.sql.DataSource;

import com.github.azell.jooq.transactions.JooqFactory;
import com.github.azell.jooq.transactions.JooqTransactionFactory;

import liquibase.integration.spring.SpringLiquibase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng
  .AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.testng.annotations.Test;

import static org.jooq.SQLDialect.HSQLDB;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@Test
public class AppTest
        extends AbstractTransactionalTestNGSpringContextTests {
  @Inject
  private App app;

  public void shouldCreateNewAuthor() {
    long id = app.createAuthor("Donald", "Knuth");

    assertTrue(id >= 0);
  }

  public void shouldCreateNewBook() {
    long authorId = app.createAuthor("Joshua", "Bloch");
    long id = app.createBook(authorId, "Effective Java", "English");

    assertTrue(id >= 0);
  }

  @Test(expectedExceptions = DataIntegrityViolationException.class)
  public void shouldFailToCreateNewBookWithoutValidForeignKey() {
    app.createBook(-1, "Invalid Foreign Key", "Navi");
  }

  public void shouldGetMultipleBooks() {
    List<String> books = app.getBooksByAuthor("Stephen", "King");

    assertFalse(books.isEmpty());
  }

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
