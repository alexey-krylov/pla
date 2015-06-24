package com.pla.individuallife.proposal.repository;

import com.pla.sharedkernel.identifier.ProposalId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * Created by Prasant on 04-Jun-15.
 */
public class ProposalIdToObjectIdConverter implements Converter<ProposalId, ObjectId>
{
    @Override
    public ObjectId convert(ProposalId source) {
        System.out.println(" Object to proposal id invoked");
        return StringUtils.hasText(source.getProposalId()) ? new ObjectId(source.getProposalId()) : null;
    }
}
