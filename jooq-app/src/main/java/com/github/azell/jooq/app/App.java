package com.github.azell.jooq.app;

import com.github.azell.jooq.models.tables.records.BookRecord;
import com.github.azell.jooq.models.tables.records.AuthorRecord;
import com.github.azell.jooq.transactions.JooqFactory;

import org.jooq.DSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import static com.github.azell.jooq.models.Tables.AUTHOR;
import static com.github.azell.jooq.models.Tables.BOOK;

@Transactional
public class App {
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  private final JooqFactory factory;

  public App(JooqFactory factory) {
    this.factory = factory;
  }

  public long createAuthor(String firstName, String lastName) {
    DSLContext ctx = factory.context();
    AuthorRecord author = ctx.newRecord(AUTHOR);

    author.setFirstName(firstName);
    author.setLastName(lastName);

    author.insert();

    LOGGER.info("author created: {}", author);

    return author.getId();
  }

  public long createBook(long authorId, String title, String language) {
    DSLContext ctx = factory.context();
    BookRecord book = ctx.newRecord(BOOK);

    book.setAuthorId(authorId);
    book.setTitle(title);
    book.setLanguage(language);

    book.insert();

    LOGGER.info("book created: {}", book);

    return book.getId();
  }
}
