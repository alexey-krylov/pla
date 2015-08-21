package com.pla.grouplife.sharedresource.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

/**
 * Created by Samir on 7/9/2015.
 */
@Getter
@Setter
public class GLPolicyDetailDto {

    private String policyId;

    private String policyHolderName;

    private DateTime inceptionDate;

    private DateTime expiryDate;

    private String policyNumber;

    private String status;

    private List<Map<String, String>> endorsementTypes;
}
