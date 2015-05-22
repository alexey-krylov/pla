package com.pla.underwriter.application;

import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import com.pla.underwriter.domain.model.UnderWriterProcessType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 5/13/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateUnderWriterRoutingLevelCommand {

    @NotNull
    private String planCode;

    @NotNull
    private String planName;

    private String coverageId;

    private UnderWriterProcessType processType;

    private List<Map<Object,Map<String,Object>>> underWriterDocumentItem;

    private List<UnderWriterInfluencingFactor> underWriterInfluencingFactors;

    @NotNull
    private LocalDate effectiveFrom;

    private UserDetails userDetails;

    @NotNull
    private MultipartFile file;
}
