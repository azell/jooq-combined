package com.github.azell.jooq.app;

import java.util.Collections;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import com.github.azell.jooq.app.beans.AuthorBean;
import com.github.azell.jooq.models.tables.records.AuthorRecord;
import com.github.azell.jooq.models.tables.records.BookRecord;
import com.github.azell.jooq.transactions.JooqFactory;

import static com.github.azell.jooq.models.Tables.AUTHOR;
import static com.github.azell.jooq.models.Tables.BOOK;

@Transactional
public class App {
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
  private final JooqFactory   factory;

  public App(JooqFactory factory) {
    this.factory = factory;
  }

  public long createAuthor(String firstName, String lastName) {
    DSLContext   ctx    = factory.context();
    AuthorRecord author = ctx.newRecord(AUTHOR);

    author.setFirstName(firstName);
    author.setLastName(lastName);

    author.insert();

    LOGGER.info("author created: {}", author);

    return author.getId();
  }

  public long createBook(long authorId, String title, String language) {
    DSLContext ctx  = factory.context();
    BookRecord book = ctx.newRecord(BOOK);

    book.setAuthorId(authorId);
    book.setTitle(title);
    book.setLanguage(language);

    book.insert();

    LOGGER.info("book created: {}", book);

    return book.getId();
  }

  @Transactional(readOnly = true)
  public List<AuthorBean> getAuthors() {
    DSLContext       ctx     = factory.context();
    List<AuthorBean> authors = ctx.select()
                                  .from(AUTHOR)
                                  .fetchInto(AuthorBean.class);

    LOGGER.info("authors: {}", authors);

    return authors;

  }

  @Transactional(readOnly = true)
  public List<String> getBooksByAuthor(String firstName, String lastName) {
    List<String> values = Collections.emptyList();

    DSLContext   ctx    = factory.context();
    Record       record = ctx.select(AUTHOR.ID)
                             .from(AUTHOR)
                             .where(AUTHOR.FIRST_NAME.eq(firstName))
                             .and(AUTHOR.LAST_NAME.eq(lastName))
                             .fetchOne();

    if (record != null) {
      Long authorId = record.getValue(AUTHOR.ID);

      values = ctx.select(BOOK.TITLE)
                  .from(BOOK)
                  .join(AUTHOR)
                  .onKey()
                  .where(BOOK.AUTHOR_ID.eq(authorId))
                  .fetch()
                  .getValues(BOOK.TITLE);

      LOGGER.info("book titles: {} {} / {}", firstName, lastName, values);
    }

    return values;
  }
}
