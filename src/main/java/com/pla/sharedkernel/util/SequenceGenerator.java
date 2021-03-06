/*
 * Copyright (c) 3/23/15 2:28 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.util;

import com.pla.core.query.MasterFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.Map;

/**
 * @author: Samir
 * @since 1.0 23/03/2015
 */
@Component
public class SequenceGenerator {

    public static final String UPDATE_ENTITY_SEQUENCE_QUERY = "UPDATE entity_sequence ES set ES.sequence_number=? where ES.sequence_id=?";
    private MasterFinder masterFinder;
    private JdbcTemplate jdbcTemplate;


    @Autowired
    public SequenceGenerator(MasterFinder masterFinder) {
        this.masterFinder = masterFinder;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Transactional
    public String getSequence(Class clazz) {
        Map<String, Object> entitySequence = getEntitySequenceMap(clazz);
        if (entitySequence == null) {
            throw new RuntimeException("Entity sequence not found for class:" + clazz);
        }
        Integer sequenceNumber = ((Integer) entitySequence.get("sequenceNumber")) + 1;
        String sequence = (String) entitySequence.get("sequencePrefix") + sequenceNumber;
        updateSequence(sequenceNumber, (Integer) entitySequence.get("sequenceId"));
        //jdbcTemplate.update(UPDATE_ENTITY_SEQUENCE_QUERY, new Object[]{sequenceNumber, entitySequence.get("sequenceId")});
        return sequence.trim();
    }

    public Map<String,Object> getEntitySequenceMap(Class clazz){
        Map<String, Object> entitySequence = masterFinder.getEntitySequenceFor(clazz);
        return entitySequence;
    }

    public void updateSequence(Integer sequenceNumber, Integer sequenceId) {
        jdbcTemplate.update(UPDATE_ENTITY_SEQUENCE_QUERY, new Object[]{sequenceNumber, sequenceId});
    }
}
