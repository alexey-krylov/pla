package com.pla.individuallife.quotation.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.quotation.domain.model.IQuotation;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.pla.individuallife.quotation.domain.exception.QuotationException.raiseQuotationNotModifiableException;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Entity
@Table(name = "individual_life_quotation")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class IndividualLifeQuotation extends AbstractAggregateRoot<QuotationId> implements IQuotation {


    @EmbeddedId
    @AggregateIdentifier
    private QuotationId quotationId;

    private String quotationCreator;

    @Embedded
    private AgentId agentId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "proposedFirstName")),
            @AttributeOverride(name = "surname", column = @Column(name = "proposerSurname")),
            @AttributeOverride(name = "nrcNumber", column = @Column(name = "proposerNrcNumber")),
            @AttributeOverride(name = "mobileNumber", column = @Column(name = "proposerMobileNumber")),
            @AttributeOverride(name = "emailAddress", column = @Column(name = "proposerEmailAddress")),
            @AttributeOverride(name = "gender", column = @Column(name = "proposerGender")),
            @AttributeOverride(name = "title", column = @Column(name = "proposerTitle")),
            @AttributeOverride(name = "dateOfBirth", column = @Column(name = "proposerDateOfBirth")),
    })
    private Proposer proposer;

    @Embedded

    private ProposedAssured proposedAssured;

    @Enumerated(EnumType.STRING)
    private ILQuotationStatus ilQuotationStatus;

    private int versionNumber;

    private String quotationNumber;

    private LocalDate generatedOn;

    @Embedded
    @AttributeOverrides(value = @AttributeOverride(name = "quotationId", column = @Column(name = "PARENT_QUOTATION_ID")))
    private QuotationId parentQuotationId;

    @Embedded
    private PlanDetail planDetail;

    @Column( columnDefinition = "BOOLEAN DEFAULT false" )
    private Boolean isAssuredTheProposer;

    @ElementCollection
    private Set<RiderDetail> riderDetails;

    private IndividualLifeQuotation(String quotationCreator, String quotationNumber, QuotationId quotationId, AgentId agentId, ProposedAssured proposedAssured, PlanId planId, ILQuotationStatus ilQuotationStatus, int versionNumber) {
        checkArgument(isNotEmpty(quotationCreator));
        checkArgument(isNotEmpty(quotationNumber));
        checkArgument(quotationId != null);
        checkArgument(isNotEmpty(quotationId.getQuotationId()));
        checkArgument(agentId != null);
        checkArgument(proposedAssured != null);
        checkArgument(proposedAssured.getFirstName() != null);
        checkArgument(proposedAssured.getSurname() != null);
        checkArgument(proposedAssured.getTitle() != null);
        checkArgument(proposedAssured.getNrcNumber() != null);
        checkArgument(planId != null);
        checkArgument(ILQuotationStatus.DRAFT.equals(ilQuotationStatus));
        this.quotationCreator = quotationCreator;
        this.quotationId = quotationId;
        this.agentId = agentId;
        this.proposedAssured = proposedAssured;
        this.ilQuotationStatus = ilQuotationStatus;
        this.versionNumber = versionNumber;
        this.quotationNumber = quotationNumber;
        this.planDetail = new PlanDetail(planId, null, null, null);
    }

    private IndividualLifeQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId, int versionNumber, ILQuotationStatus ilQuotationStatus) {
        checkArgument(isNotEmpty(quotationNumber));
        checkArgument(isNotEmpty(quotationCreator));
        checkArgument(quotationId != null);
        checkArgument(ILQuotationStatus.DRAFT.equals(ilQuotationStatus));
        this.quotationCreator = quotationCreator;
        this.quotationId = quotationId;
        this.versionNumber = versionNumber;
        this.ilQuotationStatus = ilQuotationStatus;
    }

    public static IndividualLifeQuotation createWithBasicDetail(String quotationNumber, String quotationCreator, QuotationId quotationId, AgentId agentId, ProposedAssured proposedAssured, PlanId planid) {
        return new IndividualLifeQuotation(quotationCreator, quotationNumber, quotationId, agentId, proposedAssured, planid, ILQuotationStatus.DRAFT, 0);
    }

    public IndividualLifeQuotation updateWithProposer(Proposer proposer, AgentId agentId) {
        checkInvariant();
        this.proposer = proposer;
        this.agentId = agentId;
        return this;
    }

    public IndividualLifeQuotation updateWithAssured(ProposedAssured proposedAssured, Boolean isAssuredTheProposer) {
        checkInvariant();
        this.proposedAssured = proposedAssured;
        this.isAssuredTheProposer = isAssuredTheProposer;

        if (isAssuredTheProposer) {
            this.proposer = convertAssuredToProposer(proposedAssured);
        }
        return this;
    }

    public IndividualLifeQuotation updateWithPlan(PlanDetail planDetail) {
        checkInvariant();
        this.planDetail = planDetail;
        return this;
    }

    @Override
    public QuotationId getIdentifier() {
        return quotationId;
    }

    @Override
    public void closeQuotation() {
        this.ilQuotationStatus = ILQuotationStatus.CLOSED;
    }

    @Override
    public void purgeQuotation() {
        this.ilQuotationStatus = ILQuotationStatus.PURGED;
    }

    @Override
    public void declineQuotation() {
        this.ilQuotationStatus = ILQuotationStatus.DECLINED;
    }

    @Override
    public void generateQuotation(LocalDate generatedOn) {
        this.ilQuotationStatus = ILQuotationStatus.GENERATED;
        this.generatedOn = generatedOn;
    }

    @Override
    public boolean requireVersioning() {
        return ILQuotationStatus.GENERATED.equals(this.ilQuotationStatus);
    }

    public IndividualLifeQuotation cloneQuotation(String quotationCreator, QuotationId quotationId) {
        IndividualLifeQuotation newQuotation = new IndividualLifeQuotation(quotationCreator, quotationNumber, quotationId, this.versionNumber + 1, ILQuotationStatus.DRAFT);
        newQuotation.parentQuotationId = this.quotationId;
        newQuotation.proposer = this.proposer;
        newQuotation.agentId = this.agentId;
        newQuotation.proposedAssured = this.proposedAssured;
        return newQuotation;
    }

    private void checkInvariant() {
        if (ILQuotationStatus.CLOSED.equals(this.ilQuotationStatus) || ILQuotationStatus.DECLINED.equals(this.ilQuotationStatus)) {
            raiseQuotationNotModifiableException();
        }
    }

    private Proposer convertAssuredToProposer(ProposedAssured proposedAssured) {
        Proposer proposer = new Proposer(proposedAssured.getTitle(), proposedAssured.getFirstName(), proposedAssured.getSurname(), proposedAssured.getNrcNumber(), proposedAssured.getDateOfBirth(), proposedAssured.getGender(), proposedAssured.getMobileNumber(), proposedAssured.getEmailAddress());
        return proposer;
    }

}
