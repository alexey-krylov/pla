package com.pla.individuallife.quotation.domain.service;

import com.google.common.base.Preconditions;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.quotation.domain.model.*;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.UUID;

/**
 * Created by Karunakar on 5/13/2015.
 */
@DomainService
public class ILQuotationService {

    @Autowired
    private ILQuotationNumberGenerator ilQuotationNumberGenerator;
    @Autowired
    private IIdGenerator idGenerator;

    /**
     * @param quotationProcessor
     * @param quotationId
     * @param agentId
     * @param proposedAssured
     * @param planId
     * @return
     */
    public ILQuotationAR createQuotation(ILQuotationProcessor quotationProcessor, QuotationId quotationId,
                                         AgentId agentId, ProposedAssured proposedAssured, PlanId planId) {
        Preconditions.checkArgument(quotationId != null, "Quotation Id is empty.");
        Preconditions.checkArgument(agentId != null, "Agent Id is empty.");
        Preconditions.checkArgument(planId != null, "Plan Id is empty.");
        String quotationNumber = ilQuotationNumberGenerator.getQuotationNumber(ILQuotation.class);
        String quotationARId = UUID.randomUUID().toString();
        ILQuotation quotation =
                new ILQuotation(quotationProcessor, quotationARId,
                        quotationNumber, quotationId, agentId, proposedAssured, planId,
                        ILQuotationStatus.DRAFT, 0);
        ILQuotationAR quotationAR = new ILQuotationAR(quotationARId, quotation);
        return quotationAR;
    }

    /**
     * @param quotationAR
     * @param quotation
     * @param proposer
     * @param userDetails
     * @return
     */
    public QuotationId updateProposerWithVersion(ILQuotationAR quotationAR,
                                                 ILQuotation quotation, Proposer proposer, UserDetails userDetails) {
        ILQuotationProcessor quotationProcessor = ILQuotationRoleAdapter.userToQuotationProcessor(userDetails);
        Preconditions.checkState(quotation.requireVersioning());
        QuotationId newQuotationId = new QuotationId(idGenerator.nextId());
        String quotationNumber = ilQuotationNumberGenerator.getQuotationNumber(ILQuotation.class);
        ILQuotation newQuotation = quotationAR.nextVersion(quotationProcessor, quotation, newQuotationId, quotationNumber);
        newQuotation.updateWithProposer(quotationProcessor, proposer);
        return newQuotationId;
    }


    /**
     * Create a cloned quotation with next version and updated the ProposedAssured Details.
     *
     * @param quotationProcessor
     * @param quotationAR
     * @param quotation
     * @param proposedAssured
     * @param isAssuredTheProposer
     * @return
     */
    public QuotationId updateAssuredDetailWithVersion(ILQuotationProcessor quotationProcessor,
                                                      ILQuotationAR quotationAR, ILQuotation quotation, ProposedAssured proposedAssured, boolean isAssuredTheProposer) {
        Preconditions.checkArgument(quotationProcessor != null);
        Preconditions.checkState(quotation.requireVersioning());
        QuotationId newQuotationId = new QuotationId(idGenerator.nextId());
        String quotationNumber = ilQuotationNumberGenerator.getQuotationNumber(ILQuotation.class);
        ILQuotation newQuotation = quotationAR.nextVersion(quotationProcessor, quotation, newQuotationId, quotationNumber);
        newQuotation.updateWithAssured(quotationProcessor, proposedAssured, isAssuredTheProposer);
        return newQuotationId;
    }

    public QuotationId updateWithPlanWithVersion(ILQuotationProcessor quotationProcessor,
                                                 ILQuotationAR quotationAR, ILQuotation quotation,
                                                 PlanDetail planDetail, Set<RiderDetail> riders) {
        Preconditions.checkArgument(quotationProcessor != null);
        Preconditions.checkState(quotation.requireVersioning());
        QuotationId newQuotationId = new QuotationId(idGenerator.nextId());
        String quotationNumber = ilQuotationNumberGenerator.getQuotationNumber(ILQuotation.class);
        ILQuotation newQuotation = quotationAR.nextVersion(quotationProcessor, quotation, newQuotationId, quotationNumber);
        newQuotation.updateWithPlan(quotationProcessor, planDetail, riders);
        return newQuotationId;
    }


}
