package dev.codesoapbox.backity.testing.messaging.annotations;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * We need a dummy PlatformTransactionManager so that TransactionalEventListeners get triggered after commit.
 */
public class DummyPlatformTransactionManager extends AbstractPlatformTransactionManager {

    @Override
    protected Object doGetTransaction() throws TransactionException {
        // Dummy
        return new Object();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        // No-op
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        // No-op
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        // No-op
    }
}
