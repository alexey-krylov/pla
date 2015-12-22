package com.pla.core.hcp.domain.model;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Set;

/**
 * Created by Mohan Sharma on 12/21/2015.
 */
@Document(collection = "hcp_rate")
@NoArgsConstructor
@Getter
public class HCPRate {
    @Id
    private HCPRateId hcpRateId;
    private String hcpName;
    private HCPCode hcpCode;
    private DateTime fromDate;
    private DateTime toDate;
    private Set<HCPServiceDetail> hcpServiceDetails = Sets.newHashSet();

    public HCPRate updateWithHCPRateId(HCPRateId hcpRateId){
        this.hcpRateId = hcpRateId;
        return this;
    }

    public HCPRate updateWithHCPName(String hcpName){
        this.hcpName = hcpName;
        return this;
    }

    public HCPRate updateWithHCPCode(HCPCode hcpCode){
        this.hcpCode = hcpCode;
        return this;
    }

    public HCPRate updateWithFromDate(DateTime fromDate){
        this.fromDate = fromDate;
        return this;
    }

    public HCPRate updateWithToDate(DateTime toDate){
        this.toDate = toDate;
        return this;
    }

    public HCPRate updateWithHcpServiceDetails(Set<HCPServiceDetail> hcpServiceDetails){
        this.hcpServiceDetails = hcpServiceDetails;
        return this;
    }
}
