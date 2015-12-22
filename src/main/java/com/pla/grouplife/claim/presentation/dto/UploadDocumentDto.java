package com.pla.grouplife.claim.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by ak
 */
@Getter
@Setter
@NoArgsConstructor
public class UploadDocumentDto {

    //private String documentId;

    //private String documentName;

    private String filename;

    private MultipartFile file;

    //private MultipartFile file;

   // private boolean mandatory;

    //private boolean additional;

    //private boolean isRequireForSubmission;

  /*  public UploadDocumentDto(String documentId, String documentName) {
        this.documentId = documentId;
        this.documentName = documentName;
    }  */

}
