package com.pla.individuallife.sharedresource.model;

import com.google.common.collect.Lists;
import com.pla.individuallife.policy.domain.model.IndividualLifePolicy;
import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.proposal.query.ILProposalFinder;
import com.pla.individuallife.sharedresource.dto.AgentDetailDto;
import com.pla.individuallife.sharedresource.dto.ILPolicyDetailDto;
import com.pla.individuallife.sharedresource.dto.ProposerDto;
import com.pla.individuallife.sharedresource.model.vo.AgentCommissionShareModel;
import com.pla.individuallife.sharedresource.model.vo.EmploymentDetail;
import com.pla.individuallife.sharedresource.model.vo.ResidentialAddress;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import com.pla.sharedkernel.service.EmailAttachment;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Raghu on 25/12/2015.
 */
public enum ILEndorsementType {

    ASSURED_NAME_CHANGE("Correction of Life Assured Name") {


        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.getProposedAssured().setFirstName(ilPolicy.getProposedAssured().getFirstName());
            ilPolicyDto.getProposedAssured().setSurname(ilPolicy.getProposedAssured().getSurname());
            ilPolicyDto.getProposedAssured().setTitle(ilPolicy.getProposedAssured().getTitle());
            ilPolicyDto.getProposedAssured().setOtherName(ilPolicy.getProposedAssured().getOtherName());
            return ilPolicyDto;
        }
    }, POLICYHOLDER_NAME_CHANGE("Correction of Policyholder Name") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.getProposer().setFirstName(ilPolicy.getProposer().getFirstName());
            ilPolicyDto.getProposer().setSurname(ilPolicy.getProposer().getSurname());
            ilPolicyDto.getProposer().setTitle(ilPolicy.getProposer().getTitle());
            ilPolicyDto.getProposer().setOtherName(ilPolicy.getProposer().getOtherName());
            return ilPolicyDto;
        }
    }, ASSURED_GENDER_CHANGE("Correction Life Assured - Gender") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.getProposedAssured().setGender(ilPolicy.getProposedAssured().getGender());
            return ilPolicyDto;
        }
    }, POLICYHOLDER_GENDER_CHANGE("Correction Policyholder - Gender") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.getProposer().setGender(ilPolicy.getProposer().getGender());
            return ilPolicyDto;
        }
    }, ASSURED_CONTACT_DETAILS_CHANGE("Change of Contact Details- Life Assured") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.getProposedAssured().setEmailAddress(ilPolicy.getProposedAssured().getEmailAddress());
            ilPolicyDto.getProposedAssured().setMobileNumber(ilPolicy.getProposedAssured().getMobileNumber());
            ilPolicyDto.getProposedAssured().getResidentialAddress().setAddress1(ilPolicy.getProposedAssured().getResidentialAddress().getAddress().getAddress1());
            ilPolicyDto.getProposedAssured().getResidentialAddress().setAddress2(ilPolicy.getProposedAssured().getResidentialAddress().getAddress().getAddress2());
            return ilPolicyDto;
        }
    }, POLICYHOLDER_CONTACT_DETAILS_CHANGE("Change of Contact Details-Policyholder") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.getProposer().setEmailAddress(ilPolicy.getProposer().getEmailAddress());
            ilPolicyDto.getProposer().setMobileNumber(ilPolicy.getProposer().getMobileNumber());
            ilPolicyDto.getProposer().getResidentialAddress().setAddress1(ilPolicy.getProposer().getResidentialAddress().getAddress().getAddress1());
            ilPolicyDto.getProposer().getResidentialAddress().setAddress2(ilPolicy.getProposer().getResidentialAddress().getAddress().getAddress2());
            return ilPolicyDto;
        }
    }, BENEFICIARY_DETAILS_CHANGE("Change/Add Beneficiary") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.setBeneficiaries(ilPolicy.getBeneficiaries());
            return ilPolicyDto;
        }

    }, PAYMENT_MODE_CHANGE("Change method of payment") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();

            ilPolicyDto.setPremiumPaymentDetails(ilPolicy.getPremiumPaymentDetails());

            return ilPolicyDto;
        }

    }, AGENT_DETAILS_CHANGE("Change Agent") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            AgentCommissionShareModel agentCommissionDetails = ilPolicy.getAgentCommissionShareModel();
            List<AgentCommissionShareModel.AgentCommissionShare> listAgentDetails = agentCommissionDetails.getCommissionShare();
            Set<AgentDetailDto> listAgentDto = new HashSet();

            for (AgentCommissionShareModel.AgentCommissionShare agent:listAgentDetails) {
                AgentDetailDto agentDto = new AgentDetailDto();
                agentDto.setAgentId(agent.getAgentId().getAgentId());
                agentDto.setCommission(agent.getAgentCommission());
                agentDto.setFirstName(ilProposalFinder.getAgentFullNameById(agent.getAgentId().getAgentId()));
                listAgentDto.add(agentDto);
            }
            ilPolicyDto.setAgentCommissionDetails(listAgentDto);
            return ilPolicyDto;
        }

    }, PAYER_DETAILS_CHANGE("Change Payer") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();

            ProposerDto proposerDetails = new ProposerDto(ilPolicy.getProposer().getTitle(),ilPolicy.getProposer().getFirstName(), ilPolicy.getProposer().getSurname(), ilPolicy.getProposer().getNrc(), ilPolicy.getProposer().getDateOfBirth(), ilPolicy.getProposer().getGender(),
                    ilPolicy.getProposer().getMobileNumber(), ilPolicy.getProposer().getEmailAddress(), ilPolicy.getProposer().getMaritalStatus(), ilPolicy.getProposer().getSpouseFirstName(), ilPolicy.getProposer().getSpouseLastName(), ilPolicy.getProposer().getEmailAddress(), ilPolicy.getProposer().getSpouseMobileNumber(), ilPolicy.getProposer().getEmploymentDetail(), ilPolicy.getProposer().getResidentialAddress(),ilPolicy.getProposer().getIsProposedAssured(), ilPolicy.getProposer().getOtherName(),
                    ilPolicy.getProposer().getClientId());

            ilPolicyDto.setProposer(proposerDetails);
            return ilPolicyDto;
        }

    }, SUM_ASSURED_CHANGE("Change Sum Assured") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.setProposalPlanDetail(ilPolicy.getProposalPlanDetail());
            return ilPolicyDto;
        }

    }, ASSURED_DOB_CHANGE("Change Life Assured Date of Birth") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.getProposedAssured().setDateOfBirth(ilPolicy.getProposedAssured().getDateOfBirth());
            return ilPolicyDto;
        }

    }, POLICYHOLDER_DOB_CHANGE("Change Policyholder Date of Birth") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.getProposer().setFirstName(ilPolicy.getProposer().getFirstName());
            ilPolicyDto.getProposer().setDateOfBirth(ilPolicy.getProposer().getDateOfBirth());
            return ilPolicyDto;
        }

    }, ASSURED_NRC_CHANGE("Correction of NRC - Life Assured") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.getProposedAssured().setNrc(ilPolicy.getProposedAssured().getNrc());
            return ilPolicyDto;
        }
    }, POLICYHOLDER_NRC_CHANGE("Correction of NRC-Policyholder") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.getProposer().setNrc(ilPolicy.getProposer().getNrc());
            return ilPolicyDto;
        }
    }, PREMIUM_ADJUSTMENT("Premium Adjustment") {
        public ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy){
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            ilPolicyDto.getPremiumPaymentDetails().setPremiumFrequencyPayable(ilPolicy.getPremiumPaymentDetails().getPremiumFrequencyPayable());
            ilPolicyDto.getPremiumDetailDto().setAnnualPremium(ilPolicy.getPremiumPaymentDetails().getPremiumDetail().getAnnualPremium());
            ilPolicyDto.getPremiumDetailDto().setMonthlyPremium(ilPolicy.getPremiumPaymentDetails().getPremiumDetail().getMonthlyPremium());
            ilPolicyDto.getPremiumDetailDto().setSemiannualPremium(ilPolicy.getPremiumPaymentDetails().getPremiumDetail().getSemiannualPremium());
            ilPolicyDto.getPremiumDetailDto().setQuarterlyPremium(ilPolicy.getPremiumPaymentDetails().getPremiumDetail().getQuarterlyPremium());
            return ilPolicyDto;
        }
    };

    private String description;

    ILEndorsementType(String description) {
        this.description = description;
    }

    public static List<Map<String, String>> getAllEndorsementType() {
        List<Map<String, String>> endorsementTypes = Lists.newArrayList();
        ILEndorsementType[] ilEndorsementTypes = ILEndorsementType.values();
        for (int count = 0; count < ilEndorsementTypes.length; count++) {
            com.pla.individuallife.sharedresource.model.ILEndorsementType ilEndorsementType = ilEndorsementTypes[count];
            Map<String, String> map = new HashMap<>();
            map.put("code", ilEndorsementType.name());
            map.put("description", ilEndorsementType.getDescription());
            endorsementTypes.add(map);
        }
        return endorsementTypes;
    }

    public String getDescription() {
        return description;
    }

    //public abstract EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException;
    public abstract ILPolicyDto populateInfoByType(IndividualLifePolicy ilPolicy);

    @Autowired
    ILProposalFinder ilProposalFinder;

}
