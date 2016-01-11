package com.pla.grouphealth.claim.cashless.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.presentation.dto.*;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.beanutils.BeanUtils;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Mohan Sharma on 1/9/2016.
 */
@Document(collection = "PRE_AUTHORIZATION_REQUEST")
@Getter
@NoArgsConstructor
public class PreAuthorizationRequest extends AbstractAggregateRoot<PreAuthorizationRequestId> {

    @Id
    @AggregateIdentifier
    private PreAuthorizationRequestId preAuthorizationRequestId;
    private String category;
    private String relationship;
    private String claimType;
    private LocalDate claimIntimationDate;
    private String batchNumber;
    private GHProposer ghProposer;
    private PreAuthorizationRequestPolicyDetail preAuthorizationRequestPolicyDetail;
    private PreAuthorizationRequestHCPDetail preAuthorizationRequestHCPDetail;
    private Set<PreAuthorizationRequestDiagnosisTreatmentDetail> preAuthorizationRequestDiagnosisTreatmentDetails;
    private PreAuthorizationRequestIllnessDetail preAuthorizationRequestIllnessDetail;
    private Set<PreAuthorizationRequestDrugService> preAuthorizationRequestDrugServices;
    private Status status;

    @Override
    public PreAuthorizationRequestId getIdentifier() {
        return preAuthorizationRequestId;
    }

    public PreAuthorizationRequest(Status status){
        this.status = status;
    }

    public PreAuthorizationRequest updateWithProposerDetail(ClaimantPolicyDetailDto claimantPolicyDetailDto) {
        if(isNotEmpty(claimantPolicyDetailDto)) {
            GHProposer ghProposer = isNotEmpty(this.getGhProposer()) ? this.getGhProposer() : getInstance(GHProposer.class);
            ghProposer.updateWithProposerDetails(claimantPolicyDetailDto.getPreAuthorizationClaimantProposerDetail());
            this.ghProposer = ghProposer;
        }
        return this;
    }

    public PreAuthorizationRequest updateWithPreAuthorizationId() {
        if(isEmpty(this.preAuthorizationRequestId))
            this.preAuthorizationRequestId = new PreAuthorizationRequestId(new ObjectId().toString());
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
        return null;
    }

    public enum Status {
        DRAFT("Draft"), PRE_AUTH_PROCESSOR_EVALUATION("Pre-Auth Processor Evaluation"), PRE_AUTH_PROCESSOR_REJECTED("Pre-Auth Processor Rejected"),
        UNDERWRITER_EVALUATION("Underwriter Evaluation"), UNDERWRITER_ON_HOLD("Underwriter On Hold"), APPROVED("Approve"), REJECTED("Rejected");

        private String description;

        private Status(String description){
            this.description = description;
        }
    }
}
