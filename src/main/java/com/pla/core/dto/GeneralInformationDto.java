package com.pla.core.dto;

import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
@Setter
@ToString
public class GeneralInformationDto {

    private String productLineInformationId;
    private LineOfBusinessId productLine;
    private List<ProductLineProcessItemDto> quotationProcessItems;
    private List<ProductLineProcessItemDto> enrollmentProcessItems;
    private List<ProductLineProcessItemDto> reinstatementProcessItems;
    private List<ProductLineProcessItemDto> endorsementProcessItems;
    private List<ProductLineProcessItemDto> claimProcessItems;
    private List<PolicyFeeProcessItemDto> policyFeeProcessItems;
    private List<PolicyProcessMinimumLimitItemDto> policyProcessMinimumLimitItems;
    private List<ProductLineProcessItemDto> surrenderProcessItems;
    private List<ProductLineProcessItemDto> maturityProcessItems;

    private String organizationInformationId;
    private List<Map<DiscountFactorItem,BigDecimal>> discountFactorItems;
    private List<Map<ModalFactorItem,BigDecimal>> modelFactorItems;
    private Map<Tax, BigDecimal> serviceTax;

    public GeneralInformationDto() {
    }
}
