package com.pla.grouplife.claim.domain.model;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by Mirror on 8/19/2015.
 */
@Getter
public class PolicyHolderDetail {

    private String proposerName;

    private String addressLine1;

    private String addressLine2;

    private String postalCode;

    private String province;

    private String town;

    private String emailId;

    private String mobileNumber;

    private String workPhone;

   public PolicyHolderDetail(String proposerName, String addressLine1, String addressLine2, String postalCode, String province,
                             String town, String emailId, String mobileNumber, String workPhone){

       this.proposerName=proposerName;
       this.addressLine1=addressLine1;
       this.addressLine2=addressLine2;
       this.postalCode=postalCode;
       this.province=province;
       this.town=town;
       this.emailId=emailId;
       this.mobileNumber=mobileNumber;
       this.workPhone=workPhone;


   }
}
