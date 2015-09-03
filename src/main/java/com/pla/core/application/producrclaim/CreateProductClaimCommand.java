package com.pla.core.application.producrclaim;

import com.pla.core.dto.CoverageClaimTypeDto;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Created by Admin on 9/2/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateProductClaimCommand {
    private String planCode;
    private LineOfBusinessEnum lineOfBusiness;
    private List<CoverageClaimTypeDto> coverageClaimType;
    private UserDetails userDetails;

}
