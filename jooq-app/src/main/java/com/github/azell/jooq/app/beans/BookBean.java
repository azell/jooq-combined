package com.github.azell.jooq.app.beans;

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

public class BookBean {
  private Long       id;
  private AuthorBean author;
  private String     title;
  private String     language;

  /** {@inheritDoc} */
  @Override
  public final boolean equals(Object obj) {
    boolean rc = false;

    if (this == obj) {
      rc = true;
    } else if (obj instanceof BookBean) {
      BookBean that = (BookBean) obj;

      rc = Objects.equals(id, that.id)
           && Objects.equals(author, that.author)
           && Objects.equals(title, that.title)
           && Objects.equals(language, that.language);
    }

    return rc;
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return Objects.hash(id, author, title, language);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return toStringHelper(this).omitNullValues()
                               .add("id", id)
                               .add("author", author)
                               .add("title", title)
                               .add("language", language)
                               .toString();
  }

  public AuthorBean getAuthor() {
    return author;
  }

  public void setAuthor(AuthorBean author) {
    this.author = author;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
