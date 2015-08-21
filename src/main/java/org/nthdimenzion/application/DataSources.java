package org.nthdimenzion.application;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Author: Nthdimenzion
 */

@Configuration
public class DataSources {


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


   /* @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        Mongo mongo = new MongoClient(mongoDBUrl, port);
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongo, mongoDbName, new UserCredentials(mongoDbUserName, mongoDbPassword), mongoDbName);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);
        return mongoTemplate;
    }*/
}

