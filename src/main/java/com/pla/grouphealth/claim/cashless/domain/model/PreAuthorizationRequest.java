package com.pla.grouphealth.claim.cashless.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.domain.event.PreAuthorizationFollowUpReminderEvent;
import com.pla.grouphealth.claim.cashless.domain.exception.GenerateReminderFollowupException;
import com.pla.grouphealth.claim.cashless.presentation.dto.*;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerDocument;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.beanutils.BeanUtils;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 1/9/2016.
 */
@Document(collection = "PRE_AUTHORIZATION_REQUEST")
@Getter
public class PreAuthorizationRequest extends AbstractAggregateRoot<String> {

    @AggregateIdentifier
    @Id
    private String preAuthorizationRequestId;
    private PreAuthorizationId preAuthorizationId;
    private String category;
    private String relationship;
    private String claimType;
    private LocalDate claimIntimationDate;
    private String batchNumber;
    private String batchUploaderUserId;
    private GHProposer ghProposer;
    private PreAuthorizationRequestPolicyDetail preAuthorizationRequestPolicyDetail;
    private PreAuthorizationRequestHCPDetail preAuthorizationRequestHCPDetail;
    private Set<PreAuthorizationRequestDiagnosisTreatmentDetail> preAuthorizationRequestDiagnosisTreatmentDetails;
    private PreAuthorizationRequestIllnessDetail preAuthorizationRequestIllnessDetail;
    private Set<PreAuthorizationRequestDrugService> preAuthorizationRequestDrugServices;
    private Status status;
    private Set<GHProposerDocument> proposerDocuments;
    private DateTime createdOn;
    private Map<String, ScheduleToken> scheduledTokens;
    private boolean firstReminderSent;
    private boolean secondReminderSent;
    private Set<CommentDetail> commentDetails;
    private LocalDate preAuthorizationDate;
    private boolean submitted;
    private String preAuthorizationProcessorUserId;
    private String preAuthorizationUnderWriterUserId;

    public PreAuthorizationRequest(Status status){
        this.status = status;
        this.createdOn = new DateTime();
    }

    public PreAuthorizationRequest updateWithProposerDetail(ClaimantPolicyDetailDto claimantPolicyDetailDto) {
        if(isNotEmpty(claimantPolicyDetailDto)) {
            GHProposer ghProposer = isNotEmpty(this.getGhProposer()) ? this.getGhProposer() : getInstance(GHProposer.class);
            ghProposer.updateWithProposerDetails(claimantPolicyDetailDto.getPreAuthorizationClaimantProposerDetail());
            this.ghProposer = ghProposer;
        }
        return this;
    }

    public PreAuthorizationRequest updateWithPreAuthorizationRequestId(String preAuthorizationId) {
        if(isEmpty(this.preAuthorizationRequestId))
            this.preAuthorizationRequestId = preAuthorizationId;
        return this;
    }

    private <T> T getInstance(Class<T> tClass) {
        try {
            Constructor<T> constructor = tClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PreAuthorizationRequest updateWithCategory(ClaimantPolicyDetailDto claimantPolicyDetailDto) {
        if(isNotEmpty(claimantPolicyDetailDto)){
            this.category = claimantPolicyDetailDto.getCategory();
        }
        return this;
    }

    public PreAuthorizationRequest updateWithRelationship(ClaimantPolicyDetailDto claimantPolicyDetailDto) {
        if(isNotEmpty(claimantPolicyDetailDto)){
            this.relationship = claimantPolicyDetailDto.getRelationship();
        }
        return this;
    }

    public PreAuthorizationRequest updateWithClaimType(String claimType) {
        if(isNotEmpty(claimType))
            this.claimType = claimType;
        return this;
    }

    public PreAuthorizationRequest updateWithClaimIntimationDate(LocalDate claimIntimationDate) {
        if(isNotEmpty(claimIntimationDate))
            this.claimIntimationDate = claimIntimationDate;
        return this;
    }

    public PreAuthorizationRequest updateWithBatchNumber(String batchNumber) {
        if(isNotEmpty(batchNumber))
            this.batchNumber = batchNumber;
        return this;
    }

    public PreAuthorizationRequest updateWithPreAuthorizationRequestPolicyDetail(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) {
        PreAuthorizationRequestPolicyDetail preAuthorizationRequestPolicyDetail = isNotEmpty(this.preAuthorizationRequestPolicyDetail) ? this.preAuthorizationRequestPolicyDetail : new PreAuthorizationRequestPolicyDetail();
        this.preAuthorizationRequestPolicyDetail = preAuthorizationRequestPolicyDetail.updateWithDetails(preAuthorizationClaimantDetailCommand);
        return this;
    }

    public PreAuthorizationRequest updateWithPreAuthorizationRequestHCPDetail(ClaimantHCPDetailDto claimantHCPDetailDto) {
        PreAuthorizationRequestHCPDetail preAuthorizationRequestHCPDetail =  isNotEmpty(this.preAuthorizationRequestHCPDetail) ? this.preAuthorizationRequestHCPDetail : new PreAuthorizationRequestHCPDetail();
        this.preAuthorizationRequestHCPDetail = preAuthorizationRequestHCPDetail.updateWithDetails(claimantHCPDetailDto);
        return this;
    }

    public PreAuthorizationRequest updateWithPreAuthorizationRequestDiagnosisTreatmentDetail(List<DiagnosisTreatmentDto> diagnosisTreatmentDtos) {
        this.preAuthorizationRequestDiagnosisTreatmentDetails = isNotEmpty(diagnosisTreatmentDtos) ? diagnosisTreatmentDtos.parallelStream().map(new Function<DiagnosisTreatmentDto, PreAuthorizationRequestDiagnosisTreatmentDetail>() {
                @Override
                public PreAuthorizationRequestDiagnosisTreatmentDetail apply(DiagnosisTreatmentDto diagnosisTreatmentDto) {
                    PreAuthorizationRequestDiagnosisTreatmentDetail preAuthorizationRequestDiagnosisTreatmentDetail = new PreAuthorizationRequestDiagnosisTreatmentDetail();
                    try {
                        BeanUtils.copyProperties(preAuthorizationRequestDiagnosisTreatmentDetail, diagnosisTreatmentDto);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return preAuthorizationRequestDiagnosisTreatmentDetail;
                }
            }).collect(Collectors.toSet()) : Sets.newHashSet();
        return this;
    }

    public PreAuthorizationRequest updateWithPreAuthorizationRequestIllnessDetail(IllnessDetailDto illnessDetailDto) {
        PreAuthorizationRequestIllnessDetail preAuthorizationRequestIllnessDetail = isNotEmpty(this.preAuthorizationRequestIllnessDetail) ? this.preAuthorizationRequestIllnessDetail : new PreAuthorizationRequestIllnessDetail();
        if(isNotEmpty(illnessDetailDto)){
            try {
                BeanUtils.copyProperties(preAuthorizationRequestIllnessDetail, illnessDetailDto);
                this.preAuthorizationRequestIllnessDetail = preAuthorizationRequestIllnessDetail;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public PreAuthorizationRequest updateWithPreAuthorizationRequestDrugService(List<DrugServiceDto> drugServicesDtos) {
        this.preAuthorizationRequestDrugServices = isNotEmpty(drugServicesDtos) ? drugServicesDtos.parallelStream().map(new Function<DrugServiceDto, PreAuthorizationRequestDrugService>() {
            @Override
            public PreAuthorizationRequestDrugService apply(DrugServiceDto drugServiceDto) {
                PreAuthorizationRequestDrugService preAuthorizationRequestDrugService = new PreAuthorizationRequestDrugService();
                try {
                    BeanUtils.copyProperties(preAuthorizationRequestDrugService, drugServiceDto);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return preAuthorizationRequestDrugService;
            }
        }).collect(Collectors.toSet()) : Sets.newHashSet();
        return this;
    }

    public PreAuthorizationRequest updateWithDocuments(Set<GHProposerDocument> proposerDocuments) {
        this.proposerDocuments = proposerDocuments;
        return this;
    }

    public PreAuthorizationRequest updateWithPreAuthorizationId(String preAuthorizationIdString) {
        if(isEmpty(preAuthorizationId)) {
            PreAuthorizationId preAuthorizationId = new PreAuthorizationId(preAuthorizationIdString);
            this.preAuthorizationId = preAuthorizationId;
        }
        return this;
    }

    public void savedRegisterFollowUpReminders() throws GenerateReminderFollowupException {
        try {
            registerEvent(new PreAuthorizationFollowUpReminderEvent(this.preAuthorizationRequestId));
        } catch (Exception e){
            throw new GenerateReminderFollowupException(e.getMessage());
        }
    }

    public PreAuthorizationRequest updateWithScheduledTokens(Map<String, ScheduleToken> scheduledTokens) {
        this.scheduledTokens = scheduledTokens;
        return this;
    }

    public PreAuthorizationRequest updateFlagForFirstReminderSent(Boolean isSent) {
        this.firstReminderSent = isSent;
        return this;
    }

    public PreAuthorizationRequest updateFlagForSecondReminderSent(Boolean isSent) {
        this.secondReminderSent = isSent;
        return this;
    }

    public PreAuthorizationRequest updateStatus(Status status) {
        this.status = status;
        return this;
    }

    public PreAuthorizationRequest updateWithComments(Set<CommentDetail> commentDetails) {
        this.commentDetails = commentDetails;
        return this;
    }

    public PreAuthorizationRequest updateWithPreAuthorizationDate(LocalDate preAuthorizationDate) {
        this.preAuthorizationDate = preAuthorizationDate;
        return this;
    }

    @Override
    public String getIdentifier() {
        return preAuthorizationRequestId;
    }

    public PreAuthorizationRequest updatePreAuthorizationSubmitted(Boolean submitted) {
        this.submitted = submitted;
        return this;
    }

    public PreAuthorizationRequest updateWithProcessorUserId(String preAuthProcessorUserId) {
        if(isNotEmpty(preAuthProcessorUserId))
            this.preAuthorizationProcessorUserId = preAuthProcessorUserId;
        return this;
    }

    public PreAuthorizationRequest updateWithPreAuthorizationUnderWriterUserId(String preAuthorizationUnderWriterUserId) {
        if(isNotEmpty(preAuthorizationUnderWriterUserId))
            this.preAuthorizationUnderWriterUserId = preAuthorizationUnderWriterUserId;
        return this;
    }

    public PreAuthorizationRequest updateWithBatchUploaderUserId(String batchUploaderUserId) {
        this.batchUploaderUserId = batchUploaderUserId;
        return this;
    }


    public enum Status {
        INTIMATION("Intimation"), EVALUATION("Evaluation"), CANCELLED("Cancelled"), UNDERWRITING("Underwriting"), APPROVED("Approved"), REJECTED("Rejected"), RETURNED("Evaluation");

        private String description;

        private Status(String description){
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
