package com.pla.quotation.domain.model.grouplife;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.quotation.domain.event.ProposerAddedEvent;
import com.pla.quotation.domain.event.QuotationClosedEvent;
import com.pla.quotation.domain.event.QuotationGeneratedEvent;
import com.pla.quotation.domain.model.IQuotation;
import com.pla.quotation.domain.model.QuotationStatus;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.pla.quotation.domain.exception.QuotationException.raiseQuotationNotModifiableException;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/7/2015.
 */
@Document(collection = "group_life_quotation")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupLifeQuotation extends AbstractAggregateRoot<QuotationId> implements IQuotation {

    @Id
    @AggregateIdentifier
    private QuotationId quotationId;

    private String quotationCreator;

    private AgentId agentId;

    private Proposer proposer;

    private Set<Insured> insureds;

    private Set<Policy> policies;

    private QuotationStatus quotationStatus;

    private int versionNumber;

    @Getter
    private String quotationNumber;

    private LocalDate generatedOn;

    private QuotationId parentQuotationId;

    private GroupLifeQuotation(String quotationCreator, String quotationNumber, QuotationId quotationId, AgentId agentId, Proposer proposer, QuotationStatus quotationStatus, int versionNumber) {
        checkArgument(isNotEmpty(quotationCreator));
        checkArgument(isNotEmpty(quotationNumber));
        checkArgument(quotationId != null);
        checkArgument(agentId != null);
        checkArgument(proposer != null);
        checkArgument(QuotationStatus.DRAFT.equals(quotationStatus));
        this.quotationCreator = quotationCreator;
        this.quotationId = quotationId;
        this.agentId = agentId;
        this.proposer = proposer;
        this.quotationStatus = quotationStatus;
        this.versionNumber = versionNumber;
        this.quotationNumber = quotationNumber;
    }

    private GroupLifeQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId, int versionNumber, QuotationStatus quotationStatus) {
        checkArgument(isNotEmpty(quotationNumber));
        checkArgument(isNotEmpty(quotationCreator));
        checkArgument(quotationId != null);
        checkArgument(QuotationStatus.DRAFT.equals(quotationStatus));
        this.quotationCreator = quotationCreator;
        this.quotationId = quotationId;
        this.versionNumber = versionNumber;
        this.quotationStatus = quotationStatus;
    }

    public static GroupLifeQuotation createWithAgentAndProposerDetail(String quotationNumber, String quotationCreator, QuotationId quotationId, AgentId agentId, Proposer proposer) {
        return new GroupLifeQuotation(quotationCreator, quotationNumber, quotationId, agentId, proposer, QuotationStatus.DRAFT, 0);
    }

    public GroupLifeQuotation updateWithPolicy(Set<Policy> policies) {
        checkInvariant();
        this.policies = policies;
        return this;
    }

    public GroupLifeQuotation updateWithAgent(AgentId agentId) {
        checkInvariant();
        this.agentId = agentId;
        return this;
    }

    public GroupLifeQuotation updateWithProposer(Proposer proposer) {
        checkInvariant();
        this.proposer = proposer;
        return this;
    }

    public GroupLifeQuotation updateWithInsured(Set<Insured> insureds) {
        checkInvariant();
        this.insureds = insureds;
        return this;
    }

    @Override
    public QuotationId getIdentifier() {
        return quotationId;
    }

    @Override
    public void closeQuotation() {
        this.quotationStatus = QuotationStatus.CLOSED;
        registerEvent(new QuotationClosedEvent(quotationId));
    }

    @Override
    public void inactiveQuotation() {
        this.quotationStatus = QuotationStatus.INACTIVE;
    }

    @Override
    public void declineQuotation() {
        this.quotationStatus = QuotationStatus.DECLINED;
    }

    @Override
    public void generateQuotation(LocalDate generatedOn) {
        this.quotationStatus = QuotationStatus.GENERATED;
        this.generatedOn = generatedOn;
        registerEvent(new QuotationGeneratedEvent(quotationId));
        registerEvent(new ProposerAddedEvent());
    }

    @Override
    public boolean requireVersioning() {
        return QuotationStatus.GENERATED.equals(this.quotationStatus);
    }

    public GroupLifeQuotation cloneQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId) {
        GroupLifeQuotation newQuotation = new GroupLifeQuotation(quotationCreator, quotationNumber, quotationId, this.versionNumber + 1, QuotationStatus.DRAFT);
        newQuotation.parentQuotationId = this.quotationId;
        newQuotation.proposer = this.proposer;
        newQuotation.agentId = this.agentId;
        newQuotation.insureds = this.insureds;
        newQuotation.policies = this.policies;
        return newQuotation;
    }


    private void checkInvariant() {
        if (QuotationStatus.CLOSED.equals(this.quotationStatus) || QuotationStatus.DECLINED.equals(this.quotationStatus)) {
            raiseQuotationNotModifiableException();
        }
    }
}
