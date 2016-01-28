package com.pla.individuallife.endorsement.domain.service;

import com.google.common.collect.Lists;
import com.pla.individuallife.endorsement.domain.model.ILEndorsementProcessor;
import com.pla.individuallife.endorsement.domain.model.IndividualLifeEndorsement;
import com.pla.individuallife.endorsement.domain.model.IndividualLifeEndorsementStatusAudit;
import com.pla.individuallife.endorsement.presentation.dto.ILEndorsementApproverCommentDto;
import com.pla.individuallife.endorsement.presentation.dto.ILEndorsementDto;
import com.pla.individuallife.endorsement.query.ILEndorsementFinder;
import com.pla.individuallife.endorsement.repository.ILEndorsementRepository;
import com.pla.individuallife.endorsement.repository.ILEndorsementStatusAuditRepository;
import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.sharedresource.query.ILClientFinder;
import com.pla.individuallife.sharedresource.util.ILInsuredFactory;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.sharedkernel.identifier.EndorsementId;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Raghu on 8/27/2015.
 */
@Service
public class IndividualLifeEndorsementService {

    private ILEndorsementRoleAdapter ilEndorsementRoleAdapter;

    private ILEndorsementNumberGenerator ilEndorsementNumberGenerator;

    private ILEndorsementRequestNumberGenerator ilEndorsementRequestNumberGenerator;

    @Autowired
    private ILEndorsementFinder ilEndorsementFinder;

    @Autowired
    private IPremiumCalculator premiumCalculator;

    private ILClientFinder ilFinder;

    @Autowired
    private ILEndorsementStatusAuditRepository ilEndorsementStatusAuditRepository;

    @Autowired
    private ILInsuredFactory ilInsuredFactory;

    @Autowired
    public IndividualLifeEndorsementService(ILEndorsementRoleAdapter ilEndorsementRoleAdapter, ILEndorsementRequestNumberGenerator ilEndorsementNumberGenerator, ILClientFinder ilFinder) {
        this.ilEndorsementRoleAdapter = ilEndorsementRoleAdapter;
        this.ilEndorsementRequestNumberGenerator = ilEndorsementNumberGenerator;
        this.ilFinder = ilFinder;
    }

    public IndividualLifeEndorsement createEndorsement(String policyId, ILPolicyDto ilPolicyDto, UserDetails userDetails) {
        ILEndorsementProcessor ilEndorsementProcessor = ilEndorsementRoleAdapter.userToEndorsementProcessor(userDetails);
        String endorsementId = ObjectId.get().toString();
        String endorsementRequestNumber = ilEndorsementRequestNumberGenerator.getEndorsementRequestNumber(IndividualLifeEndorsement.class);
        //Map<String, Object> policyMap = ilFinder.findPolicyById(policyId);
        //String policyNumber = ((PolicyNumber) policyMap.get("policyNumber")).getPolicyNumber();
        //String policyHolderName =  policyMap.get("proposer")!=null?((Proposer) policyMap.get("proposer")).getFirstName():null;
        return ilEndorsementProcessor.createEndorsement(endorsementId, endorsementRequestNumber, policyId, ilPolicyDto);
    }

    public IndividualLifeEndorsement updateEndorsement(String policyId, ILEndorsementDto iLEndorsementDto, UserDetails userDetails) {
        ILEndorsementProcessor ilEndorsementProcessor = ilEndorsementRoleAdapter.userToEndorsementProcessor(userDetails);
        //String endorsementId = ObjectId.get().toString();
        //String endorsementRequestNumber = ilEndorsementRequestNumberGenerator.getEndorsementRequestNumber(IndividualLifeEndorsement.class);
        //Map<String, Object> policyMap = ilFinder.findPolicyById(policyId);
        //String policyNumber = ((PolicyNumber) policyMap.get("policyNumber")).getPolicyNumber();
        //String policyHolderName =  policyMap.get("proposer")!=null?((Proposer) policyMap.get("proposer")).getFirstName():null;
        return ilEndorsementProcessor.createEndorsement(iLEndorsementDto.getEndorsementId().getEndorsementId(), iLEndorsementDto.getEndorsementRequestNumber(), policyId, iLEndorsementDto.getIlPolicyDto());
    }

    public List<ILEndorsementApproverCommentDto> findApproverComments(String endorsementId) {
        List<IndividualLifeEndorsementStatusAudit> audits = ilEndorsementStatusAuditRepository.findByEndorsementId(new EndorsementId(endorsementId));
        List<ILEndorsementApproverCommentDto> endorsementApproverCommentsDtos = Lists.newArrayList();
        if (isNotEmpty(audits)) {
            endorsementApproverCommentsDtos = audits.stream().map(new Function<IndividualLifeEndorsementStatusAudit, ILEndorsementApproverCommentDto>() {
                @Override
                public ILEndorsementApproverCommentDto apply(IndividualLifeEndorsementStatusAudit groupLifeEndorsementStatusAudit) {
                    ILEndorsementApproverCommentDto endorsementApproverCommentDto = new ILEndorsementApproverCommentDto();
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

/*   public PremiumDetailDto recalculatePremium(String endorsementId,UserDetails userDetails) throws ParseException {
        IndividualLifeEndorsement groupLifeEndorsement = ilEndorsementRepository.findOne(new EndorsementId(endorsementId));
        if (groupLifeEndorsement.getEndorsement()==null){
            return new PremiumDetailDto();
        }

     *//**//*   if (groupLifeEndorsement.getEndorsement().getMemberEndorsement()==null){
            return new PremiumDetailDto();
        }*//**//*
    Map  policyMap = ilFinder.findPolicyById(groupLifeEndorsement.getPolicy().getPolicyId().getPolicyId());
    PremiumDetail premiumDetail = (PremiumDetail) policyMap.get("premiumDetail");
    Industry industry = (Industry) policyMap.get("industry");
    PremiumDetailDto premiumDetailDto = new PremiumDetailDto(premiumDetail.getAddOnBenefit(),premiumDetail.getProfitAndSolvency(),premiumDetail.getHivDiscount(),
            premiumDetail.getValuedClientDiscount(),premiumDetail.getLongTermDiscount(),premiumDetail.getPolicyTermValue());
    groupLifeEndorsement = populateAnnualBasicPremiumOfInsured(groupLifeEndorsement, userDetails, premiumDetailDto,industry);
        premiumDetailDto = getPremiumDetail(groupLifeEndorsement);
        premiumDetailDto.setIsPremiumApplicable(!groupLifeEndorsement.getEndorsementType().equals(ILEndorsementType.ASSURED_MEMBER_DELETION));
        return premiumDetailDto;
    }*/


    /* @TODO change according to type
   /* public IndividualLifeEndorsement populateAnnualBasicPremiumOfInsured(IndividualLifeEndorsement groupLifeQuotation, UserDetails userDetails, PremiumDetailDto premiumDetailDto,Industry industry) throws ParseException {
        Set<Proposer> insureds = Sets.newLinkedHashSet();
        boolean isMemberPromotion = false;
        boolean isNewCategory = false;
        if (ILEndorsementType.NEW_CATEGORY_RELATION.equals(groupLifeQuotation.getEndorsementType())) {
            insureds = groupLifeQuotation.getEndorsement().getNewCategoryRelationEndorsement().getInsureds();
            isNewCategory = true;
        }
        else if (ILEndorsementType.ASSURED_MEMBER_ADDITION.equals(groupLifeQuotation.getEndorsementType()))
            insureds = groupLifeQuotation.getEndorsement().getMemberEndorsement().getInsureds();
        else if (ILEndorsementType.ASSURED_MEMBER_DELETION.equals(groupLifeQuotation.getEndorsementType()))
            insureds = groupLifeQuotation.getEndorsement().getMemberDeletionEndorsements().getInsureds();
        else if (ILEndorsementType.MEMBER_PROMOTION.equals(groupLifeQuotation.getEndorsementType())) {
            insureds = groupLifeQuotation.getEndorsement().getPremiumEndorsement().getInsureds();
            isMemberPromotion = true;
        }
        Map<String, Object> policyDetail = ilEndorsementFinder.getPolicyDetail(groupLifeQuotation.getEndorsementId().getEndorsementId());
        Date inceptionDate = (Date) policyDetail.get("inceptionDate");
        LocalDate inceptionOn = new LocalDate(inceptionDate);
        Date expiryDate = (Date) policyDetail.get("expiredDate");
        LocalDate expiredOn = new LocalDate(expiryDate);
        int policyTerm = Days.daysBetween(inceptionOn,expiredOn).getDays();
        int endorsementDuration = Days.daysBetween(LocalDate.now(),expiredOn).getDays();
        premiumDetailDto.setPolicyTermValue(endorsementDuration);
        insureds = ilInsuredFactory.calculateProratePremiumForInsureds(premiumDetailDto, insureds, policyTerm, endorsementDuration, isMemberPromotion,isNewCategory);
        groupLifeQuotation = updateInsured(groupLifeQuotation, insureds, userDetails);
        groupLifeQuotation = updateWithPremiumDetail(groupLifeQuotation, premiumDetailDto, userDetails,industry);
        return groupLifeQuotation;
    }*/

/*    public IndividualLifeEndorsement updateInsured(IndividualLifeEndorsement groupLifeEndorsement, Set<Insured> insureds, UserDetails userDetails) {
        ILEndorsementProcessor glEndorsementProcessor = ilEndorsementRoleAdapter.userToEndorsementProcessor(userDetails);
        return glEndorsementProcessor.updateWithInsured(groupLifeEndorsement, insureds);
    }*/

    /*public IndividualLifeEndorsement updateWithPremiumDetail(IndividualLifeEndorsement groupLifeProposal, PremiumDetailDto premiumDetailDto, UserDetails userDetails,Industry industry) {
        ILEndorsementProcessor glEndorsementProcessor = ilEndorsementRoleAdapter.userToEndorsementProcessor(userDetails);
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
    }*/

/*    private PremiumDetailDto getPremiumDetail(IndividualLifeEndorsement groupLifeProposal) {
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
    }*/
}
