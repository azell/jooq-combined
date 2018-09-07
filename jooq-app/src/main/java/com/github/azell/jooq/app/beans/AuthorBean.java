package com.github.azell.jooq.app.beans;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.Objects;

public class AuthorBean {
  private Long id;
  private String firstName;
  private String lastName;

  /** {@inheritDoc} */
  @Override
  public final boolean equals(Object obj) {
    boolean rc = false;

    if (this == obj) {
      rc = true;
    } else if (obj instanceof AuthorBean) {
      AuthorBean that = (AuthorBean) obj;

      rc =
          Objects.equals(id, that.id)
              && Objects.equals(firstName, that.firstName)
              && Objects.equals(lastName, that.lastName);
    }

    return rc;
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return Objects.hash(id, firstName, lastName);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return toStringHelper(this)
        .omitNullValues()
        .add("id", id)
        .add("firstName", firstName)
        .add("lastName", lastName)
        .toString();
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
