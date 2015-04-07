package org.nthdimenzion.axonframework.repository;

import org.axonframework.domain.AggregateRoot;
import org.axonframework.repository.AbstractRepository;
import org.axonframework.repository.AggregateNotFoundException;
import org.axonframework.repository.ConflictingAggregateVersionException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

import static java.lang.String.format;

/**
 * Created by pradyumna on 31-03-2015.
 */
public class GenericMongoRepository<T extends AggregateRoot> extends AbstractRepository<T> {

    private final MongoTemplate mongoTemplate;
    private final String collectionName;

    /**
     * Initialize a repository for storing aggregates of the given <code>aggregateType</code>. No additional locking
     * will be used.
     *
     * @param mongoTemplate The MongoTemplate provided for storing this entity
     * @param aggregateType the aggregate type this repository manages
     */
    public GenericMongoRepository(MongoTemplate mongoTemplate, Class<T> aggregateType) {
        super(aggregateType);
        this.mongoTemplate = mongoTemplate;
        Document annotation = aggregateType.getAnnotation(Document.class);
        assert annotation != null;
        this.collectionName = annotation.collection();
    }

    @Override
    protected void doSave(T t) {
        mongoTemplate.save(t);
    }

    @Override
    protected T doLoad(Object aggregateIdentifier, Long expectedVersion) {
        T aggregate = mongoTemplate.findById(aggregateIdentifier, getAggregateType(), this.collectionName);
        if (aggregate == null) {
            throw new AggregateNotFoundException(aggregateIdentifier, format(
                    "Aggregate [%s] with identifier [%s] not found",
                    getAggregateType().getSimpleName(),
                    aggregateIdentifier));
        } else if (expectedVersion != null && aggregate.getVersion() != null
                && !expectedVersion.equals(aggregate.getVersion())) {
            throw new ConflictingAggregateVersionException(aggregateIdentifier,
                    expectedVersion,
                    aggregate.getVersion());
        }
        return aggregate;
    }

    @Override
    protected void doDelete(T t) {
        mongoTemplate.remove(t);
    }
}
