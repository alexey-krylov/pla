package com.pla.individuallife.quotation.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by Karunakar on 5/13/2015.
 */
@EqualsAndHashCode(of = "userName")
@Getter
public class ILQuotationProcessor {

    private String userName;

    public ILQuotationProcessor(String userName) {
        this.userName = userName;
    }

}
