package com.pla.core.presentation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pla.core.dto.CommissionTermDto;
import com.pla.core.query.PlanFinder;
import com.pla.sharedkernel.domain.model.CommissionDesignation;
import com.pla.sharedkernel.domain.model.CommissionTermType;
import com.pla.sharedkernel.domain.model.CommissionType;
import com.pla.sharedkernel.domain.model.PremiumFee;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
import org.nthdimenzion.presentation.LocalJodaDateSerializer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by User on 4/7/2015.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"commissionId", "planId", "planName", "availableFor", "commissionType", "fromDate", "premiumFee"})
public class CommissionDto {

    String commissionId;
    String planId;
    String planName;
    CommissionDesignation availableFor;
    CommissionType commissionType;
    PremiumFee premiumFee;

    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    @JsonSerialize(using = LocalJodaDateSerializer.class)
    LocalDate fromDate;

    Set<CommissionTermDto> commissionTermSet;


    public static List<CommissionDto> transformToCommissionDto(List<Map<String, Object>> allCommissions, List<Map<String, Object>> allCommissionTerms, PlanFinder planFinder) {
        List<CommissionDto> commissionDtos = allCommissions.stream().map(new TransformAllCommissionToCommissionDto(allCommissionTerms, planFinder)).collect(Collectors.toList());
        return commissionDtos;
    }

    public static CommissionDto transformToCommissionDto(Map<String, Object> commission, List<Map<String, Object>> allCommissionTerms, PlanFinder planFinder) {
        CommissionDto commissionDto = new CommissionDto();
        commissionDto.setCommissionId((String) commission.get("commissionId"));
        commissionDto.setPlanId((String) commission.get("planId"));
        Map plan = planFinder.findPlanByPlanId(new PlanId(commissionDto.getPlanId()));
        Map planDetail = ((Map) plan.get("planDetail"));
        commissionDto.setPlanName((String) (planDetail.get("planName")));
        commissionDto.setAvailableFor(CommissionDesignation.valueOf((String) commission.get("availableFor")));
        commissionDto.setCommissionType(CommissionType.valueOf((String) commission.get("commissionType")));
        commissionDto.setPremiumFee(PremiumFee.valueOf((String) commission.get("premiumFee")));
        commissionDto.setFromDate(new LocalDate(commission.get("fromDate")));
        List<Map<String, Object>> commissionTermsByCommissionId = allCommissionTerms.stream().filter(new FilterCommissionTermByCommissionId((String) commission.get("commissionId"))).collect(Collectors.toList());
        commissionDto.setCommissionTermSet(commissionTermsByCommissionId.stream().map(new CommissionTermTransformer()).collect(Collectors.toSet()));
        return commissionDto;
    }

    public static CommissionTermDto transformInToCommissionTermDto(Map<String, Object> commissionTermDtos) {
        CommissionTermDto commissionTermDto = new CommissionTermDto();
        commissionTermDto.setCommissionTermType(CommissionTermType.valueOf(commissionTermDtos.get("commissionTermType").toString()));
        commissionTermDto.setCommissionPercentage((BigDecimal) (commissionTermDtos.get("commissionPercentage")));
        commissionTermDto.setStartYear((Integer) commissionTermDtos.get("startYear"));
        commissionTermDto.setEndYear((Integer) commissionTermDtos.get("endYear"));
        return commissionTermDto;
    }

    private static class TransformAllCommissionToCommissionDto implements Function<Map<String, Object>, CommissionDto> {
        private List<Map<String, Object>> allCommissionTerms;
        private PlanFinder planFinder;

        TransformAllCommissionToCommissionDto(List<Map<String, Object>> allCommissionTerms, PlanFinder planFinder) {
            this.allCommissionTerms = allCommissionTerms;
            this.planFinder = planFinder;
        }

        @Override
        public CommissionDto apply(Map<String, Object> commission) {
            return CommissionDto.transformToCommissionDto(commission, allCommissionTerms, planFinder);
        }
    }

    private static class CommissionTermTransformer implements Function<Map<String, Object>, CommissionTermDto> {

        @Override
        public CommissionTermDto apply(Map<String, Object> commissionTermDtos) {
            return transformInToCommissionTermDto(commissionTermDtos);
        }
    }

    private static class FilterCommissionTermByCommissionId implements Predicate<Map<String, Object>> {

        private String commissionId;

        FilterCommissionTermByCommissionId(String commissionId) {
            this.commissionId = commissionId;
        }

        @Override
        public boolean test(Map<String, Object> commissionTerms) {
            return commissionId.equals(commissionTerms.get("commissionId"));
        }
    }
}
