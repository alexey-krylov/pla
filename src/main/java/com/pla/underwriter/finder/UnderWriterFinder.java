package com.pla.underwriter.finder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.pla.underwriter.domain.model.UnderWriterDocument;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 5/13/2015.
 */
@Service
public class UnderWriterFinder {

    private MongoTemplate mongoTemplate;

    private ObjectMapper objectMapper;

    @Autowired
    public UnderWriterFinder(MongoTemplate mongoTemplate) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        this.mongoTemplate = mongoTemplate;
    }

    public List<Map> findAllUnderWriterDocument() {
        List<UnderWriterDocument> allUnderWriterDocument = mongoTemplate.findAll(UnderWriterDocument.class, "under_writer_document");
        List<Map> underWriterDocumentList = new ArrayList<Map>();
        for (UnderWriterDocument underWriterDocument : allUnderWriterDocument) {
            Map underWriterDocumentMap = objectMapper.convertValue(underWriterDocument, Map.class);
            underWriterDocumentList.add(underWriterDocumentMap);
        }
        return underWriterDocumentList;
    }

    public List<Map> findAllUnderWriterRoutingLevel() {
        List<UnderWriterRoutingLevel> allUnderWriterRoutingLevel = mongoTemplate.findAll(UnderWriterRoutingLevel.class, "under_writing_router");
        List<Map> underWritingRoutingLevelList = new ArrayList<Map>();
        for (UnderWriterRoutingLevel underWriterRoutingLevel : allUnderWriterRoutingLevel) {
            Map underWritingRoutingLevelMap = objectMapper.convertValue(underWriterRoutingLevel, Map.class);
            underWritingRoutingLevelList.add(underWritingRoutingLevelMap);
        }
        return underWritingRoutingLevelList;
    }

}
