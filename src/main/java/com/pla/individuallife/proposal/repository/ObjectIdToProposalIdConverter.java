package com.pla.individuallife.proposal.repository;

import com.pla.sharedkernel.identifier.ProposalId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by Prasant on 04-Jun-15.
 */
public class ObjectIdToProposalIdConverter implements Converter<ObjectId, ProposalId> {

    @Override
    public ProposalId convert(ObjectId id) {
        System.out.println(" Object to proposal id invoked");
        return id == null ? null : new ProposalId(id.toString());
    }
}
