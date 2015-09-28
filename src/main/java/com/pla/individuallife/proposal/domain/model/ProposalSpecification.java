package com.pla.individuallife.proposal.domain.model;

import com.google.common.base.Preconditions;
import com.pla.individuallife.sharedresource.model.vo.ProposedAssured;
import com.pla.individuallife.sharedresource.model.vo.Proposer;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import org.nthdimenzion.utils.UtilValidator;

/**
 * Created by pradyumna on 22-05-2015.
 */
public class ProposalSpecification {


    public void checkProposedAssured(ProposedAssured proposedAssured) {
        if (proposedAssured.getMaritalStatus() == MaritalStatus.MARRIED) {
            Preconditions.checkArgument(UtilValidator.isNotEmpty(proposedAssured.getSpouseFirstName()));
            Preconditions.checkArgument(UtilValidator.isNotEmpty(proposedAssured.getSpouseLastName()));
            Preconditions.checkArgument(UtilValidator.isNotEmpty(proposedAssured.getMobileNumber()));
        }
        Preconditions.checkArgument(proposedAssured.getTitle() != null, "Please specify Title.");
        Preconditions.checkArgument(UtilValidator.isNotEmpty(proposedAssured.getFirstName()), "Please specify Firstname.");
        Preconditions.checkArgument(UtilValidator.isNotEmpty(proposedAssured.getSurname()), "Please specify Surname.");
        Preconditions.checkArgument(UtilValidator.isNotEmpty(proposedAssured.getNrc()), "Please specify NRC.");
        Preconditions.checkArgument(UtilValidator.isNotEmpty(proposedAssured.getMobileNumber()), "Please specify MobileNumber.");
        Preconditions.checkArgument(proposedAssured.getGender() != null, "Please specify Sex.");
        Preconditions.checkArgument(proposedAssured.getDateOfBirth() != null, "Please specify Date of Birth.");

        Preconditions.checkArgument(proposedAssured.getEmploymentDetail().getOccupationClass() != null, "Please specify Occupation.");
        if (!proposedAssured.getEmploymentDetail().getOccupationClass().equals("Housewives")) {
            Preconditions.checkArgument(proposedAssured.getEmploymentDetail().getEmployer() != null, "Please specify Employer.");
            Preconditions.checkArgument(proposedAssured.getEmploymentDetail().getEmploymentDate() != null, "Please specify Employment Date.");
            Preconditions.checkArgument(proposedAssured.getEmploymentDetail().getEmploymentTypeId() != null, "Please specify Employment Type.");
            Preconditions.checkArgument(proposedAssured.getEmploymentDetail().getAddress().getAddress1() != null, "Please specify Employment Address1.");
            Preconditions.checkArgument(proposedAssured.getEmploymentDetail().getAddress().getProvince() != null, "Please specify Employment Province.");
            Preconditions.checkArgument(proposedAssured.getEmploymentDetail().getAddress().getTown() != null, "Please specify Employment Town.");
        }

//        Preconditions.checkArgument(proposedAssured.getResidentialAddress().getAddress1() != null, "Please specify Residential Address1.");
//        Preconditions.checkArgument(proposedAssured.getResidentialAddress().getProvince() != null, "Please specify Residential Province.");
//        Preconditions.checkArgument(proposedAssured.getResidentialAddress().getTown() != null, "Please specify Residential Town.");

        Preconditions.checkArgument(proposedAssured.getResidentialAddress().getAddress().getAddress1() != null, "Please specify Residential Address1.");
        Preconditions.checkArgument(proposedAssured.getResidentialAddress().getAddress().getProvince() != null, "Please specify Residential Province.");
        Preconditions.checkArgument(proposedAssured.getResidentialAddress().getAddress().getTown() != null, "Please specify Residential Town.");

    }

    public void checkProposer(Proposer proposer) {

        if (proposer.getMaritalStatus() == MaritalStatus.MARRIED) {
            Preconditions.checkArgument(UtilValidator.isNotEmpty(proposer.getSpouseFirstName()));
            Preconditions.checkArgument(UtilValidator.isNotEmpty(proposer.getSpouseLastName()));
            Preconditions.checkArgument(UtilValidator.isNotEmpty(proposer.getMobileNumber()));
        }
        Preconditions.checkArgument(proposer.getTitle() != null, "Please specify Title.");
        Preconditions.checkArgument(UtilValidator.isNotEmpty(proposer.getFirstName()), "Please specify Firstname.");
        Preconditions.checkArgument(UtilValidator.isNotEmpty(proposer.getSurname()), "Please specify Surname.");
        Preconditions.checkArgument(UtilValidator.isNotEmpty(proposer.getNrc()), "Please specify NRC.");
        Preconditions.checkArgument(UtilValidator.isNotEmpty(proposer.getMobileNumber()), "Please specify MobileNumber.");
        Preconditions.checkArgument(proposer.getGender() != null, "Please specify Sex.");
        Preconditions.checkArgument(proposer.getDateOfBirth() != null, "Please specify Date of Birth.");
        Preconditions.checkArgument(proposer.getEmploymentDetail().getOccupationClass() != null, "Please specify Occupation.");
        if (!proposer.getEmploymentDetail().getOccupationClass().equals("Housewives")) {
            Preconditions.checkArgument(proposer.getEmploymentDetail().getEmployer() != null, "Please specify Employer.");
            Preconditions.checkArgument(proposer.getEmploymentDetail().getEmploymentDate() != null, "Please specify Employment Date.");
            Preconditions.checkArgument(proposer.getEmploymentDetail().getEmploymentTypeId() != null, "Please specify Employment Type.");
            Preconditions.checkArgument(proposer.getEmploymentDetail().getAddress().getAddress1() != null, "Please specify Employment Address1.");
            Preconditions.checkArgument(proposer.getEmploymentDetail().getAddress().getProvince() != null, "Please specify Employment Province.");
            Preconditions.checkArgument(proposer.getEmploymentDetail().getAddress().getTown() != null, "Please specify Employment Town.");
        }

//        Preconditions.checkArgument(proposer.getResidentialAddress().getAddress1() != null, "Please specify Residential Address1.");
//        Preconditions.checkArgument(proposer.getResidentialAddress().getProvince() != null, "Please specify Residential Province.");
//        Preconditions.checkArgument(proposer.getResidentialAddress().getTown() != null, "Please specify Residential Town.");

        Preconditions.checkArgument(proposer.getResidentialAddress().getAddress().getAddress1() != null, "Please specify Residential Address1.");
        Preconditions.checkArgument(proposer.getResidentialAddress().getAddress().getProvince() != null, "Please specify Residential Province.");
        Preconditions.checkArgument(proposer.getResidentialAddress().getAddress().getTown() != null, "Please specify Residential Town.");
    }

    public void checkProposerAgainstPlan(int minAge,int maxAge,int proposedAssuredAge ) {
        Preconditions.checkState(proposedAssuredAge > minAge && proposedAssuredAge < maxAge, "Please specify Proposed Assured age between " + minAge +" and " + maxAge);
    }
}
