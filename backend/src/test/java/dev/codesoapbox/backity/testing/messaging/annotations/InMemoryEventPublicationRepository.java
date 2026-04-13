package dev.codesoapbox.backity.testing.messaging.annotations;

import org.springframework.modulith.events.EventPublication;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.modulith.events.core.EventSerializer;
import org.springframework.modulith.events.core.PublicationTargetIdentifier;
import org.springframework.modulith.events.core.TargetEventPublication;
import org.springframework.modulith.events.support.CompletionMode;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

// @TODO Test
// @TODO Refactor? Simplify?
// @TODO Consider using H2 with a custom DDL script instead?
/**
 * A custom EventPublicationRepository, so that we don't have to use a real database.
 */
class InMemoryEventPublicationRepository implements EventPublicationRepository {

    private final Map<UUID, InMemoryEventPublication> store = new ConcurrentHashMap<>();

    private final EventSerializer serializer;
    private final CompletionMode completionMode;

    public InMemoryEventPublicationRepository(EventSerializer serializer,
                                              CompletionMode completionMode) {
        this.serializer = serializer;
        this.completionMode = completionMode;
    }

    @Override
    public TargetEventPublication create(TargetEventPublication publication) {
        InMemoryEventPublication entity = InMemoryEventPublication.from(publication, serializer);
        store.put(entity.id, entity);
        return publication;
    }

    @Override
    public void markProcessing(UUID identifier) {
        update(identifier, p -> p.status = EventPublication.Status.PROCESSING);
    }

    private void update(UUID id, Consumer<InMemoryEventPublication> fn) {
        var p = store.get(id);
        if (p != null) fn.accept(p);
    }

    @Override
    public void markCompleted(Object event, PublicationTargetIdentifier identifier, Instant completionDate) {

        var serializedEvent = serializer.serialize(event).toString();
        var listenerId = identifier.getValue();

        var match = findByEventAndListener(serializedEvent, listenerId);

        if (match == null) return;

        applyCompletionLogic(match, completionDate);
    }

    private void applyCompletionLogic(InMemoryEventPublication p, Instant completionDate) {

        if (completionMode == CompletionMode.DELETE) {
            store.remove(p.id);
            return;
        }

        if (completionMode == CompletionMode.ARCHIVE) {
            store.remove(p.id);
            var archived = p.copy();
            archived.completionDate = completionDate;
            archived.status = EventPublication.Status.COMPLETED;
            store.put(archived.id, archived);
            return;
        }

        p.completionDate = completionDate;
        p.status = EventPublication.Status.COMPLETED;
    }

    private InMemoryEventPublication findByEventAndListener(String event, String listener) {
        return store.values().stream()
                .filter(p -> Objects.equals(p.serializedEvent, event))
                .filter(p -> Objects.equals(p.listenerId, listener))
                .filter(p -> p.completionDate == null)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void markCompleted(UUID identifier, Instant completionDate) {

        var entity = store.get(identifier);
        if (entity == null) return;

        applyCompletionLogic(entity, completionDate);
    }

    @Override
    public void markFailed(UUID identifier) {
        update(identifier, p -> p.status = EventPublication.Status.FAILED);
    }

    @Override
    public boolean markResubmitted(UUID identifier, Instant resubmissionDate) {
        var entity = store.get(identifier);
        if (entity == null || entity.status == EventPublication.Status.RESUBMITTED) return false;

        entity.status = EventPublication.Status.RESUBMITTED;
        entity.completionAttempts++;
        entity.lastResubmissionDate = resubmissionDate;
        return true;
    }

    @Override
    public List<TargetEventPublication> findIncompletePublications() {
        return store.values().stream()
                .filter(p -> p.completionDate == null)
                .sorted(Comparator.comparing(p -> p.publicationDate))
                .map(this::toDomain)
                .toList();
    }

    private TargetEventPublication toDomain(InMemoryEventPublication p) {
        return new SimpleTargetEventPublication(p, serializer);
    }

    @Override
    public List<TargetEventPublication> findIncompletePublicationsPublishedBefore(Instant instant) {
        return store.values().stream()
                .filter(p -> p.completionDate == null)
                .filter(p -> p.publicationDate.isBefore(instant))
                .sorted(Comparator.comparing(p -> p.publicationDate))
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<TargetEventPublication> findIncompletePublicationsByEventAndTargetIdentifier(
            Object event,
            PublicationTargetIdentifier targetIdentifier) {

        var serialized = serializer.serialize(event).toString();
        var listenerId = targetIdentifier.getValue();

        return Optional.ofNullable(findByEventAndListener(serialized, listenerId))
                .map(this::toDomain);
    }

    @Override
    public List<TargetEventPublication> findCompletedPublications() {
        return store.values().stream()
                .filter(p -> p.completionDate != null)
                .sorted(Comparator.comparing(p -> p.publicationDate))
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<TargetEventPublication> findFailedPublications(FailedCriteria criteria) {

        return store.values().stream()
                .filter(p ->
                        p.status == EventPublication.Status.FAILED ||
                                (p.status == null && p.completionDate == null)
                )
                .filter(p -> criteria.getPublicationDateReference() == null
                        || p.publicationDate.isBefore(criteria.getPublicationDateReference()))
                .sorted(Comparator.comparing(p -> p.publicationDate))
                .limit(criteria.getMaxItemsToRead() == -1
                        ? Long.MAX_VALUE
                        : criteria.getMaxItemsToRead())
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deletePublications(List<UUID> identifiers) {
        identifiers.forEach(store::remove);
    }


    @Override
    public void deleteCompletedPublications() {
        store.values().removeIf(p -> p.completionDate != null);
    }

    @Override
    public void deleteCompletedPublicationsBefore(Instant instant) {
        store.values().removeIf(p ->
                p.completionDate != null &&
                        p.completionDate.isBefore(instant)
        );
    }

    @Override
    public List<TargetEventPublication> findByStatus(EventPublication.Status status) {
        return store.values().stream()
                .filter(p -> p.status == status)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public int countByStatus(EventPublication.Status status) {
        return (int) store.values().stream()
                .filter(p -> p.status == status)
                .count();
    }

    private static class InMemoryEventPublication {

        UUID id;
        Instant publicationDate;
        Instant completionDate;

        String listenerId;
        String serializedEvent;
        Class<?> eventType;

        EventPublication.Status status;
        int completionAttempts;
        Instant lastResubmissionDate;

        static InMemoryEventPublication from(TargetEventPublication pub, EventSerializer serializer) {
            var publication = new InMemoryEventPublication();
            publication.id = pub.getIdentifier();
            publication.publicationDate = pub.getPublicationDate();
            publication.listenerId = pub.getTargetIdentifier().getValue();
            publication.serializedEvent = serializer.serialize(pub.getEvent()).toString();
            publication.eventType = pub.getEvent().getClass();
            publication.status = pub.getStatus();
            publication.completionDate = pub.getCompletionDate().orElse(null);
            return publication;
        }

        InMemoryEventPublication copy() {
            var p = new InMemoryEventPublication();
            p.id = this.id;
            p.publicationDate = this.publicationDate;
            p.completionDate = this.completionDate;
            p.listenerId = this.listenerId;
            p.serializedEvent = this.serializedEvent;
            p.eventType = this.eventType;
            p.status = this.status;
            p.completionAttempts = this.completionAttempts;
            p.lastResubmissionDate = this.lastResubmissionDate;
            return p;
        }
    }

    private static class SimpleTargetEventPublication implements TargetEventPublication {

        private final InMemoryEventPublication p;
        private final EventSerializer serializer;

        SimpleTargetEventPublication(InMemoryEventPublication p, EventSerializer serializer) {
            this.p = p;
            this.serializer = serializer;
        }

        @Override
        public UUID getIdentifier() {
            return p.id;
        }

        @Override
        public Object getEvent() {
            return serializer.deserialize(p.serializedEvent, p.eventType);
        }

        @Override
        public PublicationTargetIdentifier getTargetIdentifier() {
            return PublicationTargetIdentifier.of(p.listenerId);
        }

        @Override
        public Instant getPublicationDate() {
            return p.publicationDate;
        }

        @Override
        public Optional<Instant> getCompletionDate() {
            return Optional.ofNullable(p.completionDate);
        }

        @Override
        public boolean isPublicationCompleted() {
            return p.completionDate != null;
        }

        @Override
        public void markCompleted(Instant instant) {
            p.completionDate = instant;
        }

        @Override
        public Status getStatus() {
            return p.status;
        }

        @Override
        public int getCompletionAttempts() {
            return p.completionAttempts;
        }

        @Override
        public Instant getLastResubmissionDate() {
            return p.lastResubmissionDate;
        }
    }
}
