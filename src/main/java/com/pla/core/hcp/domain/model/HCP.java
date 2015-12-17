package com.pla.core.hcp.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;

/**
 * Created by Mohan Sharma on 12/17/2015.
 */
@Entity
@NoArgsConstructor
@Getter
public class HCP implements ICrudEntity {
    @EmbeddedId
    private HCPCode hcpCode;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate activatedOn;
    @Enumerated(EnumType.STRING)
    private HCPStatus hcpStatus;
    private String hcpName;
    private HCPCategory hcpCategory;
    @Embedded
    private HCPAddress hcpAddress;
    @Embedded
    private HCPContactDetail hcpContactDetail;
    @Embedded
    private HCPContactPersonDetail hcpContactPersonDetail;

    public HCP updateWithHCPCode(HCPCode hcpCode){
        this.hcpCode = hcpCode;
        return this;
    }

    public HCP updateWithActivatedOn(LocalDate activatedOn){
        this.activatedOn = activatedOn;
        return this;
    }

    public HCP updateWithHcpStatus(String hcpStatus){
        this.hcpStatus = HCPStatus.valueOf(hcpStatus);
        return this;
    }

    public HCP updateWithHCPName(String hcpName){
        this.hcpName = hcpName;
        return this;
    }
    public HCP updateWithHcpCategory(String hcpCategory){
        this.hcpCategory = HCPCategory.getHCPCategory(hcpCategory);
        return this;
    }

    public HCP updateWithHcpAddress(String addressLine1, String addressLine2, String postalCode, String province, String town){
        this.hcpAddress = new HCPAddress(addressLine1, addressLine2, postalCode, province, town);
        return this;
    }

    public HCP updateWithHcpContactDetail(String emailId, String workPhoneNumber){
        this.hcpContactDetail = new HCPContactDetail(emailId, workPhoneNumber);
        return this;
    }

    public HCP updateWithHcpContactPersonDetail( String contactPersonDetail, String contactPersonMobile, String contactPersonWorkPhoneNumber, String contactPersonEmailId){
        this.hcpContactPersonDetail = new HCPContactPersonDetail(contactPersonDetail, contactPersonMobile, contactPersonWorkPhoneNumber, contactPersonEmailId);
        return this;
    }

}
