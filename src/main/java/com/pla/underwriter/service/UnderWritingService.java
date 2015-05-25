package com.pla.underwriter.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import com.pla.underwriter.domain.model.UnderWriterProcessType;
import com.pla.underwriter.dto.UnderWriterDto;
import com.pla.underwriter.dto.UnderWriterLineItemDto;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.pla.underwriter.exception.UnderWriterTemplateParseException.raiseNotValidPlanCodeException;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 5/11/2015.
 */
@Service
public class UnderWritingService {

    private UnderWriterTemplateParser underWriterTemplateParser;
    private IPlanAdapter iPlanAdapter;

    @Autowired
    public UnderWritingService(UnderWriterTemplateParser underWriterTemplateParser,IPlanAdapter iPlanAdapter) {
        this.underWriterTemplateParser = underWriterTemplateParser;
        this.iPlanAdapter  = iPlanAdapter;
    }

    public HSSFWorkbook generateUnderWriterExcelTemplate(List<UnderWriterInfluencingFactor> premiumInfluencingFactors, String planName) throws IOException {
        return underWriterTemplateParser.generateUnderWriterTemplate(premiumInfluencingFactors, planName);
    }

    public List<Map<Object,Map<String,Object>>> parseUnderWriterExcelTemplate(HSSFWorkbook hssfWorkbook,List<UnderWriterInfluencingFactor> underWriterInfluencingFactors){
        List<Map<String,Object>> underWriterRoutingLevelItemFromExcel = underWriterTemplateParser.parseUnderWriterRoutingLevelTemplate(hssfWorkbook,underWriterInfluencingFactors);
        List<Map<Object,Map<String,Object>>>  underWriterRoutingLevel = underWriterTemplateParser.groupUnderWriterLineItemByInfluencingFactor(underWriterRoutingLevelItemFromExcel, underWriterInfluencingFactors);
        return underWriterRoutingLevel;
    }

    public boolean isValidUnderWriterRoutingLevelTemplate(HSSFWorkbook hssfWorkbook, String planCode, String coverageId, List<UnderWriterInfluencingFactor> underWriterInfluencingFactors) throws IOException {
        checkValidPlanAndCoverageCode(planCode);
        return underWriterTemplateParser.validateUnderWritingRoutingLevelDataForAGivenPlanAndCoverage(hssfWorkbook,planCode,coverageId,underWriterInfluencingFactors,iPlanAdapter);
    }

    public List<PlanCoverageDetailDto> getPlanCoverageDetail(){
        return iPlanAdapter.getAllPlanAndCoverageDetail();
    }

    public List<PlanCoverageDetailDto> getAllOptionalCoverageFor(List<PlanId> planIds){
        return iPlanAdapter.getPlanAndCoverageDetail(planIds);
    }

    public boolean validateTheUnderWriterDocument(String planCode, String coverageId, List<UnderWriterDto> listOfUnderWriterDto, List<String> errorMessageBuilder){
        boolean isRowOverLapping = isValidInfluencingFactorForTheProduct(listOfUnderWriterDto,planCode,coverageId,errorMessageBuilder);
        List<UnderWriterDto> comparedBy = Lists.newArrayList(listOfUnderWriterDto);
        UnderWriterDto currentUnderWriterDto = listOfUnderWriterDto.get(listOfUnderWriterDto.size()-1);
        return  doesAnyRowOverLappingEachOther(currentUnderWriterDto, comparedBy, isRowOverLapping, errorMessageBuilder);
    }

    public boolean validateTheUnderWriterDocumentData(String planCode, String coverageId, List<UnderWriterDto> listOfUnderWriterDto, List<String> errorMessageBuilder) {
        boolean isRowOverLapping = isValidInfluencingFactorForTheProduct(listOfUnderWriterDto, planCode, coverageId, errorMessageBuilder);
        List<UnderWriterDto> comparedBy = Lists.newArrayList(listOfUnderWriterDto);
        for (UnderWriterDto currentUnderWriterDto : listOfUnderWriterDto){
            isRowOverLapping = doesAnyRowOverLappingEachOther(currentUnderWriterDto, comparedBy, isRowOverLapping, errorMessageBuilder);
        }
        return isRowOverLapping;
    }

    boolean doesAnyRowOverLappingEachOther(UnderWriterDto currentUnderWriterDto, List<UnderWriterDto> comparedBy, boolean isRowOverLapping, List<String> errorMessageBuilder){
        int rowIndex = 1;
        comparedBy.remove(currentUnderWriterDto);
        List<UnderWriterLineItemDto> currentLineItem = currentUnderWriterDto.getUnderWriterLineItem();
        for (UnderWriterDto underWriterDtoToBeCompared : comparedBy){
            List<UnderWriterLineItemDto> lineItemToBeCompared = underWriterDtoToBeCompared.getUnderWriterLineItem();
            boolean isAnyOneFactorNotOverlapping = true;
            for (int index=0; index<=currentLineItem.size()-1; index++){
                if(currentLineItem.get(index).getUnderWriterInfluencingFactor().getDescription().equals(lineItemToBeCompared.get(index).getUnderWriterInfluencingFactor().getDescription())){
                    Double currentFromValue = getInfluencingFactorValue(currentLineItem.get(index).getInfluencingItemFrom());
                    Double currentToValue = getInfluencingFactorValue(currentLineItem.get(index).getInfluencingItemTo());
                    Double fromValue = getInfluencingFactorValue(lineItemToBeCompared.get(index).getInfluencingItemFrom());
                    Double toValue = getInfluencingFactorValue(lineItemToBeCompared.get(index).getInfluencingItemTo());
                    if (!(currentFromValue.compareTo(toValue) <= 0 && fromValue.compareTo(currentToValue) <= 0)) {
                        isAnyOneFactorNotOverlapping = false;
                    }
                }
            }
            if (isAnyOneFactorNotOverlapping){
                isRowOverLapping = false;
                errorMessageBuilder.add(" Overlapping with row "+rowIndex+", ");
            }
            rowIndex++;
        }
        return isRowOverLapping;
    }


    boolean isValidInfluencingFactorForTheProduct(List<UnderWriterDto> underWriterDocument,String planCode,String coverageId,List<String> errorMessageBuilder){
        boolean isValidInfluencingFactorValue=true;
        int rowIndex  = 1;
        for (UnderWriterDto underWriterDto : underWriterDocument){
            StringBuilder errorMessage = new StringBuilder();
            for (UnderWriterLineItemDto underWriterLineItemDto :underWriterDto.getUnderWriterLineItem()){
                boolean isValid =  underWriterLineItemDto.getUnderWriterInfluencingFactor().isValueAvailableForTheProduct(null,planCode,coverageId,null,errorMessage,iPlanAdapter,underWriterLineItemDto.getInfluencingItemFrom(),underWriterLineItemDto.getInfluencingItemTo());
                if (!isValid)
                    isValidInfluencingFactorValue = false;
            }
            if (isNotEmpty(errorMessage.toString()))
                errorMessageBuilder.add("Error in Row "+rowIndex+" : "+errorMessage.toString());
            rowIndex++;
        }
        return isValidInfluencingFactorValue;

    }

    public List<Map<Object,Map<String,Object>>> transformUnderWriterDocument(List<UnderWriterDto> underWriterDocumentItems){
        List<Map<Object,Map<String,Object>>> underWriterDocumentItem = Lists.newArrayList();
        for (UnderWriterDto underWriterDto : underWriterDocumentItems){
            Map<Object ,Map<String,Object>> underWriterDocumentLineItem = Maps.newLinkedHashMap();
            for (UnderWriterLineItemDto underWriterLineItemDto : underWriterDto.getUnderWriterLineItem()){
                Map<String,Object> influencingFactorValueMap = underWriterLineItemDto.getUnderWriterInfluencingFactor().transformUnderWriterDocumentLineItem(underWriterLineItemDto.getInfluencingItemFrom(), underWriterLineItemDto.getInfluencingItemTo());
                underWriterDocumentLineItem.put(underWriterLineItemDto.getUnderWriterInfluencingFactor(),influencingFactorValueMap);
            }
            Map<String,Object> documentsMap = Maps.newLinkedHashMap();
            documentsMap.put(AppConstants.UNDER_WRITER_DOCUMENT, underWriterDto.getDocuments());
            underWriterDocumentLineItem.put(AppConstants.UNDER_WRITER_DOCUMENT,documentsMap);
            underWriterDocumentItem.add(underWriterDocumentLineItem);
        }
        return underWriterDocumentItem;
    }

    Double getInfluencingFactorValue(String value){
        return NumberUtils.isNumber(value)==true?Double.valueOf(value):0L;
    }

    public void checkValidPlanAndCoverageCode(String planCode){
        if (!iPlanAdapter.isValidPlanCode(planCode))
            raiseNotValidPlanCodeException();
    }

    public List<Map<String,String>> getUnderWriterProcess(){
        List<Map<String,String>> underWriterProcess = Lists.newArrayList();
        Arrays.asList(UnderWriterProcessType.values()).forEach(process-> {
                    Map<String, String> processMap = Maps.newLinkedHashMap();
                    processMap.put("processType",process.name());
                    processMap.put("description",process.getDescription());
                    underWriterProcess.add(processMap);
                }
        );
        return underWriterProcess;
    }

    public List<Map<String,String>> getUnderWritingInfluencingFactor(String processType){
        List<Map<String,String>> influencingFactorList = Lists.newArrayList();
        Arrays.asList(UnderWriterInfluencingFactor.values()).forEach(influencingFactor->{
            Map<String,String> influencingFactorMap = Maps.newLinkedHashMap();
            if (UnderWriterProcessType.CLAIM.equals(UnderWriterProcessType.valueOf(processType))) {
                influencingFactorMap.put("influencingFactor", influencingFactor.name());
                influencingFactorMap.put("description", influencingFactor.getDescription());
                influencingFactorList.add(influencingFactorMap);
            }
            else if (!UnderWriterInfluencingFactor.CLAIM_AMOUNT.equals(influencingFactor)){
                influencingFactorMap.put("influencingFactor", influencingFactor.name());
                influencingFactorMap.put("description", influencingFactor.getDescription());
                influencingFactorList.add(influencingFactorMap);
            }
        });
        return influencingFactorList;
    }
}
