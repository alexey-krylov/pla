package com.pla.individuallife.quotation.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.quotation.domain.model.IQuotation;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.pla.individuallife.quotation.domain.exception.QuotationException.raiseQuotationNotModifiableException;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Entity
@Table (name = "individual_life_quotation")
@Getter(value = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@DynamicInsert
@DynamicUpdate
public class IndividualLifeQuotation extends AbstractAggregateRoot<QuotationId> implements IQuotation, ICrudEntity {


    @EmbeddedId
    @AggregateIdentifier
    private QuotationId quotationId;

    private String quotationCreator;

    private AgentId agentId;

    @OneToOne(targetEntity = Proposer.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "quotation_proposer", joinColumns = @JoinColumn(name = "QUOTATION_ID"), inverseJoinColumns = @JoinColumn(name = "PROPOSER_ID"))
    private Proposer proposer;

    @OneToOne(targetEntity = ProposedAssured.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "quotation_assured", joinColumns = @JoinColumn(name = "QUOTATION_ID"), inverseJoinColumns = @JoinColumn(name = "ASSURED_ID"))
    private ProposedAssured proposedAssured;

    @Enumerated(EnumType.STRING)
    private ILQuotationStatus ilQuotationStatus;

    private int versionNumber;

    private String quotationNumber;

    private LocalDate generatedOn;

    @Transient
    private QuotationId parentQuotationId;

    private PlanId  planid;

    @Column( nullable = false, columnDefinition = "BOOLEAN DEFAULT false" )
    private Boolean isAssuredTheProposer;

    @OneToOne(targetEntity = PlanDetail.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "quotation_plandetail", joinColumns = @JoinColumn(name = "QUOTATION_ID"), inverseJoinColumns = @JoinColumn(name = "PLAN_DETAIL_ID"))
    private PlanDetail planDetail;


    private IndividualLifeQuotation(String quotationCreator, String quotationNumber, QuotationId quotationId, AgentId agentId, ProposedAssured proposedAssured, PlanId planId,  ILQuotationStatus ilQuotationStatus, int versionNumber) {
        checkArgument(isNotEmpty(quotationCreator));
        checkArgument(isNotEmpty(quotationNumber));
        checkArgument(quotationId != null);
        checkArgument(isNotEmpty(quotationId.getQuotationId()));
        checkArgument(agentId != null);
        checkArgument(proposedAssured != null);
        checkArgument(proposedAssured.getAssuredFName() != null);
        checkArgument(proposedAssured.getAssuredSurname() != null);
        checkArgument(proposedAssured.getAssuredTitle() != null);
        checkArgument(proposedAssured.getAssuredNRC() != null);
        checkArgument(planId != null);
        checkArgument(ILQuotationStatus.DRAFT.equals(ilQuotationStatus));
        this.quotationCreator = quotationCreator;
        this.quotationId = quotationId;
        this.agentId = agentId;
        this.proposedAssured = proposedAssured;
        this.planid = planId;
        this.ilQuotationStatus = ilQuotationStatus;
        this.versionNumber = versionNumber;
        this.quotationNumber = quotationNumber;
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

    public static IndividualLifeQuotation createWithBasicDetail(String quotationNumber, String quotationCreator, QuotationId quotationId,  AgentId agentId, ProposedAssured proposedAssured, PlanId planid) {
        return new IndividualLifeQuotation(quotationCreator, quotationNumber, quotationId, agentId, proposedAssured, planid, ILQuotationStatus.DRAFT, 0);
    }

    public IndividualLifeQuotation updateWithAgent(AgentId agentId) {
        checkInvariant();
        this.agentId = agentId;
        return this;
    }

    public IndividualLifeQuotation updateWithProposer(Proposer proposer, AgentId agentId , String proposerId) {
        checkInvariant();
        if( this.proposer != null)
            proposerId = this.proposer.getProposerId();
        proposer.setProposerId(proposerId);
        this.proposer = proposer;
        this.agentId = agentId;
        if(isAssuredTheProposer) {
            raiseQuotationNotModifiableException();

        }
        return this;
    }

    public IndividualLifeQuotation updateWithAssured(ProposedAssured proposedAssured, Boolean isAssuredTheProposer, String proposerId) {
        checkInvariant();
        proposedAssured.setAssuredId(this.proposedAssured.getAssuredId());
        this.proposedAssured = proposedAssured;
        this.isAssuredTheProposer = isAssuredTheProposer;
        if(isAssuredTheProposer) {
            if (proposerId == null) proposerId = this.proposer.getProposerId();
            this.proposer = convertAssuredToProposer(proposedAssured);
                this.proposer.setProposerId(proposerId);

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
    public void inactiveQuotation() {
        this.ilQuotationStatus = ILQuotationStatus.INACTIVE;
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

    public IndividualLifeQuotation cloneQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId) {
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
        ProposerBuilder proposerBuilder= Proposer.getProposerBuilder(null, proposedAssured.getAssuredTitle(), proposedAssured.getAssuredFName(), proposedAssured.getAssuredSurname(), proposedAssured.getAssuredNRC(), proposedAssured.getDateOfBirth(), proposedAssured.getAgeNextBirthDay(), proposedAssured.getGender(), proposedAssured.getMobileNumber(), proposedAssured.getEmailId());
        return new Proposer(proposerBuilder);
    }

}
