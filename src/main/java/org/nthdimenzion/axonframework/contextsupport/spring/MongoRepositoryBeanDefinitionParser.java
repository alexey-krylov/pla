package org.nthdimenzion.axonframework.contextsupport.spring;
import org.axonframework.eventhandling.EventBus;
import org.nthdimenzion.axonframework.repository.GenericMongoRepository;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import static org.axonframework.contextsupport.spring.AutowiredBean.createAutowiredBean;

/**
 * Created by pradyumna on 01-04-2015.
 */
public class MongoRepositoryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private static final String MONGO_TEMPLATE = "mongo-template";
    private static final String EVENT_BUS = "event-bus";
    private static final String AGGREGATE_TYPE = "aggregate-type";

    @Override
    protected Class<?> getBeanClass(Element element) {
        return GenericMongoRepository.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        if (element.hasAttribute(MONGO_TEMPLATE)) {
            builder.addConstructorArgReference(element.getAttribute(MONGO_TEMPLATE));
        }
        builder.addConstructorArgValue(element.getAttribute(AGGREGATE_TYPE));
        parseEventBus(element, builder);
    }

    private void parseEventBus(Element element, BeanDefinitionBuilder builder) {
        if (element.hasAttribute(EVENT_BUS)) {
            builder.addPropertyReference("eventBus", element.getAttribute(EVENT_BUS));
        } else {
            builder.addPropertyValue("eventBus", createAutowiredBean(EventBus.class));
        }
    }

}
