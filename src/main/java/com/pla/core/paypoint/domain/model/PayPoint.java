package com.pla.core.paypoint.domain.model;

import com.pla.core.paypoint.application.command.PayPointCommand;
import com.pla.core.paypoint.presentation.dto.PayPointProfileDto;
import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;

/**
 * Created by Rudra on 12/11/2015.
 */
@Entity
@Table(name= "pay_point")
@ToString
@Setter
@NoArgsConstructor
@Getter(value = AccessLevel.PACKAGE)
public class PayPoint implements ICrudEntity {

    @EmbeddedId
    private PayPointId payPointId;

    @Enumerated(EnumType.STRING)
    private PayPointStatus payPointStatus;

    @Embedded
    private PayPointProfile payPointProfile;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate promptDatePremium;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate promptDateSchedules;

    @Embedded
      @AttributeOverrides({
            @AttributeOverride(name = "addressLine1", column = @Column(name = "pysicalPayPoint_AddressLine1")),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "pysicalPayPoint_AddressLine2")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "pysicalPayPoint_PostalCode")),
            @AttributeOverride(name = "province", column = @Column(name = "pysicalPayPoint_Province")),
            @AttributeOverride(name = "town", column = @Column(name = "pysicalPayPoint_Town")),
               })
    private PayPointAddress pysicalPayPointAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "addressLine1", column = @Column(name = "contactPayPoint_AddressLine1")),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "contactPayPoint_AddressLine2")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "contactPayPoint_PostalCode")),
            @AttributeOverride(name = "province", column = @Column(name = "contactPayPoint_Province")),
            @AttributeOverride(name = "town", column = @Column(name = "contactPayPoint_Town")),
    })
    private PayPointAddress contactPayPointAddress;

    @Enumerated(EnumType.STRING)
    private PayPointGrade payPointGrade;

    @Embedded
    private PayPointPaymentDetail payPointPaymentDetail;



    public PayPointCommand setPropertiesToPayPointCommandFromPayPoint(PayPointCommand payPointCommand) {
        payPointCommand.setPayPointId(this.payPointId != null ? this.payPointId.getPayPointId() : null);
        payPointCommand.setPromptDatePremium(this.promptDatePremium);
        payPointCommand.setPromptDateSchedules(this.promptDateSchedules);
        payPointCommand.setPayPointStatus(this.payPointStatus != null ? this.payPointStatus.name() : null);
        payPointCommand.setPayPointGrade(this.payPointGrade != null ? this.payPointGrade.name() : null);
        payPointCommand = new PayPointProfileDto().createAndSetPropertiesToPaypointProfileDto(this.payPointProfile, payPointCommand);
        payPointCommand = new PayPointAddress().createAndSetPropertiesToPayPointContactAddress(this.contactPayPointAddress, payPointCommand);
        payPointCommand = new PayPointAddress().createAndSetPropertiesToPayPointPhysicalAddress(this.pysicalPayPointAddress, payPointCommand);
        payPointCommand = new PayPointPaymentDetail().createAndSetPropertiesToPayPointPaymentDto(this.payPointPaymentDetail, payPointCommand);
        return payPointCommand;
    }

    public PayPointId getPayPointId() {
        return payPointId;
    }
}