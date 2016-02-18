package com.pla.grouphealth.claim.reimbursement.query;

import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
@Finder
@Component
public class GroupHealthReimbursementClaimFinder {
    private MongoTemplate mongoTemplate;
    @Autowired
    public void setDataSource(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
