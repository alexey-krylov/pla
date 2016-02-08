package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimDrugService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.nthdimenzion.utils.UtilValidator;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimDrugServiceDto {
    private String type;
    private String serviceName;
    private String drugName;
    private String drugType;
    private String accommodationType;
    private String duration;
    private int lengthOfStay;
    private String strength;
    private Status status;
    private BigDecimal billAmount;

    public GroupHealthCashlessClaimDrugServiceDto updateWithDetails(GroupHealthCashlessClaimDrugService groupHealthCashlessClaimDrugService) {
        if(isNotEmpty(groupHealthCashlessClaimDrugService)){
            try {
                BeanUtils.copyProperties(this, groupHealthCashlessClaimDrugService);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
    public enum Status{
        IGNORE, PROCESS;

        public static List<String> getStatusList() {
            return Arrays.stream(values()).map(Status::name).collect(Collectors.toList());
        }
    }
}
