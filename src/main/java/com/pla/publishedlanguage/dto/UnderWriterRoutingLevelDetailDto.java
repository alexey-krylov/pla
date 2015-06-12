package com.pla.publishedlanguage.dto;

import com.pla.sharedkernel.identifier.CoverageId;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 5/26/2015.
 */
@Getter
@Setter
public class UnderWriterRoutingLevelDetailDto {

    private String planCode;

    private String clientId;

    private String process;

    private CoverageId coverageId;

    private List<UnderWriterInfluencingFactorItem> underWriterInfluencingFactor;

    private LocalDate effectiveFrom;


    public UnderWriterRoutingLevelDetailDto(String planCode, LocalDate effectiveFrom,String process) {
        checkArgument(planCode != null);
        checkArgument(process != null);
        checkArgument(effectiveFrom != null);
        this.planCode  = planCode;
        this.process = process;
        this.effectiveFrom = effectiveFrom;
    }


    public UnderWriterRoutingLevelDetailDto addCoverage(CoverageId coverageId) {
        this.coverageId = coverageId;
        return this;
    }

    @Getter
    public static class UnderWriterInfluencingFactorItem {

        private String underWriterInfluencingFactor;

        private String value;

        public UnderWriterInfluencingFactorItem(String underWriterInfluencingFactor, String value) {
            checkArgument(underWriterInfluencingFactor != null);
            checkArgument(isNotEmpty(value));
            this.underWriterInfluencingFactor = underWriterInfluencingFactor;
            this.value = value;
        }
    }

}
