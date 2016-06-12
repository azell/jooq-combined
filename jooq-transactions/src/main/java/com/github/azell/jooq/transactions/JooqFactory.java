package com.github.azell.jooq.transactions;

import org.jooq.DSLContext;

/**
 * This interface abstracts DSLContext creation.
 *
 * @author $author$
 * @version $Revision$, $Date$
 */
public interface JooqFactory {
  DSLContext context();
}
