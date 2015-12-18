package com.pla.core.paypoint.presentation.dto;

import com.pla.core.paypoint.application.command.PayPointCommand;
import com.pla.core.paypoint.domain.model.PayPointProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

/**
 * Created by Rudra on 12/11/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class PayPointProfileDto {
    private String  payPointName;
    private BigDecimal staffCompliment;
    private BigDecimal minimumIncome;
    private Integer payPointCharge;
    private String firstName;
    private String surName;
    private String phoneNumber;
    private String emailId;
    private String payPointLiasion;
    private String payPointEmailId;

    public PayPointProfile createAndsetPropertiesToPaypointProfile() {
        PayPointProfile payPointProfile = PayPointProfile.createPayPointProfile();
        try {
            BeanUtils.copyProperties(payPointProfile, this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return payPointProfile;
    }

    public PayPointCommand createAndSetPropertiesToPaypointProfileDto(PayPointProfile payPointProfile, PayPointCommand payPointCommand) {
        PayPointProfileDto payPointProfileDto = new PayPointProfileDto();
        try {
            BeanUtils.copyProperties(payPointProfileDto, payPointProfile);
            payPointCommand.setPayPointProfileDto(payPointProfileDto);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return payPointCommand;
    }
}
