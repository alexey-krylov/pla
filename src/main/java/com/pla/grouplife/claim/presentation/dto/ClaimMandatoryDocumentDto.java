package com.pla.grouplife.claim.presentation.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@EqualsAndHashCode
/**
 * Created by ak
 */
public class ClaimMandatoryDocumentDto {

    private Long documentId;
    private String documentCode;
    private String documentName;
    private String coverageId;
    private String coverageName;
    private String planId;
    private String planName;
    private String process;


}
