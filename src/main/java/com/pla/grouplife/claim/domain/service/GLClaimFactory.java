package com.pla.grouplife.claim.domain.service;

import com.google.common.collect.Sets;
import com.pla.core.query.PlanFinder;
import com.pla.grouplife.claim.application.command.*;
import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.grouplife.claim.domain.model.*;
import com.pla.grouplife.claim.presentation.dto.ClaimAssuredDetailDto;
import com.pla.grouplife.claim.presentation.dto.ClaimMainAssuredDetailDto;
import com.pla.grouplife.claim.presentation.dto.CoverageDetailDto;
import com.pla.grouplife.claim.presentation.dto.PlanDetailDto;
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
       ClaimMainAssuredDetailDto claimMainAssuredDetail= claimAssuredDetail.getClaimMainAssuredDetail();
       ClaimMainAssuredDetail claimMainAssuredDetails=new ClaimMainAssuredDetail(claimMainAssuredDetail.getFullName(),claimMainAssuredDetail.getRelationship(),claimMainAssuredDetail.getNrcNumber(),claimMainAssuredDetail.getManNumber(),claimMainAssuredDetail.getLastSalary());
       ClaimAssuredDetail assuredDetail=new ClaimAssuredDetail(createCommand.getClaimAssuredDetail().getTitle(),createCommand.getClaimAssuredDetail().getFirstName(),
               createCommand.getClaimAssuredDetail().getSurName() ,createCommand.getClaimAssuredDetail().getDateOfBirth() ,createCommand.getClaimAssuredDetail().getAgeOnNextBirthDate(),
               createCommand.getClaimAssuredDetail().getNrcNumber(), createCommand.getClaimAssuredDetail().getGender(), createCommand.getClaimAssuredDetail().getSumAssured(),
               createCommand.getClaimAssuredDetail().getReserveAmount(), createCommand.getClaimAssuredDetail().getCategory(),
               createCommand.getClaimAssuredDetail().getManNumber(), createCommand.getClaimAssuredDetail().getLastSalary(),
               createCommand.getClaimAssuredDetail().getOccupation(), claimMainAssuredDetails);

       //ClaimAssuredDetail claimAssuredDetail=new ClaimAssuredDetail();
       //getting planPremium and CoveragePremium detail
       PlanDetailDto planDetailDto=createCommand.getPlanDetail();
       //calculating paln and coverage sum assured for claim amount
       List<CoverageId> coverageIdList=new ArrayList<CoverageId>();
       PlanId planId=planDetailDto.getPlanId();
       Map planMap=planFinder.findPlanByPlanId(planId);
       List<LinkedHashMap> coverageList=(List<LinkedHashMap>)planMap.get("coverages");
       for(LinkedHashMap planCoverage:coverageList) {
           String dbCoverageId=(String )planCoverage.get("coverageId");
           CoverageId tempCoverageId=new CoverageId(dbCoverageId);
          String dbCoverageCover=(String)planCoverage.get("coverageCover");
          if(dbCoverageCover.equals("ACCELERATED"))
           coverageIdList.add(tempCoverageId) ;
       }

       PlanDetail planDetail=new PlanDetail(planDetailDto.getPlanId(),planDetailDto.getPlanName(),planDetailDto.getPlanCode(),planDetailDto.getPremiumAmount(),planDetailDto.getSumAssured());
       Set<CoverageDetailDto> coverageDetailDtoList=createCommand.getCoverageDetails();
       Set<CoverageDetail> coverages=new LinkedHashSet<CoverageDetail>();
       BigDecimal tempSumAssured=BigDecimal.ZERO;
       if(coverageDetailDtoList!=null){
           for(CoverageDetailDto coverageDetailDto:coverageDetailDtoList){
               CoverageDetail coverageDetail=new CoverageDetail(coverageDetailDto.getCoverageCode(),coverageDetailDto.getCoverageId(),
                       coverageDetailDto.getCoverageName(),coverageDetailDto.getPremium(),coverageDetailDto.getSumAssured());
               if(coverageDetailDto.getSumAssured()!=null){
               tempSumAssured.add(coverageDetailDto.getSumAssured());}
               coverages.add(coverageDetail);

           }
       }

       BankDetails bankDetails=new BankDetails(createCommand.getBankDetails().getBankName(),createCommand.getBankDetails().getBankBranchName(),
               createCommand.getBankDetails().getBankAccountType(),createCommand.getBankDetails().getBankAccountNumber());

       //get sum assured amount
       reserveSum=planDetail.getSumAssured().add(tempSumAssured);


       if(claimType == ClaimType.DEATH ||claimType== ClaimType.FUNERAL){
           reserveSum=planDetail.getSumAssured().add(tempSumAssured);
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
       groupLifeClaim.withProposerAndPolicy(proposer,policy);
       groupLifeClaim.withAssuredDetail(assuredDetail);
       groupLifeClaim.withPlanDetail(planDetail);
       groupLifeClaim.withCoverageDetails(coverages);
       groupLifeClaim.withBankDetails(bankDetails);
       groupLifeClaim.withClaimDocuments(claimDocuments);
       groupLifeClaim.updateWithReserveAmount(reserveSum);
       groupLifeClaim.updateWithClaimAmount(reserveSum);

       if(earlyClaimDays>noOfDays){
           groupLifeClaim.withEarlyClaim(true);
       }
       return groupLifeClaim;
    }

    public GroupLifeClaim updateClaimDetails(GroupLifeClaim groupLifeClaim,GLClaimUpdateCommand glClaimUpdateCommand){
        ClaimRegistration claimRegistration=null;
        GLClaimRegistrationCommand claimCommand=glClaimUpdateCommand.getClaimCommand();
         if(claimCommand!=null){
               claimRegistration=new ClaimRegistration(claimCommand.getCauseOfDeath(),claimCommand.getPlaceOfDeath(),claimCommand.getDateOfDeath(), claimCommand.getTimeOfDeath(), claimCommand.getDurationOfIllness(), claimCommand.getNameOfDoctorAndHospitalAddress(), claimCommand.getContactNumber(),
             claimCommand.getFirstConsultation(), claimCommand.getTreatmentTaken(), claimCommand.getCauseOfDeathAccidental(), claimCommand.getTypeOfAccident(),
             claimCommand.getPlaceOfAccident(), claimCommand.getDateOfAccident(), claimCommand.getTimeOfAccident(), claimCommand.getPostMortemAutopsyDone(),
             claimCommand.getPoliceReportRegistered(), claimCommand.getRegistrationNumber(), claimCommand.getPoliceStationName());
           }
        groupLifeClaim.withClaimRegistration(claimRegistration);

        DisabilityClaimRegistration disabilityClaimRegistration=null;
        GLDisabilityClaimRegistrationCommand disableCommand=glClaimUpdateCommand.getDisableCommand();
        if(disableCommand!=null){
            disabilityClaimRegistration=new  DisabilityClaimRegistration(
                    disableCommand.getDateOfDisability(),
            disableCommand.getNatureOfDisability(),
            disableCommand.getExtendOfDisability(),
            disableCommand.getDateOfDiagnosis(),
            disableCommand.getExactDiagnosis(),
            disableCommand.getNameOfDoctorAndHospitalAddress(),
            disableCommand.getContactNumberOfHospital(),
            disableCommand.getDateOfFirstConsultation(),
            disableCommand.getTreatmentTaken(),
            disableCommand.getCapabilityOfAssuredDailyLiving(),
            disableCommand.getAssuredGainfulActivities(),
            disableCommand.getDetailsOfWorkActivities(),
            disableCommand.getFromActivitiesDate(),
            disableCommand.getAssuredConfinedToIndoor(),
            disableCommand.getFromIndoorDate(),
            disableCommand.getAssuredIndoorDetails(),
            disableCommand.getAssuredAbleToGetOutdoor(),
            disableCommand.getFromOutdoorDate(),
            disableCommand.getAssuredOutdoorDetails(),
            disableCommand.getVisitingMedicalOfficerDetails());
        }
        groupLifeClaim.withDisabilityClaimRegistration(disabilityClaimRegistration);
         return   groupLifeClaim;
        }
      public GroupLifeClaim createAmendment(GLClaimApprovalCommand glClaimApprovalCommand,GroupLifeClaim groupLifeClaim){
          LocalDate now=LocalDate.now();
          String claimNumberInString=claimNumberGenerator.getClaimNumber(GroupLifeClaim.class,now);
          //getting claimNumber
          ClaimNumber claimNumber= new ClaimNumber(claimNumberInString);
          groupLifeClaim.updateWithNewClaimNumberForAmendment(claimNumber);
          groupLifeClaim.updateWithRecoveredAmount(glClaimApprovalCommand.getTotalRecoveredAmount());
          return groupLifeClaim;
      }
    }

