package com.pla.core.application;

import com.pla.core.domain.model.BenefitId;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 3/23/15
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class UpdateCoverageCommand {

    @NotNull(message = "{coverageId id cannot be null}")
    @NotEmpty(message = "{coverageId id cannot be empty}")
    private String coverageId;

    @NotNull(message = "{status cannot be null}")
    @Length(max = 100, min = 1,message = "{Coverage name length should be between 1-100}")
    private String coverageName;

    private String coverageCode;
    private String description;

    private Set<BenefitId> benefitIds;

    private UserDetails userDetails;
}

