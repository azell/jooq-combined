package com.github.azell.jooq.transactions;

import java.sql.SQLException;

import org.jooq.ExecuteContext;
import org.jooq.impl.DefaultExecuteListener;

import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;

/**
 * This class converts between native SQL and Spring JDBC exceptions.
 *
 * @author $author$
 * @version $Revision$, $Date$
 */
public class SpringTxExecuteListener extends DefaultExecuteListener {

  /** {@inheritDoc} */
  @Override
  public void exception(ExecuteContext ctx) {
    SQLException e = ctx.sqlException();

    if (e != null) {
      String name = ctx.configuration().dialect().thirdParty().springDbName();

      /* Prefer product name, if available. */
      SQLExceptionTranslator translator =
        (name != null)
        ? new SQLErrorCodeSQLExceptionTranslator(name)
        : new SQLStateSQLExceptionTranslator();

      ctx.exception(translator.translate("jOOQ", ctx.sql(), e));
    }
  }
}
