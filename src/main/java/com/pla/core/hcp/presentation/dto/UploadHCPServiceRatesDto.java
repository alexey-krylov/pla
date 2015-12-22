package com.pla.core.hcp.presentation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
import org.nthdimenzion.presentation.LocalJodaDateSerializer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Created by Mohan Sharma on 4/14/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadHCPServiceRatesDto {

    private String hcpRateId;
    @NotNull(message = "hcpName must not be null")
    private String hcpName;
    @NotNull(message = "hcpCode must not be null")
    private String hcpCode;
    @NotNull(message = "file must not be null")
    private MultipartFile file;
    @NotNull(message = "fromDate must not be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;
    @NotNull(message = "toDate must not be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

}
