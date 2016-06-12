package com.github.azell.jooq.transactions;

import javax.sql.DataSource;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultExecuteListenerProvider;

/**
 * This class configures Spring Transaction management.
 *
 * @author $author$
 * @version $Revision$, $Date$
 */
public class JooqTransactionFactory implements JooqFactory {
  private final Configuration config = new DefaultConfiguration();

  public JooqTransactionFactory(DataSource ds, SQLDialect dialect) {
    this(ds, dialect, new Settings().withRenderSchema(false));
  }

  public JooqTransactionFactory(DataSource ds, SQLDialect dialect,
                                Settings settings) {
    config.set(new SpringTxConnectionProvider(ds))
          .set(dialect)
          .set(settings)
          .set(new DefaultExecuteListenerProvider(
              new SpringTxExecuteListener()));
  }

  /** {@inheritDoc} */
  @Override
  public DSLContext context() {
    return DSL.using(config);
  }
}
