package com.pla.core.paypoint.application.command;

import com.pla.core.paypoint.domain.model.PayPoint;
import com.pla.core.paypoint.domain.model.*;
import com.pla.core.paypoint.presentation.dto.PayPointAddressDto;
import com.pla.core.paypoint.presentation.dto.PayPointPaymentDto;
import com.pla.core.paypoint.presentation.dto.PayPointProfileDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.LocalDate;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Rudra on 12/11/2015.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PayPointCommand {
    private String payPointId;
    private PayPointAddressDto payPointPhysicalAddress;
    private PayPointAddressDto payPointContactAddress;
    private PayPointProfileDto payPointProfileDto;
    private String payPointStatus;
    private String payPointGrade;
    private PayPointPaymentDto payPointPaymentDto;
    private LocalDate promptDateSchedules;
    private LocalDate promptDatePremium;

    public PayPointCommand createPayPoint(){
        return new PayPointCommand();
    }

    public PayPoint createAndSetPropertiesToPayPointEntity() {
        PayPointProfileDto payPointProfileDto = this.payPointProfileDto;
        PayPointProfile payPointProfile = payPointProfileDto.createAndsetPropertiesToPaypointProfile();
        PayPointAddressDto payPointPhysicalAddress= this.payPointPhysicalAddress;
        PayPointAddress payPointPhysical = payPointPhysicalAddress.createAndsetPropertiesToPaypointAddress();
        PayPointAddressDto payPointContactAddress= this.payPointContactAddress;
        PayPointAddress payPointContact = payPointContactAddress.createAndsetPropertiesToPaypointAddress();
        PayPointPaymentDto payPointPaymentDto=this.payPointPaymentDto;
        PayPointPaymentDetail payPointPaymentDetail=payPointPaymentDto.createAndsetPropertiesToPayPointPayment();
        PayPoint paypoint = new PayPoint();
        paypoint.setPayPointId(new PayPointId(this.payPointId));
        paypoint.setPayPointProfile(payPointProfile);
        paypoint.setPysicalPayPointAddress(payPointPhysical);
        paypoint.setContactPayPointAddress(payPointContact);
        paypoint.setPayPointPaymentDetail(payPointPaymentDetail);
        paypoint.setPayPointStatus(isNotEmpty(this.payPointStatus)? PayPointStatus.valueOf(this.payPointStatus) : null);
        paypoint.setPayPointGrade(isNotEmpty(this.payPointGrade)? PayPointGrade.valueOf(this.payPointGrade): null);
        paypoint.setPromptDatePremium(this.promptDatePremium);
        paypoint.setPromptDateSchedules(this.promptDateSchedules);
        return paypoint;
    }
}
