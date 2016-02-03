package com.pla.grouplife.claim.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.util.List;

/**
 * Created by Mirror on 8/19/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class ClaimantDetail {

    private String proposerName;

    private String addressLine1;

    private String addressLine2;

    private String postalCode;

    private String province;

    private String town;

    private String emailId;

    private String mobileNumber;

   // private String workPhone;


    List<ContactPersonDetail> contactPersonDetail;

   public ClaimantDetail(String proposerName, String addressLine1, String addressLine2, String postalCode, String province,
                         String town, String emailId, String mobileNumber){

       this.proposerName=proposerName;
       this.addressLine1=addressLine1;
       this.addressLine2=addressLine2;
       this.postalCode=postalCode;
       this.province=province;
       this.town=town;
       this.emailId=emailId;
       this.mobileNumber=mobileNumber;
       //this.workPhone=workPhone;


   }
  public ClaimantDetail withContactPersonDetails( List<ContactPersonDetail> contactPersonDetail){
      this.contactPersonDetail=contactPersonDetail;
      return this;
  }

}
