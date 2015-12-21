package com.pla.core.hcp.presentation.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Mohan Sharma on 4/14/2015.
 */
@Getter
@Setter
public class UploadHCPServiceRatesDto {

    private String hcpRateId;
    private String hcpName;
    private String hcpCode;
    private MultipartFile file;
    private LocalDate fromDate;
    private LocalDate toDate;

}
