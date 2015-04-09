package org.nthdimenzion.axonframework.repository;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.axonframework.domain.AggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.repository.AbstractRepository;
import org.axonframework.repository.AggregateNotFoundException;
import org.axonframework.repository.ConflictingAggregateVersionException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Transient;
import java.lang.reflect.Field;

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
        Class klass = super.getAggregateType();
        try {
            Field[] fields = klass.getDeclaredFields();
            BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Transient.class) && !"logger".equalsIgnoreCase(field.getName())) {
                    field.setAccessible(true);
                    builder.append(field.getName(), field.get(t));

                }
                if (field.isAnnotationPresent(AggregateIdentifier.class)) {
                    builder.append("_id", field.get(t).toString());
                }
            }
            DBObject dbObject = builder.get();
            mongoTemplate.save(dbObject, collectionName);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected T doLoad(Object aggregateIdentifier, Long expectedVersion) {
        T aggregate = mongoTemplate.findById(aggregateIdentifier.toString(), getAggregateType(), this.collectionName);
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
