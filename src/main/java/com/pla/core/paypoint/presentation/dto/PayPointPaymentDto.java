package com.pla.core.paypoint.presentation.dto;

import com.pla.core.paypoint.domain.model.PayPointPaymentDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Rudra on 12/11/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class PayPointPaymentDto {
    private  String ddaccId;
    private  String bankName;
    private  String bankBranchName;
    private  String bankCode;
    private String preferredPayPointMethod;

    public PayPointPaymentDetail createAndsetPropertiesToPayPointPayment() {
       PayPointPaymentDetail payPointPaymentDetail=PayPointPaymentDetail.createPayPointPayment();
        try {
            BeanUtils.copyProperties(payPointPaymentDetail, this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return payPointPaymentDetail;
    }
}
