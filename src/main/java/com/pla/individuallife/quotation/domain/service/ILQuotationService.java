package com.pla.individuallife.quotation.domain.service;

import com.google.common.base.Preconditions;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.quotation.domain.model.*;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.axonframework.repository.Repository;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private Repository<ILQuotation> ilQuotationRepository;

    /**
     * @param quotationProcessor
     * @param quotationId
     * @param agentId
     * @param proposedAssured
     * @param planId
     * @return
     */
    public void createQuotation(ILQuotationProcessor quotationProcessor, QuotationId quotationId,
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
        ilQuotationRepository.add(quotation);
    }

    /**
     *
     * @param quotationProcessor
     * @param quotation
     * @param proposer
     * @return
     */
    public QuotationId updateProposerWithVersion(ILQuotationProcessor quotationProcessor,
                                                 ILQuotation quotation, Proposer proposer) {
        Preconditions.checkArgument(quotationProcessor != null);
        Preconditions.checkState(quotation.requireVersioning());
        QuotationId newQuotationId = new QuotationId(idGenerator.nextId());
        ILQuotation newQuotation = quotation.cloneQuotation(quotationProcessor, newQuotationId, -1);
        newQuotation.updateWithProposer(quotationProcessor, proposer);
        ilQuotationRepository.add(newQuotation);
        return newQuotationId;
    }


    /**
     * Create a cloned quotation with next version and updated the ProposedAssured Details.
     *
     * @param quotationProcessor
     * @param quotation
     * @param proposedAssured
     * @param isAssuredTheProposer
     * @return
     */
    public QuotationId updateAssuredDetailWithVersion(ILQuotationProcessor quotationProcessor,
                                                      ILQuotation quotation, ProposedAssured proposedAssured,
                                                      boolean isAssuredTheProposer) {
        Preconditions.checkArgument(quotationProcessor != null);
        Preconditions.checkState(quotation.requireVersioning());
        QuotationId newQuotationId = new QuotationId(idGenerator.nextId());
        ILQuotation newQuotation = quotation.cloneQuotation(quotationProcessor, newQuotationId, -1);
        newQuotation.updateWithAssured(quotationProcessor, proposedAssured, isAssuredTheProposer);
        ilQuotationRepository.add(newQuotation);
        return newQuotationId;
    }

    public QuotationId updateWithPlanWithVersion(ILQuotationProcessor quotationProcessor,
                                                 ILQuotation quotation,
                                                 PlanDetail planDetail, Set<RiderDetail> riders) {
        Preconditions.checkArgument(quotationProcessor != null);
        Preconditions.checkState(quotation.requireVersioning());
        QuotationId newQuotationId = new QuotationId(idGenerator.nextId());
        ILQuotation newQuotation = quotation.cloneQuotation(quotationProcessor, newQuotationId, -1);
        newQuotation.updateWithPlanAndRider(quotationProcessor, planDetail, riders);
        ilQuotationRepository.add(newQuotation);
        return newQuotationId;
    }


}
