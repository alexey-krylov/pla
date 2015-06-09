package com.pla.publishedlanguage.underwriter.contract;

import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 5/27/2015.
 */
public interface IUnderWriterAdapter {

    public String getRoutingLevel(UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto);

    public List<Map<String,Object>> getDocumentsForUnderWriterApproval(UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto);

    public List<String> getUnderWriterDocument(UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto);

    public List<Map<String,Object>> getDocumentsForApproverApproval();


}
