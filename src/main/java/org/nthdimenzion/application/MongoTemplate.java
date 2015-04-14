package org.nthdimenzion.application;

import com.mongodb.Mongo;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;

/**
 * Created by pradyumna on 14-04-2015.
 */
public class MongoTemplate extends org.springframework.data.mongodb.core.MongoTemplate {
    public MongoTemplate(Mongo mongo, String databaseName) {
        super(mongo, databaseName);
    }

    public MongoTemplate(Mongo mongo, String databaseName, UserCredentials userCredentials) {
        super(mongo, databaseName, userCredentials);
    }

    public MongoTemplate(MongoDbFactory mongoDbFactory) {
        super(mongoDbFactory);
    }

    public MongoTemplate(MongoDbFactory mongoDbFactory, MongoConverter mongoConverter) {
        super(mongoDbFactory, mongoConverter);
    }

    @Override
    protected <T> void maybeEmitEvent(MongoMappingEvent<T> event) {
    }
}
