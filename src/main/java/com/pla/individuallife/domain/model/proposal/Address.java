package com.pla.individuallife.domain.model.proposal;

import javax.persistence.Embeddable;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Embeddable
public class Address {
    private String address1;
    private String address2;
    private int postalCode;
    private String province;
    private String town;
}
