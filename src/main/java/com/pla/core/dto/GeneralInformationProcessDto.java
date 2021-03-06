package com.pla.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 4/20/2015.
 */
@Getter
@Setter
public class GeneralInformationProcessDto {

    private String type;

    private String description;

    private String fullDescription;

    public GeneralInformationProcessDto(String type, String description,String fullDescription) {
        this.type  = type;
        this.description = description;
        this.fullDescription = fullDescription;

    }
}
