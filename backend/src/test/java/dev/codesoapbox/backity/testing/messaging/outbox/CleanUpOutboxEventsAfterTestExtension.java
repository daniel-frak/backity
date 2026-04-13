package dev.codesoapbox.backity.testing.messaging.outbox;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.modulith.events.jpa.updating.DefaultJpaEventPublication;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;

/// Cleans up JPA outbox events after each test to ensure better test isolation.
///
/// [DefaultJpaEventPublication] entities are persisted in a separate transaction,
/// so they are not rolled back with the test transaction (if rollback is configured).
///
/// It is not necessary to use it in repository tests, as they are not configured for outbox event publication.
public class CleanUpOutboxEventsAfterTestExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) {
        var applicationContext = SpringExtension.getApplicationContext(context);

        EntityManager entityManager = applicationContext.getBean(EntityManager.class);
        JpaTransactionManager transactionManager = applicationContext.getBean(JpaTransactionManager.class);

        new TransactionTemplate(transactionManager).execute(_ -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaDelete<DefaultJpaEventPublication> criteriaDelete =
                    criteriaBuilder.createCriteriaDelete(DefaultJpaEventPublication.class);
            criteriaDelete.from(DefaultJpaEventPublication.class);

            entityManager.createQuery(criteriaDelete).executeUpdate();
            return null;
        });
    }
}
