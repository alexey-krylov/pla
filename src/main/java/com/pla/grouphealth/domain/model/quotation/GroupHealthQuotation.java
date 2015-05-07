package com.pla.grouphealth.domain.model.quotation;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.domain.event.quotation.ProposerAddedEvent;
import com.pla.grouphealth.domain.event.quotation.QuotationClosedEvent;
import com.pla.grouphealth.domain.event.quotation.QuotationGeneratedEvent;
import com.pla.quotation.domain.model.IQuotation;
import com.pla.grouphealth.domain.model.GHQuotationStatus;
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
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
import static com.pla.grouphealth.domain.exception.QuotationException.raiseQuotationNotModifiableException;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Document(collection = "group_health_quotation")
@Getter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupHealthQuotation extends AbstractAggregateRoot<QuotationId> implements IQuotation {

    @Id
    @AggregateIdentifier
    private QuotationId quotationId;

    private String quotationCreator;

    private AgentId agentId;

    private Proposer proposer;

    private Set<Insured> insureds;

    private GHQuotationStatus ghQuotationStatus;

    private int versionNumber;

    @Getter
    private String quotationNumber;

    private LocalDate generatedOn;

    private QuotationId parentQuotationId;

    private PremiumDetail premiumDetail;

    private GroupHealthQuotation(String quotationCreator, String quotationNumber, QuotationId quotationId, AgentId agentId, Proposer proposer, GHQuotationStatus ghQuotationStatus, int versionNumber) {
        checkArgument(isNotEmpty(quotationCreator));
        checkArgument(isNotEmpty(quotationNumber));
        checkArgument(quotationId != null);
        checkArgument(agentId != null);
        checkArgument(proposer != null);
        checkArgument(GHQuotationStatus.DRAFT.equals(ghQuotationStatus));
        this.quotationCreator = quotationCreator;
        this.quotationId = quotationId;
        this.agentId = agentId;
        this.proposer = proposer;
        this.ghQuotationStatus = ghQuotationStatus;
        this.versionNumber = versionNumber;
        this.quotationNumber = quotationNumber;
    }

    private GroupHealthQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId, int versionNumber, GHQuotationStatus ghQuotationStatus) {
        checkArgument(isNotEmpty(quotationNumber));
        checkArgument(isNotEmpty(quotationCreator));
        checkArgument(quotationId != null);
        checkArgument(GHQuotationStatus.DRAFT.equals(ghQuotationStatus));
        this.quotationCreator = quotationCreator;
        this.quotationId = quotationId;
        this.versionNumber = versionNumber;
        this.ghQuotationStatus = ghQuotationStatus;
    }

    public static GroupHealthQuotation createWithAgentAndProposerDetail(String quotationNumber, String quotationCreator, QuotationId quotationId, AgentId agentId, Proposer proposer) {
        return new GroupHealthQuotation(quotationCreator, quotationNumber, quotationId, agentId, proposer, GHQuotationStatus.DRAFT, 0);
    }

    public GroupHealthQuotation updateWithPremiumDetail(PremiumDetail premiumDetail) {
        checkInvariant();
        this.premiumDetail = premiumDetail;
        return this;
    }

    public GroupHealthQuotation updateWithAgent(AgentId agentId) {
        checkInvariant();
        this.agentId = agentId;
        return this;
    }

    public GroupHealthQuotation updateWithProposer(Proposer proposer) {
        checkInvariant();
        this.proposer = proposer;
        return this;
    }

    public GroupHealthQuotation updateWithInsured(Set<Insured> insureds) {
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
        this.ghQuotationStatus = GHQuotationStatus.CLOSED;
        registerEvent(new QuotationClosedEvent(quotationId));
    }

    @Override
    public void inactiveQuotation() {
        this.ghQuotationStatus = GHQuotationStatus.INACTIVE;
    }

    @Override
    public void declineQuotation() {
        this.ghQuotationStatus = GHQuotationStatus.DECLINED;
    }

    @Override
    public void generateQuotation(LocalDate generatedOn) {
        this.ghQuotationStatus = GHQuotationStatus.GENERATED;
        this.generatedOn = generatedOn;
        if (proposer != null && proposer.getContactDetail() != null) {
            ProposerContactDetail proposerContactDetail = proposer.getContactDetail();
            registerEvent(new ProposerAddedEvent(proposer.getProposerName(), proposer.getProposerCode(),
                    proposerContactDetail.getAddressLine1(), proposerContactDetail.getAddressLine2(), proposerContactDetail.getPostalCode(),
                    proposerContactDetail.getProvince(), proposerContactDetail.getTown(), proposerContactDetail.getEmailAddress()));
            registerEvent(new QuotationGeneratedEvent(quotationId));
        }
    }

    @Override
    public boolean requireVersioning() {
        return GHQuotationStatus.GENERATED.equals(this.ghQuotationStatus);
    }

    public GroupHealthQuotation cloneQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId) {
        GroupHealthQuotation newQuotation = new GroupHealthQuotation(quotationCreator, quotationNumber, quotationId, this.versionNumber + 1, GHQuotationStatus.DRAFT);
        newQuotation.parentQuotationId = this.quotationId;
        newQuotation.proposer = this.proposer;
        newQuotation.agentId = this.agentId;
        newQuotation.insureds = this.insureds;
        newQuotation.premiumDetail = this.premiumDetail;
        return newQuotation;
    }

    private void checkInvariant() {
        if (GHQuotationStatus.CLOSED.equals(this.ghQuotationStatus) || GHQuotationStatus.DECLINED.equals(this.ghQuotationStatus)) {
            raiseQuotationNotModifiableException();
        }
    }
}
