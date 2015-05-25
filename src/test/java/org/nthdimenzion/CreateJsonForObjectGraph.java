package org.nthdimenzion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.pla.quotation.application.command.grouplife.UpdateGLQuotationWithAgentCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Created by IntelliJ IDEA.
 * User: Nthdimenzion
 * Date: 3/8/13
 * Time: 11:38 AM
 */
public class CreateJsonForObjectGraph {

    private static Logger logger = LoggerFactory.getLogger(CreateJsonForObjectGraph.class);

    public static void main(String[] args) throws JsonProcessingException {
        UpdateGLQuotationWithAgentCommand result1 = fillDataIntoObjectGraph(UpdateGLQuotationWithAgentCommand.class);
        ObjectMapper mapper = new ObjectMapper();
        configureMapper(mapper);
        String result = mapper.writeValueAsString(result1);
        logger.info(result);
        System.out.println(result);
    }

    private static void configureMapper(ObjectMapper mapper) {
        mapper.registerModule(new JodaModule());
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new
                MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(mapper);
    }

    private static <T> T fillDataIntoObjectGraph(Class<T> clazz) {
        PodamFactory factory = new PodamFactoryImpl(); //This will use the default Random Data Provider Strategy
        T objectGraph = factory.manufacturePojo(clazz);
        return objectGraph;

    }
}
