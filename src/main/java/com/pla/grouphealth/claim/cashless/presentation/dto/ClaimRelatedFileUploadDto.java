package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * Author - Mohan Sharma Created on 12/31/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRelatedFileUploadDto {
    @NotNull(message = "hcpCode must not be null")
    @NotEmpty(message = "hcpCode must not be null")
    private String hcpCode;
    @NotNull(message = "file must not be null")
    private MultipartFile file;
    @NotNull(message = "fromDate must not be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private DateTime batchDate;
}
