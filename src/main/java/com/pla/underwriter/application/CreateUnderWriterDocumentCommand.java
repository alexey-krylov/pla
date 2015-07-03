package com.pla.underwriter.application;

import com.pla.sharedkernel.identifier.UnderWriterDocumentId;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import com.pla.underwriter.domain.model.UnderWriterProcessType;
import com.pla.underwriter.dto.UnderWriterDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Admin on 5/13/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateUnderWriterDocumentCommand {

    private UnderWriterDocumentId underWriterDocumentId;

    @NotNull
    private String planCode;

    private String coverageId;

    private UnderWriterProcessType processType;

    private List<UnderWriterDto> underWriterDocumentItems;

    private List<UnderWriterInfluencingFactor> underWriterInfluencingFactors;

    @NotNull
    private DateTime effectiveFrom;

    private UserDetails userDetails;

    public List<UnderWriterDto> transformTheUnderWriterDocumentLineItem(){
        return underWriterDocumentItems.stream().map(new UnderWriterDocumentTransformer()).collect(Collectors.toList());
    }

    private class UnderWriterDocumentTransformer implements Function<UnderWriterDto, UnderWriterDto> {
        @Override
        public UnderWriterDto apply(UnderWriterDto underWriterDto) {
            underWriterDto.transformUnderWriterDocument(underWriterDto.getUnderWriterDocuments());
            underWriterDto.setUnderWriterLineItem(underWriterDto.getUnderWriterDocumentLineItem(),underWriterInfluencingFactors);
            return underWriterDto;
        }
    }
}
