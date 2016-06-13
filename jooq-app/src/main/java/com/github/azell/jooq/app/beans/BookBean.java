package com.github.azell.jooq.app.beans;

public class BookBean {
  private Long       id;
  private AuthorBean author;
  private String     title;
  private String     language;

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
