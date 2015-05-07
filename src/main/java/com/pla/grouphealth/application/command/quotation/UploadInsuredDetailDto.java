package com.pla.grouphealth.application.command.quotation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Getter
@Setter
public class UploadInsuredDetailDto {

    private boolean samePlanForAllRelation;

    private boolean samePlanForAllCategory;

    private MultipartFile file;

    private String quotationId;
}
