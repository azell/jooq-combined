/** Enable jOOQ API usage with a subset of dialects for the whole package. */
@Allow
@Require({H2, HSQLDB, MARIADB, MYSQL, POSTGRES})
package com.github.azell.jooq.app;

import static org.jooq.SQLDialect.H2;
import static org.jooq.SQLDialect.HSQLDB;
import static org.jooq.SQLDialect.MARIADB;
import static org.jooq.SQLDialect.MYSQL;
import static org.jooq.SQLDialect.POSTGRES;

import org.jooq.Allow;
import org.jooq.Require;
