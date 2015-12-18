package com.pla.core.paypoint.domain.model;

import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Created by Rudra on 12/11/2015.
 */

@ValueObject
@Embeddable
@NoArgsConstructor(access= AccessLevel.PACKAGE)
@ToString
@Getter
@Setter
@AllArgsConstructor
public class PayPointProfile {
   private String payPointName;
   private BigDecimal staffCompliment;
   private BigDecimal minimumIncome;
   private Integer payPointCharge;
   private String firstName;
   private String surName;
   private String phoneNumber;
   private String emailId;
   private String payPointLiasion;
   private String payPointEmailId;

   public static PayPointProfile createPayPointProfile(){
      return new PayPointProfile();
   }
}
