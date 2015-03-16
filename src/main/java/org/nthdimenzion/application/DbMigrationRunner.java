package org.nthdimenzion.application;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @author: pradyumna
 * @since 1.0 15/03/2015
 */
@Component
public class DbMigrationRunner {

    private static Logger log = LoggerFactory.getLogger(DbMigrationRunner.class);

    @Autowired
    private DataSource dataSource;

    @Bean(name = "flyway",initMethod = "migrate")
    public Flyway migrate() {
        log.debug("Flyway migration started...");
        try {
            Flyway flyway = new Flyway();
            flyway.setInitOnMigrate(true);
            flyway.setDataSource(dataSource);
            flyway.migrate();
            return flyway;
        } catch (Exception e) {
            log.error("Error while migrate database", e);
        }
        log.debug("Flyway migration ended...");
        return null;
    }

}
