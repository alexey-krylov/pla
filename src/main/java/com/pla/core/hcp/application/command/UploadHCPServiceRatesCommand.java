package com.pla.core.hcp.application.command;

import com.pla.core.hcp.domain.model.HCPRateId;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Set;

/**
 * Created by Mohan Sharma on 12/21/2015.
 */
@AllArgsConstructor
@Getter
public class UploadHCPServiceRatesCommand {
    private HCPRateId hcpRateId;
    private Set<HCPServiceDetailDto> hcpServiceDetailDtos;
    private String hcpCode;
    private String hcpName;
    private LocalDate fromDate;
    private LocalDate toDate;
}
