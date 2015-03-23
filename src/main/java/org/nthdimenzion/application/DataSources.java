package org.nthdimenzion.application;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import javax.sql.DataSource;
import java.net.UnknownHostException;

/**
 * Author: Nthdimenzion
 */

@Configuration
public class DataSources {

    @Value(value = "${spring.mongoDB.host}")
    private String mongoDBUrl;

    @Value(value = "${spring.mongoDB.name}")
    private String mongoDbName;

    @Value(value = "${spring.mongoDB.port}")
    private int port;

    @Value(value = "${spring.mongoDB.username}")
    private String mongoDbUserName;

    @Value(value = "${spring.mongoDB.password}")
    private String mongoDbPassword;

    @Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jobsDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.jobs")
    public DataSource jobsDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        Mongo mongo = new MongoClient(mongoDBUrl, port);
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongo, mongoDbName, new UserCredentials(mongoDbUserName, mongoDbPassword), mongoDbName);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);
        return mongoTemplate;
    }
}

