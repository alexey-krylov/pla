package com.pla.core.paypoint.domain.model;

import com.pla.core.paypoint.application.command.PayPointCommand;
import com.pla.core.paypoint.presentation.dto.PayPointPaymentDto;
import lombok.*;
import org.apache.commons.beanutils.BeanUtils;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Rudra on 12/11/2015.
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
@ValueObject
@AllArgsConstructor
@EqualsAndHashCode
public class PayPointPaymentDetail {
    private  String ddaccId;
    private  String bankName;
    private  String bankBranchName;
    private  String bankCode;
    private String preferredPayPointMethod;

    public static PayPointPaymentDetail createPayPointPayment() {
        return new PayPointPaymentDetail();
    }

    public PayPointCommand createAndSetPropertiesToPayPointPaymentDto(PayPointPaymentDetail payPointPaymentDetail, PayPointCommand payPointCommand) {
        PayPointPaymentDto pointPaymentDto = new PayPointPaymentDto();
        try {
            BeanUtils.copyProperties(pointPaymentDto, payPointPaymentDetail);
            payPointCommand.setPayPointPaymentDto(pointPaymentDto);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return payPointCommand;
    }
}
