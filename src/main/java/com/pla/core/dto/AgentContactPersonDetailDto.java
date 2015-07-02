package com.pla.core.dto;

import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Samir on 7/1/2015.
 */
@Getter
@Setter
public class AgentContactPersonDetailDto {

    private LineOfBusinessEnum lineOfBusinessId;

    private String title;

    private String fullName;

    private String emailId;

    private String workPhone;

    private String fax;
}
