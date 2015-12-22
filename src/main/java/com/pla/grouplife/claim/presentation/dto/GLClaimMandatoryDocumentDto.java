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
public class GLClaimMandatoryDocumentDto {

        private String documentId;

        private String documentName;

        private MultipartFile file;

        private byte[] content;

        private boolean submitted;

        private String fileName;

        private String contentType;

        private String gridFsDocId;

        private boolean isRequireForSubmission;

        public GLClaimMandatoryDocumentDto(String documentId, String documentName) {
            this.documentId = documentId;
            this.documentName = documentName;
        }

       public GLClaimMandatoryDocumentDto updateWithContent(byte[] content) {
            this.content = content;
            this.submitted = true;
            return this;
        }

    }


