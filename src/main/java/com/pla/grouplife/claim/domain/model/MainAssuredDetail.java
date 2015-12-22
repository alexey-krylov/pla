package com.pla.grouplife.claim.domain.model;

import com.pla.sharedkernel.domain.model.Relationship;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import java.math.BigDecimal;

/**
 * Created by nthdimensioncompany on 1/12/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class MainAssuredDetail {
    private String  mainAssuredName;
    private Relationship relationWithMainAssured;
    private String mainAssuredNrcNumber;
    private String mainAssuredManNuber;
    private BigDecimal lastSalary;

}
