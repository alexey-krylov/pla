package com.pla.individuallife.presentation.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by ASUS on 26-May-15.
 */

@Getter
@Setter
@NoArgsConstructor
public class ResidentialAddressDto {

    @NotNull(message = "{Assured Address1 cannot be null}")
    @NotEmpty(message = "{Assured Address1 cannot be empty}")
    private String address1;

    @NotNull(message = "{Assured province cannot be null}")
    @NotEmpty(message = "{Assured province cannot be empty}")
    private String province;

    @NotNull(message = "{Assured town cannot be null}")
    @NotEmpty(message = "{Assured town cannot be empty}")
    private String town;
}
