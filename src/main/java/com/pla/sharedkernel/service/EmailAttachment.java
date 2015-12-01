package com.pla.sharedkernel.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.List;

/**
 * Created by Samir on 5/27/2015.
 */
@Getter
@NoArgsConstructor
public class EmailAttachment {

    private String fileName;

    private File file;

    private String contentType;

    @Setter
    private List<EmailAttachment> subAttachments;

    public EmailAttachment(String fileName, String contentType, File file) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.file = file;
    }

    public EmailAttachment withAttachment(String fileName,String contentType ,File file){
        this.fileName  = fileName;
        this.contentType  = contentType;
        this.file = file;
        return this;
    }
}
