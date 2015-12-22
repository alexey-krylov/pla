package com.pla.grouplife.claim.domain.model;

import com.pla.sharedkernel.domain.model.Relationship;
import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

/**
 * Created by ak
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Getter
@Setter(value = AccessLevel.PACKAGE)

public class MainAssuredDetails {

     String mainAssuredName;
     Relationship relationshipWithMainAssured;
     String mainAssuredNrcNumber;
     String mainAssuredManNumber;
     BigDecimal lastSalary;
}
