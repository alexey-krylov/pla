package com.pla.quotation.presentation.command.grouplife;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
public class UploadInsuredDetailDto {

    private boolean samePlanForAllRelation;

    private boolean samePlanForAllCategory;

    private MultipartFile file;

    private QuotationId quotationId;
}
