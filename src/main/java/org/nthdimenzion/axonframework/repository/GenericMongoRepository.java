package org.nthdimenzion.axonframework.repository;

import com.mongodb.BasicDBObject;
import org.axonframework.domain.AggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.repository.AbstractRepository;
import org.axonframework.repository.AggregateNotFoundException;
import org.axonframework.repository.ConflictingAggregateVersionException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.BasicQuery;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by pradyumna on 31-03-2015.
 */
public class GenericMongoRepository<T extends AggregateRoot> extends AbstractRepository<T> {

    private final MongoTemplate mongoTemplate;
    private final String collectionName;
    private final List<String> restrictedFields = Arrays.asList();
    private Field idFieldName;

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
        Class klass = super.getAggregateType();
        try {
            Field[] fields = klass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(AggregateIdentifier.class)) {
                    field.setAccessible(true);
                    idFieldName = field;
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
     /*   objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        objectMapper.registerModule(new JodaModule());*/
    }

    @Override
    protected void doSave(T t) {
        BasicDBObject dbo = new BasicDBObject();
        Field[] fields = getAggregateType().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (ignoreFields(field)) {
                    dbo.put(field.getName(), field.get(t));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        dbo.removeField(idFieldName.getName());
        dbo.put("_id", getIdValue(t));
        dbo.put("_class", getAggregateType().getName());
        mongoTemplate.save(dbo, collectionName);
    }

    private boolean ignoreFields(Field field) {
        return field.isAnnotationPresent(Transient.class)
                || field.isAnnotationPresent(org.springframework.data.annotation.Transient.class)
                || java.lang.reflect.Modifier.isStatic(field.getModifiers());
    }

    @Override
    protected T doLoad(Object aggregateIdentifier, Long expectedVersion) {
        BasicDBObject query = new BasicDBObject();
        query.put(idFieldName.getName(), aggregateIdentifier);
        T aggregate = mongoTemplate.findOne(new BasicQuery(query), getAggregateType(), this.collectionName);
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
        mongoTemplate.remove(t, collectionName);
    }

    private Object getIdValue(T t) {
        try {
            return idFieldName.get(t);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
