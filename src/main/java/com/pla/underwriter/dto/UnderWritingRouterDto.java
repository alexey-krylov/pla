package com.pla.underwriter.dto;

import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Admin on 5/8/2015.
 */
@Getter
@Setter
public class UnderWritingRouterDto {
    private String planCode;
    private String planName;
    private String coverageId;
    private String process;
    @NotNull
    private List<UnderWriterInfluencingFactor> underWriterInfluencingFactors;

}
