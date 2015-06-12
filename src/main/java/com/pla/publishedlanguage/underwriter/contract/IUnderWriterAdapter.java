package com.pla.publishedlanguage.underwriter.contract;

import com.pla.publishedlanguage.dto.ClientDocumentDto;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.sharedkernel.domain.model.RoutingLevel;

import java.util.List;

/**
 * Created by Admin on 5/27/2015.
 */
public interface IUnderWriterAdapter {

    public RoutingLevel getRoutingLevel(UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto);

    public List<ClientDocumentDto> getDocumentsForUnderWriterApproval(UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto);

    public List<ClientDocumentDto> getDocumentsForApproverApproval();


}
