package com.pla.grouplife.endorsement.query;

import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Samir on 8/11/2015.
 */
@Service
public class GLEndorsementFinder {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Map findEndorsementById(String endorsementId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", endorsementId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "group_life_endorsement");
        return proposal;
    }
}
