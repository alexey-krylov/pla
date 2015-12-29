package com.pla.grouplife.claim.domain.service;

import com.google.common.collect.Sets;
import com.pla.grouplife.claim.application.command.CreateGLClaimIntimationCommand;
import com.pla.grouplife.claim.application.command.GLClaimDocumentCommand;
import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.grouplife.claim.domain.model.*;
import com.pla.grouplife.claim.query.GLClaimFinder;
import com.pla.grouplife.sharedresource.dto.ContactPersonDetailDto;
import com.pla.grouplife.sharedresource.model.vo.CoveragePremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.PlanPremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.model.vo.ProposerBuilder;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public GLClaimFactory(GLFinder glFinder,GLClaimFinder glClaimFinder,SequenceGenerator sequenceGenerator,ClaimNumberGenerator claimNumberGenerator,IIdGenerator idGenerator){
        this.glFinder=glFinder;
        this.glClaimFinder=glClaimFinder;
        this.sequenceGenerator=sequenceGenerator;
        this.claimNumberGenerator=claimNumberGenerator;
        this.idGenerator=idGenerator;
    }
   public  GroupLifeClaim  createClaim(CreateGLClaimIntimationCommand createCommand){
       BigDecimal reserveSum=BigDecimal.ZERO;
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
      // DateTime policyInceptionDate=(DateTime)policyMap.get("inceptionOn");
      // DateTime policyExpiredDate=(DateTime)policyMap.get("expiredOn");
      // DateTime deathDate=new DateTime();
       //int noOfDays=Days.daysBetween(new LocalDate(policyInceptionDate), new LocalDate(deathDate)).getDays();

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


     List<ContactPersonDetailDto> contactDetailList=new ArrayList<>();
     /*  ContactPersonDetailDto contactPersonDetailDto=new ContactPersonDetailDto(createCommand.getClaimantDetail().getContactPersonName(),createCommand.getClaimantDetail().getContactPersonEmail()
                ,createCommand.getClaimantDetail().getMobileNumber(),createCommand.getClaimantDetail().getContactPersonPhone());
       contactDetailList.add(contactPersonDetailDto);
       */
       contactDetailList=createCommand.getClaimantDetail().getContactPersonDetail();
       proposerBuilder.withContactPersonDetail(contactDetailList);

      // proposerBuilder.withContactPersonDetail(createCommand.getClaimantDetail().getContactPersonName(),createCommand.getClaimantDetail().getContactPersonEmail()
      // ,createCommand.getClaimantDetail().getMobileNumber(),createCommand.getClaimantDetail().getContactPersonPhone());

       Proposer proposer=proposerBuilder.build();

       //getting assured detail

       AssuredDetail assuredDetail=new AssuredDetail(createCommand.getAssuredDetail().getCompanyName(),createCommand.getAssuredDetail().getManNumber(),
               createCommand.getAssuredDetail().getNrcNumber(),createCommand.getAssuredDetail().getSalutation(),createCommand.getAssuredDetail().getFirstName(),
               createCommand.getAssuredDetail().getLastName(),createCommand.getAssuredDetail().getDateOfBirth(), createCommand.getAssuredDetail().getGender(),createCommand.getAssuredDetail().getCategory(),
               createCommand.getAssuredDetail().getAnnualIncome(), createCommand.getAssuredDetail().getOccupationClass(),  createCommand.getAssuredDetail().getOccupationCategory(),
               createCommand.getAssuredDetail().getNoOfAssured(),createCommand.getAssuredDetail().getFamilyId(),createCommand.getAssuredDetail().getRelationship() );

       //getting planPremium and CoveragePremium detail
       PlanPremiumDetail planPremiumDetail=createCommand.getAssuredDetail().getPlanPremiumDetail();

       Set<CoveragePremiumDetail> coveragePremiumDetailList=createCommand.getAssuredDetail().getCoveragePremiumDetails();
       BankDetails bankDetails=new BankDetails(createCommand.getBankDetails().getBankName(),createCommand.getBankDetails().getBankBranchName(),
               createCommand.getBankDetails().getBankAccountType(),createCommand.getBankDetails().getBankAccountNumber());
       //ClaimIntimation Date

       DateTime claimIntimationDate = createCommand.getClaimIntimationDate();
       //claimStatus
       ClaimStatus claimStatus=ClaimStatus.INTIMATED;
       //get sum assured amount
       reserveSum=planPremiumDetail.getSumAssured();


       if(claimType == ClaimType.DEATH ||claimType== ClaimType.FUNERAL){
           reserveSum=planPremiumDetail.getSumAssured();
       }


      //adding mandatory documents, if any
       Set<GLClaimDocument> claimDocuments= Sets.newHashSet();
       Set<GLClaimDocumentCommand>uploadedDocuments=createCommand.getUploadedDocuments();
       if(uploadedDocuments.size() > 0) {
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
       GroupLifeClaim groupLifeClaim=new GroupLifeClaim(claimId,claimNumber,claimType,claimIntimationDate,claimStatus);
       groupLifeClaim.withProposerAndPolicy(proposer,policy);
       groupLifeClaim.withAssuredDetail(assuredDetail);
       groupLifeClaim.withPlanPremiumDetail(planPremiumDetail);
       groupLifeClaim.withCoveragePremiumDetails(coveragePremiumDetailList);
       groupLifeClaim.withBankDetails(bankDetails);
       groupLifeClaim.withClaimDocuments(claimDocuments);
       groupLifeClaim.updateWithReserveAmount(reserveSum);
       return groupLifeClaim;

    }
}
