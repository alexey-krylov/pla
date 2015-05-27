package com.pla.sharedkernel.service;

import lombok.Getter;

import java.io.File;

/**
 * Created by Samir on 5/27/2015.
 */
@Getter
public class EmailAttachment {

    private String fileName;

    private File file;

    private String contentType;

    public EmailAttachment(String fileName, String contentType, File file) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.file = file;
    }
}
