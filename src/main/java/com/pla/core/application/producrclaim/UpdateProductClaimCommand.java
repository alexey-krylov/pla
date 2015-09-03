package com.pla.core.application.producrclaim;

import com.pla.core.dto.CoverageClaimTypeDto;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Created by Admin on 9/3/2015.
 */
@Getter
@Setter
public class UpdateProductClaimCommand {
    private Long productClaimId;
    private String planCode;
    private LineOfBusinessEnum lineOfBusiness;
    private List<CoverageClaimTypeDto> coverageClaimType;
    private UserDetails userDetails;
}
