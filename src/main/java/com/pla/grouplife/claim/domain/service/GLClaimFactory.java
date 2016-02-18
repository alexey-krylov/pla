package com.pla.grouplife.claim.domain.service;

import com.google.common.collect.Sets;
import com.pla.core.query.PlanFinder;
import com.pla.grouplife.claim.application.command.*;
import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.grouplife.claim.domain.model.*;
import com.pla.grouplife.claim.presentation.dto.*;
import com.pla.grouplife.claim.query.GLClaimFinder;
import com.pla.grouplife.sharedresource.dto.ContactPersonDetailDto;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.model.vo.ProposerBuilder;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by ak
 */
@Component
public class GLClaimFactory {

    private GLFinder glFinder;
    private SequenceGenerator sequenceGenerator;
    private ClaimNumberGenerator claimNumberGenerator;
    private IIdGenerator idGenerator;
    private GLClaimFinder glClaimFinder;
    @Autowired
    private GLClaimService glClaimService;
    @Autowired
    private PlanFinder planFinder;
    @Autowired
    public GLClaimFactory(GLFinder glFinder,GLClaimFinder glClaimFinder,SequenceGenerator sequenceGenerator,ClaimNumberGenerator claimNumberGenerator,IIdGenerator idGenerator){
        this.glFinder=glFinder;
        this.glClaimFinder=glClaimFinder;
        this.sequenceGenerator=sequenceGenerator;
        this.claimNumberGenerator=claimNumberGenerator;
        this.idGenerator=idGenerator;
    }
   public  GroupLifeClaim  createClaim(CreateGLClaimIntimationCommand createCommand){
        BigDecimal reserveSum=BigDecimal.ZERO;;
        BigDecimal claimAmount=BigDecimal.ZERO;
        BigDecimal updatedClaimAmount=BigDecimal.ZERO;

       //getting claimId
       ClaimId claimId = new ClaimId(ObjectId.get().toString());
       LocalDate now=LocalDate.now();
       String claimNumberInString=claimNumberGenerator.getClaimNumber(GroupLifeClaim.class,now);
       //getting claimNumber
       ClaimNumber claimNumber= new ClaimNumber(claimNumberInString);
       ClaimType claimType=createCommand.getClaimType();
       //getting category and relationship
       String category=createCommand.getCategory();
       String relationship=createCommand.getRelationship();
       //creating policy
       Map policyMap=glClaimFinder.findPolicyByPolicyNumber(createCommand.getPolicyNumber());
       String policyIdStr=policyMap.get("_id").toString();

       //calculate for early death claim
       DateTime policyInceptionDate = policyMap.get("inceptionOn") != null ? new DateTime(policyMap.get("inceptionOn")) : null;
       DateTime policyExpiredDate = policyMap.get("expiredOn") != null ? new DateTime(policyMap.get("expiredOn")) : null;
       DateTime claimIncidenceDate=createCommand.getClaimIncidenceDate()!=null?new DateTime(createCommand.getClaimIncidenceDate()) : null;
       DateTime claimIntimationDate=createCommand.getClaimIntimationDate()!=null?new DateTime(createCommand.getClaimIntimationDate()) : null;
       //DateTime deathDate=new DateTime();
       int noOfDays= Days.daysBetween(new LocalDate(policyInceptionDate), new LocalDate(claimIncidenceDate)).getDays();
       int earlyClaimDays =glClaimService.daysRequiredForEarlyClaim(LineOfBusinessEnum.GROUP_LIFE,ProcessType.CLAIM);

       PolicyId policyId=new PolicyId(policyIdStr);
       PolicyNumber policyNumber=new PolicyNumber(createCommand.getPolicyNumber());
       String policyHolderName=createCommand.getClaimantDetail().getProposerName();
       Policy policy=new Policy(policyId,policyNumber,policyHolderName);
       Proposer proposerFromDb=(Proposer)policyMap.get("proposer");
       String proposalCode=proposerFromDb.getProposerCode();
       //creating proposer
       ProposerBuilder proposerBuilder= Proposer.getProposerBuilder(createCommand.getClaimantDetail().getProposerName(),proposalCode);
       proposerBuilder.withContactDetail(createCommand.getClaimantDetail().getAddressLine1(),createCommand.getClaimantDetail().getAddressLine2(),
               createCommand.getClaimantDetail().getPostalCode(),createCommand.getClaimantDetail().getProvince(),createCommand.getClaimantDetail().getTown()
       ,createCommand.getClaimantDetail().getEmailId());

     List<ContactPersonDetailDto> contactDetailLists=new ArrayList<>();

       List<ContactPersonDetail> contactDetailList=new ArrayList<>();
       contactDetailList=createCommand.getClaimantDetail().getContactPersonDetail();
       for(ContactPersonDetail contactPersonDetail:contactDetailList){
           ContactPersonDetailDto contactPersonDetailDto=new ContactPersonDetailDto(contactPersonDetail.getContactPersonName(),contactPersonDetail.getContactPersonEmail(),contactPersonDetail.getContactPersonMobileNumber(),contactPersonDetail.getContactPersonWorkPhoneNumber())  ;
           contactDetailLists.add(contactPersonDetailDto);
       }
       proposerBuilder.withContactPersonDetail(contactDetailLists);
       Proposer proposer=proposerBuilder.build();

       //getting assured detail
       ClaimAssuredDetailDto claimAssuredDetail=createCommand.getClaimAssuredDetail();
       LocalDate dateFromCommand=null;
       LocalDate dateOfBirthDate=claimAssuredDetail.getDateOfBirth();
       if(dateOfBirthDate!=null){
           dateFromCommand=dateOfBirthDate;
       }

       DateTime dateTimeOfBirth=claimAssuredDetail.getDateOfBirthInDateTime();
       if(dateTimeOfBirth!=null){
       LocalDate resultDate=dateTimeOfBirth.toLocalDate();
           dateFromCommand=resultDate;
       }
       ClaimMainAssuredDetailDto claimMainAssuredDetail= claimAssuredDetail.getClaimMainAssuredDetail();

       ClaimMainAssuredDetail claimMainAssuredDetails=new ClaimMainAssuredDetail(claimMainAssuredDetail.getFullName(),claimMainAssuredDetail.getRelationship(),claimMainAssuredDetail.getNrcNumber(),claimMainAssuredDetail.getManNumber(),claimMainAssuredDetail.getLastSalary());
       ClaimAssuredDetail assuredDetail=new ClaimAssuredDetail(createCommand.getClaimAssuredDetail().getTitle(),createCommand.getClaimAssuredDetail().getFirstName(),
               createCommand.getClaimAssuredDetail().getSurName() ,dateFromCommand ,createCommand.getClaimAssuredDetail().getAgeOnNextBirthDate(),
               createCommand.getClaimAssuredDetail().getNrcNumber(), createCommand.getClaimAssuredDetail().getGender(), createCommand.getClaimAssuredDetail().getSumAssured(),
               createCommand.getClaimAssuredDetail().getReserveAmount(), createCommand.getClaimAssuredDetail().getCategory(),
               createCommand.getClaimAssuredDetail().getManNumber(), createCommand.getClaimAssuredDetail().getLastSalary(),
               createCommand.getClaimAssuredDetail().getOccupation(), claimMainAssuredDetails);

       //ClaimAssuredDetail claimAssuredDetail=new ClaimAssuredDetail();
       //getting planPremium and CoveragePremium detail
       PlanDetailDto planDetailDto=createCommand.getPlanDetail();
       //calculating paln and coverage sum assured for claim amount
       List<CoverageId> coverageIdList=new ArrayList<CoverageId>();
       String planIdInString=planDetailDto.getPlanId();
       PlanId  planId=new PlanId(planIdInString);
       Map planMap=planFinder.findPlanByPlanId(planId);
       List<LinkedHashMap> coverageList=(List<LinkedHashMap>)planMap.get("coverages");
       for(LinkedHashMap planCoverage:coverageList) {
           String dbCoverageId=(String )planCoverage.get("coverageId");
           CoverageId tempCoverageId=new CoverageId(dbCoverageId);
          String dbCoverageCover=(String)planCoverage.get("coverageCover");
          if(dbCoverageCover.equals("ACCELERATED"))
           coverageIdList.add(tempCoverageId) ;
       }

       PlanDetail planDetail=new PlanDetail(planId,planDetailDto.getPlanName(),planDetailDto.getPlanCode(),planDetailDto.getPremiumAmount(),planDetailDto.getSumAssured());
       Set<CoverageDetailDto> coverageDetailDtoList=createCommand.getCoverageDetails();
       Set<CoverageDetail> coverages=new LinkedHashSet<CoverageDetail>();
       BigDecimal tempSumAssured=BigDecimal.ZERO;
       String coverageIdInString=null;
       CoverageId coverageId=null;
       if(coverageDetailDtoList!=null){
           for(CoverageDetailDto coverageDetailDto:coverageDetailDtoList){
               coverageIdInString=coverageDetailDto.getCoverageId();
               coverageId=new CoverageId(coverageIdInString);
               CoverageDetail coverageDetail=new CoverageDetail(coverageDetailDto.getCoverageCode(),coverageId,
                       coverageDetailDto.getCoverageName(),coverageDetailDto.getPremium(),coverageDetailDto.getSumAssured());
               if(coverageDetailDto.getSumAssured()!=null){
                   tempSumAssured=tempSumAssured.add(coverageDetailDto.getSumAssured());
               }
               coverages.add(coverageDetail);

           }
       }

       BankDetails bankDetails=new BankDetails(createCommand.getBankDetails().getBankName(),createCommand.getBankDetails().getBankBranchName(),
               createCommand.getBankDetails().getBankAccountType(),createCommand.getBankDetails().getBankAccountNumber());

       //get sum assured amount
       BigDecimal tempReserveSum=planDetail.getSumAssured();


       tempReserveSum= tempReserveSum.add(tempSumAssured);


       if(claimType == ClaimType.DEATH ||claimType== ClaimType.FUNERAL){
           reserveSum=tempReserveSum;
       }


      //adding mandatory documents, if any
       Set<GLClaimDocument> claimDocuments= Sets.newHashSet();
       Set<GLClaimDocumentCommand>uploadedDocuments=createCommand.getUploadedDocuments();
       if(uploadedDocuments !=null) {
               for(GLClaimDocumentCommand document: uploadedDocuments){
                   String requiredDocumentId = document.getDocumentId();
                   try {
                       GLClaimDocument claimDocument = glClaimService.getAddedDocument(document.getFile(), requiredDocumentId, document.isMandatory());
                       claimDocuments.add(claimDocument) ;
                   } catch(IOException exception){
                     exception.printStackTrace();
                   }
               }
           }

       //creating groupLife Claim
       GroupLifeClaim groupLifeClaim=new GroupLifeClaim(claimId,claimNumber,claimType,claimIntimationDate);
       groupLifeClaim.withIncidenceDate(claimIncidenceDate);
       groupLifeClaim.withCategoryAndRelationship(category,relationship);
       groupLifeClaim.withProposerAndPolicy(proposer, policy);
       groupLifeClaim.withAssuredDetail(assuredDetail);
       groupLifeClaim.withPlanDetail(planDetail);
       groupLifeClaim.withCoverageDetails(coverages);
       groupLifeClaim.withBankDetails(bankDetails);
       groupLifeClaim.withClaimDocuments(claimDocuments);
       groupLifeClaim.updateWithReserveAmount(reserveSum);
       claimAmount=reserveSum;
       groupLifeClaim.updateWithClaimAmount(claimAmount);

       if(earlyClaimDays>noOfDays){
           groupLifeClaim.withEarlyClaim(true);
       }
       return groupLifeClaim;
    }

    public GroupLifeClaim updateClaimDetails(GroupLifeClaim groupLifeClaim,GLClaimUpdateCommand glClaimUpdateCommand){
        ClaimRegistration claimRegistration=null;
        ClaimRegistrationDto incidentDetails=glClaimUpdateCommand.getIncidentDetails();
         if(incidentDetails!=null){
               claimRegistration=new ClaimRegistration(incidentDetails.getCauseOfDeath(),incidentDetails.getPlaceOfDeath(),incidentDetails.getDateOfDeath(), incidentDetails.getTimeOfDeath(), incidentDetails.getDurationOfIllness(),incidentDetails.getNameOfDoctorAndHospitalAddress(),incidentDetails.getContactNumber(),
                       incidentDetails.getFirstConsultation(),incidentDetails.getTreatmentTaken(), incidentDetails.getIsCauseOfDeathAccidental(),incidentDetails.getTypeOfAccident(),
                       incidentDetails.getPlaceOfAccident(), incidentDetails.getDateOfAccident(),incidentDetails.getTimeOfAccident(), incidentDetails.getIsPostMortemAutopsyDone(),
                       incidentDetails.getIsPoliceReportRegistered(), incidentDetails.getRegistrationNumber(),incidentDetails.getPoliceStationName());
           }
        groupLifeClaim.withClaimRegistration(claimRegistration);

        DisabilityClaimRegistration disabilityClaimRegistration=null;
        ClaimDisabilityRegistrationDto disabilityIncidentDetails=glClaimUpdateCommand.getDisabilityIncidentDetails();
        if(disabilityIncidentDetails!=null){
            disabilityClaimRegistration=new  DisabilityClaimRegistration(
                    disabilityIncidentDetails.getDateOfDisability(),
                    disabilityIncidentDetails.getNatureOfDisability(),
                    disabilityIncidentDetails.getExtendOfDisability(),
                    disabilityIncidentDetails.getDateOfDiagnosis(),
                    disabilityIncidentDetails.getExactDiagnosis(),
                    disabilityIncidentDetails.getNameOfDoctorAndHospitalAddress(),
                    disabilityIncidentDetails.getContactNumberOfHospital(),
                    disabilityIncidentDetails.getDateOfFirstConsultation(),
                    disabilityIncidentDetails.getTreatmentTaken(),
                    disabilityIncidentDetails.getCapabilityOfAssuredDailyLiving(),
                    disabilityIncidentDetails.getAssuredGainfulActivities(),
                    disabilityIncidentDetails.getDetailsOfWorkActivities(),
                    disabilityIncidentDetails.getFromActivitiesDate(),
                    disabilityIncidentDetails.getAssuredConfinedToIndoor(),
                    disabilityIncidentDetails.getFromIndoorDate(),
                    disabilityIncidentDetails.getAssuredIndoorDetails(),
                    disabilityIncidentDetails.getAssuredAbleToGetOutdoor(),
                    disabilityIncidentDetails.getFromOutdoorDate(),
                    disabilityIncidentDetails.getAssuredOutdoorDetails(),
                    disabilityIncidentDetails.getVisitingMedicalOfficerDetails());
        }
        groupLifeClaim.withDisabilityClaimRegistration(disabilityClaimRegistration);
         return   groupLifeClaim;
        }
      public GroupLifeClaim createAmendment(GLClaimAmendmentCommand glClaimApprovalCommand,GroupLifeClaim groupLifeClaim){
          LocalDate now=LocalDate.now();
          String claimNumberInString=claimNumberGenerator.getClaimNumber(GroupLifeClaim.class,now);
          //getting claimNumber
          ClaimNumber claimNumber= new ClaimNumber(claimNumberInString);
          groupLifeClaim.updateWithOldClaimNumberForAmendment(claimNumber);
          groupLifeClaim.updateWithRecoveredAmount(glClaimApprovalCommand.getTotalRecoveredAmount());
          return groupLifeClaim;
      }
    public GroupLifeClaim makeAmendment(GLClaimApprovalCommand glClaimApprovalCommand,GroupLifeClaim groupLifeClaim){
        LocalDate now=LocalDate.now();
        String claimNumberInString=claimNumberGenerator.getClaimNumber(GroupLifeClaim.class,now);
        //getting claimNumber
        ClaimNumber newClaimNumber= new ClaimNumber(claimNumberInString);

        //getting claimId
        ClaimId claimId = new ClaimId(ObjectId.get().toString());
        ClaimType claimType=groupLifeClaim.getClaimType();
        ClaimStatus claimStatus=ClaimStatus.UNDERWRITING;

        DateTime claimIncidenceDate=groupLifeClaim.getIncidenceDate()!=null?new DateTime(groupLifeClaim.getIncidenceDate()):null;
        DateTime claimIntimationDate=groupLifeClaim.getIntimationDate()!=null?new DateTime(groupLifeClaim.getIntimationDate()):null;
        GroupLifeClaim newGroupLifeClaim=new GroupLifeClaim(claimId,newClaimNumber,claimType,claimIntimationDate);
        newGroupLifeClaim.withIncidenceDate(claimIncidenceDate);
        newGroupLifeClaim.withClaimStatus(claimStatus);
        String category=groupLifeClaim.getCategory();
        String relationship=groupLifeClaim.getRelationship();
        if(category!=null &&relationship!=null ) {
            newGroupLifeClaim.withCategoryAndRelationship(category, relationship);
        }
        ClaimNumber oldClaimNumber=groupLifeClaim.getClaimNumber();
        newGroupLifeClaim.updateWithOldClaimNumberForAmendment(oldClaimNumber);
        Proposer proposer=groupLifeClaim.getProposer();
        Policy policy=groupLifeClaim.getPolicy();
        if(proposer!=null && policy!=null ) {
            newGroupLifeClaim.withProposerAndPolicy(proposer, policy);
        }
        ClaimAssuredDetail assuredDetail=groupLifeClaim.getAssuredDetail();
        if(assuredDetail!=null){
            newGroupLifeClaim.withAssuredDetail(assuredDetail);
        }
        PlanDetail planDetail=groupLifeClaim.getPlanDetail();
        if(planDetail!=null){
            newGroupLifeClaim.withPlanDetail(planDetail);
        }
        Set<CoverageDetail> coverageDetails=groupLifeClaim.getCoverageDetails();
        if(coverageDetails!=null){
            newGroupLifeClaim.withCoverageDetails(coverageDetails);
        }
        BankDetails bankDetails=groupLifeClaim.getBankDetails();
        if(bankDetails!=null){
            newGroupLifeClaim.withBankDetails(bankDetails);
        }
        Set<GLClaimDocument> claimDocuments=groupLifeClaim.getClaimDocuments();
        if(claimDocuments!=null){
            newGroupLifeClaim.withClaimDocuments(claimDocuments);
        }
        BigDecimal reserveAmount=groupLifeClaim.getReserveAmount();
        if(reserveAmount!=null){
            newGroupLifeClaim.updateWithReserveAmount(reserveAmount);

        }
         BigDecimal claimAmount=groupLifeClaim.getClaimAmount();
        if(claimAmount!=null){
            newGroupLifeClaim.updateWithClaimAmount(reserveAmount);

        }

        BigDecimal recoveredAmount=groupLifeClaim.getRecoveredAmount();
        if(recoveredAmount!=null){
            newGroupLifeClaim.updateWithRecoveredAmount(recoveredAmount);

        }

        ClaimRegistration claimRegistration=groupLifeClaim.getClaimRegistration();
        if(claimRegistration!=null){
            newGroupLifeClaim.withClaimRegistration(claimRegistration);

        }

         DisabilityClaimRegistration disabilityClaimRegistration=groupLifeClaim.getDisabilityClaimRegistration();
        if(disabilityClaimRegistration!=null){
            newGroupLifeClaim.withDisabilityClaimRegistration(disabilityClaimRegistration);

        }

        DateTime submittedOn=groupLifeClaim.getSubmittedOn()!=null?new DateTime(groupLifeClaim.getSubmittedOn()):null;
        newGroupLifeClaim.withSubmittedOn(submittedOn);

         boolean isEarlyDeathClaim=groupLifeClaim.isEarlyDeathClaim();
        newGroupLifeClaim.withEarlyClaim(isEarlyDeathClaim);

        boolean isLateClaim=groupLifeClaim.isLateClaim();
        newGroupLifeClaim.withLateClaim(isLateClaim);


        //groupLifeClaim.updateWithRecoveredAmount(glClaimApprovalCommand.getTotalRecoveredAmount());
        return newGroupLifeClaim;


    }
    }

/*
BigDecimal reserveSum=BigDecimal.ZERO;;
        BigDecimal claimAmount=BigDecimal.ZERO;
        BigDecimal updatedClaimAmount=BigDecimal.ZERO;

       //getting claimId
       ClaimId claimId = new ClaimId(ObjectId.get().toString());
       LocalDate now=LocalDate.now();
       String claimNumberInString=claimNumberGenerator.getClaimNumber(GroupLifeClaim.class,now);
       //getting claimNumber
       ClaimNumber claimNumber= new ClaimNumber(claimNumberInString);
       ClaimType claimType=createCommand.getClaimType();
       //getting category and relationship
       String category=createCommand.getCategory();
       String relationship=createCommand.getRelationship();
       //creating policy
       Map policyMap=glClaimFinder.findPolicyByPolicyNumber(createCommand.getPolicyNumber());
       String policyIdStr=policyMap.get("_id").toString();

       //calculate for early death claim
       DateTime policyInceptionDate = policyMap.get("inceptionOn") != null ? new DateTime(policyMap.get("inceptionOn")) : null;
       DateTime policyExpiredDate = policyMap.get("expiredOn") != null ? new DateTime(policyMap.get("expiredOn")) : null;
       DateTime claimIncidenceDate=createCommand.getClaimIncidenceDate()!=null?new DateTime(createCommand.getClaimIncidenceDate()) : null;
       DateTime claimIntimationDate=createCommand.getClaimIntimationDate()!=null?new DateTime(createCommand.getClaimIntimationDate()) : null;
       //DateTime deathDate=new DateTime();
       int noOfDays= Days.daysBetween(new LocalDate(policyInceptionDate), new LocalDate(claimIncidenceDate)).getDays();
       int earlyClaimDays =glClaimService.daysRequiredForEarlyClaim(LineOfBusinessEnum.GROUP_LIFE,ProcessType.CLAIM);

       PolicyId policyId=new PolicyId(policyIdStr);
       PolicyNumber policyNumber=new PolicyNumber(createCommand.getPolicyNumber());
       String policyHolderName=createCommand.getClaimantDetail().getProposerName();
       Policy policy=new Policy(policyId,policyNumber,policyHolderName);
       Proposer proposerFromDb=(Proposer)policyMap.get("proposer");
       String proposalCode=proposerFromDb.getProposerCode();
       //creating proposer
       ProposerBuilder proposerBuilder= Proposer.getProposerBuilder(createCommand.getClaimantDetail().getProposerName(),proposalCode);
       proposerBuilder.withContactDetail(createCommand.getClaimantDetail().getAddressLine1(),createCommand.getClaimantDetail().getAddressLine2(),
               createCommand.getClaimantDetail().getPostalCode(),createCommand.getClaimantDetail().getProvince(),createCommand.getClaimantDetail().getTown()
       ,createCommand.getClaimantDetail().getEmailId());

     List<ContactPersonDetailDto> contactDetailLists=new ArrayList<>();

       List<ContactPersonDetail> contactDetailList=new ArrayList<>();
       contactDetailList=createCommand.getClaimantDetail().getContactPersonDetail();
       for(ContactPersonDetail contactPersonDetail:contactDetailList){
           ContactPersonDetailDto contactPersonDetailDto=new ContactPersonDetailDto(contactPersonDetail.getContactPersonName(),contactPersonDetail.getContactPersonEmail(),contactPersonDetail.getContactPersonMobileNumber(),contactPersonDetail.getContactPersonWorkPhoneNumber())  ;
           contactDetailLists.add(contactPersonDetailDto);
       }
       proposerBuilder.withContactPersonDetail(contactDetailLists);
       Proposer proposer=proposerBuilder.build();

       //getting assured detail
       ClaimAssuredDetailDto claimAssuredDetail=createCommand.getClaimAssuredDetail();
       LocalDate dateFromCommand=null;
       LocalDate dateOfBirthDate=claimAssuredDetail.getDateOfBirth();
       if(dateOfBirthDate!=null){
           dateFromCommand=dateOfBirthDate;
       }

       DateTime dateTimeOfBirth=claimAssuredDetail.getDateOfBirthInDateTime();
       if(dateTimeOfBirth!=null){
       LocalDate resultDate=dateTimeOfBirth.toLocalDate();
           dateFromCommand=resultDate;
       }
       ClaimMainAssuredDetailDto claimMainAssuredDetail= claimAssuredDetail.getClaimMainAssuredDetail();

       ClaimMainAssuredDetail claimMainAssuredDetails=new ClaimMainAssuredDetail(claimMainAssuredDetail.getFullName(),claimMainAssuredDetail.getRelationship(),claimMainAssuredDetail.getNrcNumber(),claimMainAssuredDetail.getManNumber(),claimMainAssuredDetail.getLastSalary());
       ClaimAssuredDetail assuredDetail=new ClaimAssuredDetail(createCommand.getClaimAssuredDetail().getTitle(),createCommand.getClaimAssuredDetail().getFirstName(),
               createCommand.getClaimAssuredDetail().getSurName() ,dateFromCommand ,createCommand.getClaimAssuredDetail().getAgeOnNextBirthDate(),
               createCommand.getClaimAssuredDetail().getNrcNumber(), createCommand.getClaimAssuredDetail().getGender(), createCommand.getClaimAssuredDetail().getSumAssured(),
               createCommand.getClaimAssuredDetail().getReserveAmount(), createCommand.getClaimAssuredDetail().getCategory(),
               createCommand.getClaimAssuredDetail().getManNumber(), createCommand.getClaimAssuredDetail().getLastSalary(),
               createCommand.getClaimAssuredDetail().getOccupation(), claimMainAssuredDetails);

       //ClaimAssuredDetail claimAssuredDetail=new ClaimAssuredDetail();
       //getting planPremium and CoveragePremium detail
       PlanDetailDto planDetailDto=createCommand.getPlanDetail();
       //calculating paln and coverage sum assured for claim amount
       List<CoverageId> coverageIdList=new ArrayList<CoverageId>();
       String planIdInString=planDetailDto.getPlanId();
       PlanId  planId=new PlanId(planIdInString);
       Map planMap=planFinder.findPlanByPlanId(planId);
       List<LinkedHashMap> coverageList=(List<LinkedHashMap>)planMap.get("coverages");
       for(LinkedHashMap planCoverage:coverageList) {
           String dbCoverageId=(String )planCoverage.get("coverageId");
           CoverageId tempCoverageId=new CoverageId(dbCoverageId);
          String dbCoverageCover=(String)planCoverage.get("coverageCover");
          if(dbCoverageCover.equals("ACCELERATED"))
           coverageIdList.add(tempCoverageId) ;
       }

       PlanDetail planDetail=new PlanDetail(planId,planDetailDto.getPlanName(),planDetailDto.getPlanCode(),planDetailDto.getPremiumAmount(),planDetailDto.getSumAssured());
       Set<CoverageDetailDto> coverageDetailDtoList=createCommand.getCoverageDetails();
       Set<CoverageDetail> coverages=new LinkedHashSet<CoverageDetail>();
       BigDecimal tempSumAssured=BigDecimal.ZERO;
       String coverageIdInString=null;
       CoverageId coverageId=null;
       if(coverageDetailDtoList!=null){
           for(CoverageDetailDto coverageDetailDto:coverageDetailDtoList){
               coverageIdInString=coverageDetailDto.getCoverageId();
               coverageId=new CoverageId(coverageIdInString);
               CoverageDetail coverageDetail=new CoverageDetail(coverageDetailDto.getCoverageCode(),coverageId,
                       coverageDetailDto.getCoverageName(),coverageDetailDto.getPremium(),coverageDetailDto.getSumAssured());
               if(coverageDetailDto.getSumAssured()!=null){
                   tempSumAssured=tempSumAssured.add(coverageDetailDto.getSumAssured());
               }
               coverages.add(coverageDetail);

           }
       }

       BankDetails bankDetails=new BankDetails(createCommand.getBankDetails().getBankName(),createCommand.getBankDetails().getBankBranchName(),
               createCommand.getBankDetails().getBankAccountType(),createCommand.getBankDetails().getBankAccountNumber());

       //get sum assured amount
       BigDecimal tempReserveSum=planDetail.getSumAssured();


       tempReserveSum= tempReserveSum.add(tempSumAssured);


       if(claimType == ClaimType.DEATH ||claimType== ClaimType.FUNERAL){
           reserveSum=tempReserveSum;
       }


      //adding mandatory documents, if any
       Set<GLClaimDocument> claimDocuments= Sets.newHashSet();
       Set<GLClaimDocumentCommand>uploadedDocuments=createCommand.getUploadedDocuments();
       if(uploadedDocuments !=null) {
               for(GLClaimDocumentCommand document: uploadedDocuments){
                   String requiredDocumentId = document.getDocumentId();
                   try {
                       GLClaimDocument claimDocument = glClaimService.getAddedDocument(document.getFile(), requiredDocumentId, document.isMandatory());
                       claimDocuments.add(claimDocument) ;
                   } catch(IOException exception){
                     exception.printStackTrace();
                   }
               }
           }

       //creating groupLife Claim
       GroupLifeClaim groupLifeClaim=new GroupLifeClaim(claimId,claimNumber,claimType,claimIntimationDate);
       groupLifeClaim.withIncidenceDate(claimIncidenceDate);
       groupLifeClaim.withCategoryAndRelationship(category,relationship);
       groupLifeClaim.withProposerAndPolicy(proposer, policy);
       groupLifeClaim.withAssuredDetail(assuredDetail);
       groupLifeClaim.withPlanDetail(planDetail);
       groupLifeClaim.withCoverageDetails(coverages);
       groupLifeClaim.withBankDetails(bankDetails);
       groupLifeClaim.withClaimDocuments(claimDocuments);
       groupLifeClaim.updateWithReserveAmount(reserveSum);
       claimAmount=reserveSum;
       groupLifeClaim.updateWithClaimAmount(claimAmount);

       if(earlyClaimDays>noOfDays){
           groupLifeClaim.withEarlyClaim(true);
       }
       return groupLifeClaim;
 */