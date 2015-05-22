package com.pla.underwriter.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * Created by Admin on 5/8/2015.
 */
@Getter
@Setter
public class UnderWriterDto {

    private List<UnderWriterLineItemDto> underWriterLineItem;
    private String routingLevel;
    private Set<String> documents;

}
