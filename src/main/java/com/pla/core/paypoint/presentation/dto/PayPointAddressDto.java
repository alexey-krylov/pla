package com.pla.core.paypoint.presentation.dto;

import com.pla.core.paypoint.domain.model.PayPointAddress;
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
public class PayPointAddressDto {
    private String addressLine1;
    private String addressLine2;
    private String postalCode;
    private String province;
    private String town;

    public PayPointAddress createAndsetPropertiesToPaypointAddress() {
        PayPointAddress payPointAddress = PayPointAddress.createPaypointAddress();
        try {
            BeanUtils.copyProperties(payPointAddress, this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return payPointAddress;
    }
}
