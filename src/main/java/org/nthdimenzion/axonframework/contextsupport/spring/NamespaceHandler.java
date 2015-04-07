package org.nthdimenzion.axonframework.contextsupport.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by pradyumna on 01-04-2015.
 */
public class NamespaceHandler extends NamespaceHandlerSupport {
    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        registerBeanDefinitionParser("mongo-repository", new MongoRepositoryBeanDefinitionParser());
    }
}
