package com.pla.underwriter.application;

import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import com.pla.underwriter.domain.model.UnderWriterProcessType;
import com.pla.underwriter.dto.UnderWriterDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Admin on 5/13/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateUnderWriterDocumentCommand {

    @NotNull
    private String planCode;

    private String coverageId;

    private UnderWriterProcessType processType;

    private List<UnderWriterDto> underWriterDocumentItems;

    private List<UnderWriterInfluencingFactor> underWriterInfluencingFactors;

    @NotNull
    private LocalDate effectiveFrom;

    private UserDetails userDetails;

}
