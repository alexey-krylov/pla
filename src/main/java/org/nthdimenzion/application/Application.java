/*
 * Copyright (c) 1/22/15 8:49 PM.Nth Dimenzion, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.nthdimenzion.application;

import org.nthdimenzion.security.configuration.SecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * @author: Samir
 * @since 1.0 23/01/2015
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.pla", "org.nthdimenzion"})
@EntityScan(basePackages = {"com.pla", "org.nthdimenzion"})
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


}
