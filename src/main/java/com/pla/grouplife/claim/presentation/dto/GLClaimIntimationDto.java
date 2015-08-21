//package com.pla.grouplife.claim.presentation.dto;
//
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import lombok.NoArgsConstructor;
//import lombok.Getter;
//import lombok.Setter;
//import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
//import org.nthdimenzion.presentation.LocalJodaDateSerializer;
//
//import java.time.LocalDate;
//
//@NoArgsConstructor
///**
// * Created by Mirror on 8/12/2015.
// */
//@Getter
//@Setter
//
//public class GLClaimIntimationDto {
//    private LocalDate claimIntimationDate;
//    private String memberId;
//    private String claimType;
//    private String bankName;
//    private String bankBranchName;
//    private String bankAccountType;
//    private String bankAccountNumber;
//    private String policyId;
//    private String policyNumber;
//
//
//    public GLClaimIntimationDto(String memberId,String claimType,LocalDate claimIntimationDate, String policyNumber,String policyId,String bankAccountNumber,String bankAccountType,String bankBranchName,String bankName){
//        this.claimIntimationDate=claimIntimationDate;
//        this.memberId=memberId;
//        this.claimType=claimType;
//        this.policyNumber=policyNumber;
//        this.bankName=bankName;
//        this.bankAccountNumber=bankAccountNumber;
//        this.bankAccountType=bankAccountType;
//        this.bankBranchName=bankBranchName;
//        this.policyId=policyId;
//        this.policyNumber=policyNumber;
//
//    }
//
//}
