package com.github.azell.jooq.app;

import com.github.azell.jooq.transactions.JooqFactory;

public class App {
  private final JooqFactory factory;

  public App(JooqFactory factory) {
    this.factory = factory;
  }
}
