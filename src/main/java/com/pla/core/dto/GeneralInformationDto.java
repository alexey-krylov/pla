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
    private LineOfBusinessId lineOfBusinessId;
    private List<Map<ProductLineProcessType,Integer>> quotationProcessItems;
    private List<Map<ProductLineProcessType,Integer>> enrollmentProcessItems;
    private List<Map<ProductLineProcessType,Integer>> reinstatementProcessItems;
    private List<Map<ProductLineProcessType,Integer>> endorsementProcessItems;
    private List<Map<ProductLineProcessType,Integer>> claimProcessItems;
    private List<Map<PolicyFeeProcessType,Integer>> policyFeeProcessItems;
    private List<PolicyProcessMinimumLimitItemDto> policyProcessMinimumLimitItems;
    private List<Map<ProductLineProcessType,Integer>> surrenderProcessItems;
    private List<Map<ProductLineProcessType,Integer>> maturityProcessItems;

    private String organizationInformationId;
    private List<Map<DiscountFactorItem,BigDecimal>> discountFactorItems;
    private List<Map<ModalFactorItem,BigDecimal>> modelFactorItems;
    private Map<Tax, BigDecimal> serviceTax;


}
