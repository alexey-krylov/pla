package com.pla.core.hcp.application.command;

import com.pla.core.hcp.domain.model.HCP;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;

/**
 * Author - Mohan Sharma Created on 12/17/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateOrUpdateHCPCommand {
    private String hcpCode;
    private LocalDate activatedOn;
    @NotNull(message = "{hcpStatus cannot be null}")
    @NotEmpty(message = "{hcpStatus cannot be null}")
    private String hcpStatus;
    @NotNull(message = "{hcpName cannot be null}")
    @NotEmpty(message = "{hcpName cannot be null}")
    private String hcpName;
    @NotNull(message = "{hcpCategory cannot be null}")
    @NotEmpty(message = "{hcpCategory cannot be null}")
    private String hcpCategory;
    @NotNull(message = "{addressLine1 cannot be null}")
    @NotEmpty(message = "{addressLine1 cannot be null}")
    private String addressLine1;
    private String addressLine2;
    private String postalCode;
    @NotNull(message = "{province cannot be null}")
    @NotEmpty(message = "{province cannot be null}")
    private String province;
    @NotNull(message = "{town cannot be null}")
    @NotEmpty(message = "{town cannot be null}")
    private String town;
    @NotNull(message = "{emailId cannot be null}")
    @NotEmpty(message = "{emailId cannot be null}")
    private String emailId;
    @NotNull(message = "{workPhoneNumber cannot be null}")
    @NotEmpty(message = "{workPhoneNumber cannot be null}")
    private String workPhoneNumber;
    @NotNull(message = "{contactPersonDetailcannot be null}")
    @NotEmpty(message = "{contactPersonDetail cannot be null}")
    private String contactPersonDetail;
    @NotNull(message = "{contactPersonMobile cannot be null}")
    @NotEmpty(message = "{contactPersonMobile cannot be null}")
    private String contactPersonMobile;
    @NotNull(message = "{contactPersonWorkPhoneNumber cannot be null}")
    @NotEmpty(message = "{contactPersonWorkPhoneNumber cannot be null}")
    private String contactPersonWorkPhoneNumber;
    @NotNull(message = "{contactPersonEmailId cannot be null}")
    @NotEmpty(message = "{contactPersonEmailId cannot be null}")
    private String contactPersonEmailId;

    public static CreateOrUpdateHCPCommand setPropertiesFromHCPEntity(HCP hcp) {
        CreateOrUpdateHCPCommand createOrUpdateHCPCommand = new CreateOrUpdateHCPCommand();
        createOrUpdateHCPCommand.hcpCode = hcp.getHcpCode().getHcpCode();
        createOrUpdateHCPCommand.activatedOn = hcp.getActivatedOn();
        createOrUpdateHCPCommand.hcpName = hcp.getHcpName();
        createOrUpdateHCPCommand.hcpStatus = hcp.getHcpStatus().name();
        createOrUpdateHCPCommand.hcpCategory = hcp.getHcpCategory().description;
        createOrUpdateHCPCommand.addressLine1 = hcp.getHcpAddress() != null ? hcp.getHcpAddress().getAddressLine1() : null;
        createOrUpdateHCPCommand.addressLine2 = hcp.getHcpAddress() != null ? hcp.getHcpAddress().getAddressLine2() : null;
        createOrUpdateHCPCommand.postalCode = hcp.getHcpAddress() != null ? hcp.getHcpAddress().getPostalCode() : null;
        createOrUpdateHCPCommand.province = hcp.getHcpAddress() != null ? hcp.getHcpAddress().getProvince() : null;
        createOrUpdateHCPCommand.town = hcp.getHcpAddress() != null ? hcp.getHcpAddress().getTown() : null;
        createOrUpdateHCPCommand.emailId = hcp.getHcpContactDetail() != null ? hcp.getHcpContactDetail().getEmailId() : null;
        createOrUpdateHCPCommand.workPhoneNumber = hcp.getHcpContactDetail() != null ? hcp.getHcpContactDetail().getWorkPhoneNumber() : null;
        createOrUpdateHCPCommand.contactPersonDetail = hcp.getHcpContactPersonDetail() != null ? hcp.getHcpContactPersonDetail().getContactPersonDetail() : null;
        createOrUpdateHCPCommand.contactPersonMobile = hcp.getHcpContactPersonDetail() != null ? hcp.getHcpContactPersonDetail().getContactPersonMobile() : null;
        createOrUpdateHCPCommand.contactPersonWorkPhoneNumber = hcp.getHcpContactPersonDetail() != null ? hcp.getHcpContactPersonDetail().getContactPersonWorkPhoneNumber() : null;
        createOrUpdateHCPCommand.contactPersonEmailId = hcp.getHcpContactPersonDetail() != null ? hcp.getHcpContactPersonDetail().getContactPersonEmailId() : null;
        return createOrUpdateHCPCommand;
    }
}
