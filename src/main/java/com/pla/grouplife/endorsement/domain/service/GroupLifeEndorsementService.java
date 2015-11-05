package com.pla.grouplife.endorsement.domain.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.grouplife.endorsement.domain.model.GLEndorsementProcessor;
import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsement;
import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsementStatusAudit;
import com.pla.grouplife.endorsement.presentation.dto.GLEndorsementApproverCommentDto;
import com.pla.grouplife.endorsement.query.GLEndorsementFinder;
import com.pla.grouplife.endorsement.repository.GLEndorsementRepository;
import com.pla.grouplife.endorsement.repository.GLEndorsementStatusAuditRepository;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.grouplife.sharedresource.util.GLInsuredFactory;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.BasicPremiumDto;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 8/27/2015.
 */
@Service
public class GroupLifeEndorsementService {

    private GroupLifeEndorsementRoleAdapter groupLifeEndorsementRoleAdapter;

    private GLEndorsementNumberGenerator glEndorsementNumberGenerator;

    @Autowired
    private GLEndorsementFinder glEndorsementFinder;

    @Autowired
    private GLEndorsementRepository glEndorsementRepository;

    @Autowired
    private IPremiumCalculator premiumCalculator;

    private GLFinder glFinder;

    @Autowired
    private GLEndorsementStatusAuditRepository glEndorsementStatusAuditRepository;

    @Autowired
    private GLInsuredFactory glInsuredFactory;

    @Autowired
    public GroupLifeEndorsementService(GroupLifeEndorsementRoleAdapter groupLifeEndorsementRoleAdapter, GLEndorsementNumberGenerator glEndorsementNumberGenerator, GLFinder glFinder) {
        this.groupLifeEndorsementRoleAdapter = groupLifeEndorsementRoleAdapter;
        this.glEndorsementNumberGenerator = glEndorsementNumberGenerator;
        this.glFinder = glFinder;
    }

    public GroupLifeEndorsement createEndorsement(String policyId, GLEndorsementType glEndorsementType, UserDetails userDetails) {
        GLEndorsementProcessor glEndorsementProcessor = groupLifeEndorsementRoleAdapter.userToEndorsementProcessor(userDetails);
        String endorsementId = ObjectId.get().toString();
        String endorsementNumber = glEndorsementNumberGenerator.getEndorsementNumber(GroupLifeEndorsement.class, LocalDate.now());
        Map<String, Object> policyMap = glFinder.findPolicyById(policyId);
        String policyNumber = ((PolicyNumber) policyMap.get("policyNumber")).getPolicyNumber();
        String policyHolderName =  policyMap.get("proposer")!=null?((Proposer) policyMap.get("proposer")).getProposerName():null;
        return glEndorsementProcessor.createEndorsement(endorsementId, endorsementNumber, policyId, policyNumber, policyHolderName, glEndorsementType);
    }

    public List<GLEndorsementApproverCommentDto> findApproverComments(String endorsementId) {
        List<GroupLifeEndorsementStatusAudit> audits = glEndorsementStatusAuditRepository.findByEndorsementId(new EndorsementId(endorsementId));
        List<GLEndorsementApproverCommentDto> endorsementApproverCommentsDtos = Lists.newArrayList();
        if (isNotEmpty(audits)) {
            endorsementApproverCommentsDtos = audits.stream().map(new Function<GroupLifeEndorsementStatusAudit, GLEndorsementApproverCommentDto>() {
                @Override
                public GLEndorsementApproverCommentDto apply(GroupLifeEndorsementStatusAudit groupLifeEndorsementStatusAudit) {
                    GLEndorsementApproverCommentDto endorsementApproverCommentDto = new GLEndorsementApproverCommentDto();
                    try {
                        BeanUtils.copyProperties(endorsementApproverCommentDto, groupLifeEndorsementStatusAudit);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return endorsementApproverCommentDto;
                }
            }).collect(Collectors.toList());
        }
        return endorsementApproverCommentsDtos;
    }

    public PremiumDetailDto recalculatePremium(String endorsementId,UserDetails userDetails) throws ParseException {
        GroupLifeEndorsement groupLifeEndorsement = glEndorsementRepository.findOne(new EndorsementId(endorsementId));
        if (groupLifeEndorsement.getEndorsement()==null){
            return new PremiumDetailDto();
        }

     /*   if (groupLifeEndorsement.getEndorsement().getMemberEndorsement()==null){
            return new PremiumDetailDto();
        }*/
    Map  policyMap = glFinder.findPolicyById(groupLifeEndorsement.getPolicy().getPolicyId().getPolicyId());
    PremiumDetail premiumDetail = (PremiumDetail) policyMap.get("premiumDetail");
    Industry industry = (Industry) policyMap.get("industry");
    PremiumDetailDto premiumDetailDto = new PremiumDetailDto(premiumDetail.getAddOnBenefit(),premiumDetail.getProfitAndSolvency(),premiumDetail.getHivDiscount(),
            premiumDetail.getValuedClientDiscount(),premiumDetail.getLongTermDiscount(),premiumDetail.getPolicyTermValue());
    groupLifeEndorsement = populateAnnualBasicPremiumOfInsured(groupLifeEndorsement, userDetails, premiumDetailDto,industry);
        premiumDetailDto = getPremiumDetail(groupLifeEndorsement);
        premiumDetailDto.setIsPremiumApplicable(!groupLifeEndorsement.getEndorsementType().equals(GLEndorsementType.ASSURED_MEMBER_DELETION));
        return premiumDetailDto;
    }

    /*
    * @TODO change according to type
    * */
    public GroupLifeEndorsement populateAnnualBasicPremiumOfInsured(GroupLifeEndorsement groupLifeQuotation, UserDetails userDetails, PremiumDetailDto premiumDetailDto,Industry industry) throws ParseException {
        Set<Insured> insureds = Sets.newLinkedHashSet();
        if (GLEndorsementType.NEW_CATEGORY_RELATION.equals(groupLifeQuotation.getEndorsementType()))
            insureds = groupLifeQuotation.getEndorsement().getNewCategoryRelationEndorsement().getInsureds();
        else if (GLEndorsementType.ASSURED_MEMBER_ADDITION.equals(groupLifeQuotation.getEndorsementType()))
            insureds = groupLifeQuotation.getEndorsement().getMemberEndorsement().getInsureds();
        else if (GLEndorsementType.ASSURED_MEMBER_DELETION.equals(groupLifeQuotation.getEndorsementType()))
            insureds = groupLifeQuotation.getEndorsement().getMemberDeletionEndorsements().getInsureds();
        else if (GLEndorsementType.MEMBER_PROMOTION.equals(groupLifeQuotation.getEndorsementType()))
            insureds = groupLifeQuotation.getEndorsement().getPremiumEndorsement().getInsureds();
        Map<String, Object> policyDetail = glEndorsementFinder.getPolicyDetail(groupLifeQuotation.getEndorsementId().getEndorsementId());
        Date inceptionDate = (Date) policyDetail.get("inceptionDate");
        DateTime inceptionOn = new DateTime(inceptionDate);
        Date expiryDate = (Date) policyDetail.get("expiredDate");
        DateTime expiredOn = new DateTime(expiryDate);
        int policyTerm = Days.daysBetween(inceptionOn,expiredOn).getDays();
        int endorsementDuration = Days.daysBetween(DateTime.now(),expiredOn).getDays();
        premiumDetailDto.setPolicyTermValue(endorsementDuration);
        insureds = glInsuredFactory.calculateProratePremiumForInsureds(premiumDetailDto, insureds, policyTerm, endorsementDuration);
        groupLifeQuotation = updateInsured(groupLifeQuotation, insureds, userDetails);
        groupLifeQuotation = updateWithPremiumDetail(groupLifeQuotation, premiumDetailDto, userDetails,industry);
        return groupLifeQuotation;
    }

    public GroupLifeEndorsement updateInsured(GroupLifeEndorsement groupLifeEndorsement, Set<Insured> insureds, UserDetails userDetails) {
        GLEndorsementProcessor glEndorsementProcessor = groupLifeEndorsementRoleAdapter.userToEndorsementProcessor(userDetails);
        return glEndorsementProcessor.updateWithInsured(groupLifeEndorsement, insureds);
    }

    public GroupLifeEndorsement updateWithPremiumDetail(GroupLifeEndorsement groupLifeProposal, PremiumDetailDto premiumDetailDto, UserDetails userDetails,Industry industry) {
        GLEndorsementProcessor glEndorsementProcessor = groupLifeEndorsementRoleAdapter.userToEndorsementProcessor(userDetails);
        PremiumDetail premiumDetail = new PremiumDetail(premiumDetailDto.getAddOnBenefit(), premiumDetailDto.getProfitAndSolvencyLoading(), premiumDetailDto.getHivDiscount(), premiumDetailDto.getValuedClientDiscount(), premiumDetailDto.getLongTermDiscount(), premiumDetailDto.getPolicyTermValue());
        premiumDetail = premiumDetail.updateWithNetPremium(groupLifeProposal.getNetAnnualPremiumPaymentAmount(premiumDetail,industry));
        if (premiumDetailDto.getPolicyTermValue() != null && premiumDetailDto.getPolicyTermValue() == 365) {
            List<ComputedPremiumDto> computedPremiumDtoList = premiumCalculator.calculateModalPremium(new BasicPremiumDto(PremiumFrequency.ANNUALLY, premiumDetail.getNetTotalPremium(), LineOfBusinessEnum.GROUP_LIFE));
            Set<GLFrequencyPremium> policies = computedPremiumDtoList.stream().map(new Function<ComputedPremiumDto, GLFrequencyPremium>() {
                @Override
                public GLFrequencyPremium apply(ComputedPremiumDto computedPremiumDto) {
                    return new GLFrequencyPremium(computedPremiumDto.getPremiumFrequency(), computedPremiumDto.getPremium().setScale(AppConstants.scale, AppConstants.roundingMode));
                }
            }).collect(Collectors.toSet());
            premiumDetail = premiumDetail.addPolicies(policies);
            premiumDetail = premiumDetail.nullifyPremiumInstallment();
        } else if (premiumDetailDto.getPolicyTermValue() != null && premiumDetailDto.getPolicyTermValue() > 0 && premiumDetailDto.getPolicyTermValue() != 365) {
            int noOfInstallment = premiumDetailDto.getPolicyTermValue() / 30;
            if ((premiumDetailDto.getPolicyTermValue() % 30) == 0) {
                noOfInstallment = noOfInstallment - 1;
            }
            for (int count = 1; count <= noOfInstallment; count++) {
                BigDecimal installmentAmount = premiumDetail.getNetTotalPremium().divide(new BigDecimal(count), 2, BigDecimal.ROUND_CEILING);
                premiumDetail = premiumDetail.addInstallments(count, installmentAmount);
            }
            if (premiumDetailDto.getPremiumInstallment() != null) {
                premiumDetail = premiumDetail.addChoosenPremiumInstallment(premiumDetailDto.getPremiumInstallment().getInstallmentNo(), premiumDetailDto.getPremiumInstallment().getInstallmentAmount());
            }
            premiumDetail = premiumDetail.nullifyFrequencyPremium();
        }
        if (premiumDetailDto.getOptedPremiumFrequency() != null && isNotEmpty(premiumDetail.getFrequencyPremiums())) {
            premiumDetail = premiumDetail.updateWithOptedFrequencyPremium(premiumDetailDto.getOptedPremiumFrequency());
        }

        groupLifeProposal = glEndorsementProcessor.updateWithPremiumDetail(groupLifeProposal, premiumDetail);
        return groupLifeProposal;
    }

    private PremiumDetailDto getPremiumDetail(GroupLifeEndorsement groupLifeProposal) {
        PremiumDetail premiumDetail = groupLifeProposal.getPremiumDetail();
        if (premiumDetail == null) {
            return new PremiumDetailDto();
        }
        PremiumDetailDto premiumDetailDto = new PremiumDetailDto(premiumDetail.getAddOnBenefit(), premiumDetail.getProfitAndSolvency(), premiumDetail.getHivDiscount(), premiumDetail.getValuedClientDiscount(), premiumDetail.getLongTermDiscount(), premiumDetail.getPolicyTermValue());
        PremiumDetail.PremiumInstallment premiumInstallment = premiumDetail.getPremiumInstallment();
        if (premiumInstallment != null) {
            premiumDetailDto = premiumDetailDto.addOptedInstallmentDetail(premiumInstallment.getNoOfInstallment(), premiumInstallment.getInstallmentAmount());
        }
        if (isNotEmpty(premiumDetail.getInstallments())) {
            for (PremiumDetail.PremiumInstallment installment : premiumDetail.getInstallments()) {
                if (installment.getNoOfInstallment() == 1) {
                    premiumDetailDto = premiumDetailDto.addInstallments(installment.getNoOfInstallment(), installment.getInstallmentAmount());
                }
            }
        }
        premiumDetailDto = premiumDetailDto.addFrequencyPremiumAmount(premiumDetail.getAnnualPremiumAmount(), premiumDetail.getSemiAnnualPremiumAmount(), premiumDetail.getQuarterlyPremiumAmount(), premiumDetail.getMonthlyPremiumAmount());
        premiumDetailDto = premiumDetailDto.addNetTotalPremiumAmount(premiumDetail.getNetTotalPremium());
        premiumDetailDto = premiumDetailDto.updateWithOptedFrequency(premiumDetail.getOptedFrequencyPremium() != null ? premiumDetail.getOptedFrequencyPremium().getPremiumFrequency() : null);
        return premiumDetailDto;
    }


}
