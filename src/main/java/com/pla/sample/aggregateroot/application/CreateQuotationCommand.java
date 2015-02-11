package com.pla.sample.aggregateroot.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: Samir
 * @since 1.0 10/02/2015
 */
@Getter
@Setter
@AllArgsConstructor
public class CreateQuotationCommand {

    private String clientName;

    private String contactNumber;

    private String quotationName;


}
