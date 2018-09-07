package com.github.azell.jooq.app;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.github.azell.jooq.app.beans.AuthorBean;
import com.github.azell.jooq.app.beans.BookBean;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

@Test
public abstract class AppTest extends AbstractTransactionalTestNGSpringContextTests {
  @Inject private App app;

  static SpringLiquibase load(DataSource dataSource) {
    SpringLiquibase obj = new SpringLiquibase();

    obj.setDataSource(dataSource);
    obj.setChangeLog("classpath:changelog/jooq.changelog-master.xml");
    obj.setContexts("test");

    return obj;
  }

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
    app.createBook(-1, "Invalid Foreign Key", "Esperanto");
  }

  public void shouldGetMultipleAuthors() {
    List<AuthorBean> authors = app.getAuthors();

    assertFalse(authors.isEmpty());
  }

  public void shouldGetMultipleBooks() {
    List<BookBean> books = app.getBooksByAuthor("Stephen", "King");

    assertFalse(books.isEmpty());
  }

  public void shouldGetOneLanguage() {
    List<String> languages = app.getBookLanguages("Hamlet");

    assertEquals(languages, Arrays.asList("Portuguese"));
  }
}
