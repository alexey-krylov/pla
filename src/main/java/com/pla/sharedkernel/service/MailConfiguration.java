package com.pla.sharedkernel.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Samir on 5/27/2015.
 */
@Configuration
public class MailConfiguration {

    @Bean
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        Properties properties = new Properties();
        ClassLoader bundleClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            properties.load(bundleClassLoader.getResourceAsStream("mailsettings.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        javaMailSender.setUsername(properties.getProperty("mail.user"));
        javaMailSender.setPassword(properties.getProperty("mail.password"));
        javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;
    }

    @Bean
    public VelocityEngineFactoryBean velocityEngine() {
        VelocityEngineFactoryBean velocityEngine = new VelocityEngineFactoryBean();
        Properties velocityProperties = new Properties();
        velocityProperties.put("resource.loader", "class");
        velocityProperties.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.setVelocityProperties(velocityProperties);
        return velocityEngine;
    }
}
