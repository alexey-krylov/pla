package com.pla.core.paypoint.domain.model;

import com.pla.core.paypoint.application.command.PayPointCommand;
import com.pla.core.paypoint.presentation.dto.PayPointAddressDto;
import lombok.*;
import org.apache.commons.beanutils.BeanUtils;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Rudra on 12/11/2015.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@ValueObject
@AllArgsConstructor
public class PayPointAddress {
    private String addressLine1;
    private String addressLine2;
    private String postalCode;
    private String province;
    private String town;


    public static PayPointAddress createPaypointAddress() {
     return new PayPointAddress();
    }

    public PayPointCommand createAndSetPropertiesToPayPointContactAddress(PayPointAddress contactPayPointAddress, PayPointCommand payPointCommand) {
        PayPointAddressDto contactPayPointAddressDto = new PayPointAddressDto();
        try {
            BeanUtils.copyProperties(contactPayPointAddressDto, contactPayPointAddress);
            payPointCommand.setPayPointContactAddress(contactPayPointAddressDto);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return payPointCommand;
    }

    public PayPointCommand createAndSetPropertiesToPayPointPhysicalAddress(PayPointAddress pysicalPayPointAddress, PayPointCommand payPointCommand) {
        PayPointAddressDto physicalPayPointAddressDto = new PayPointAddressDto();
        try {
            BeanUtils.copyProperties(physicalPayPointAddressDto, pysicalPayPointAddress);
            payPointCommand.setPayPointPhysicalAddress(physicalPayPointAddressDto);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return payPointCommand;
    }
}
