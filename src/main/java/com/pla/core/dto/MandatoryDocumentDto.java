package com.pla.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 3/31/2015.
 */
@Getter
@Setter
public class MandatoryDocumentDto {
    private Long documentId;
    private String coverageId;
    private String planId;
    private String process;
    List<Map<String,Object>> documents;
}