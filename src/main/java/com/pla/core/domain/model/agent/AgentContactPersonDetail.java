package com.pla.core.domain.model.agent;

import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by Samir on 7/1/2015.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AgentContactPersonDetail {

    @Enumerated(EnumType.STRING)
    private LineOfBusinessEnum lineOfBusiness;

    private String salutation;

    private String personName;

    private String emailId;

    private String workPhoneNumber;

    private String faxNumber;


    public AgentContactPersonDetail(LineOfBusinessEnum lineOfBusinessEnum, String salutation, String personName, String emailId, String workPhoneNumber, String faxNumber) {
        this.lineOfBusiness = lineOfBusinessEnum;
        this.salutation = salutation;
        this.personName = personName;
        this.emailId = emailId;
        this.workPhoneNumber = workPhoneNumber;
        this.faxNumber = faxNumber;
    }

}
