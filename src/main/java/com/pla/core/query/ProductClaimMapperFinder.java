package com.pla.core.query;

import com.pla.core.dto.CoverageClaimTypeDto;
import com.pla.core.dto.ProductClaimTypeDto;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Admin on 9/2/2015.
 */
@Finder
@Service
public class ProductClaimMapperFinder {

    public static final String getPlanDetailsByLineOfBusiness = " SELECT DISTINCT p.plan_code planCode,p.plan_id planId, p.plan_name planName FROM " +
            " plan_coverage_benefit_assoc p " +
            " WHERE p.line_of_business=  :lineOfBusiness  ";
    public static final String getAllProductClaimMappingDetails = " SELECT * FROM product_claim_map_view ";
    public static final String getProductClaimMapDetailByProductClaimId = " SELECT * FROM product_claim_map_view WHERE productClaimId=:productClaimId ";
    public static final String getAllCoveragesDetailByPlanCode = "SELECT DISTINCT p.coverageName, p.coverageId " +
            "from  product_claim_map_view p where planCode=:planCode";
    public static final String getAllClaimTypesDetailByCoverageId = "SELECT p.claimType  from product_claim_map_view p where coverageId=:coverageId";
    public static final String getAllProductClaimMappingDetailsByLineOfBusinessAndPlanName = "SELECT * FROM product_claim_map_view WHERE lineOfBusiness=:lineOfBusiness OR " +
            " planName=:planName ";
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public List<Map<String, Object>> getPlanDetailBy(LineOfBusinessEnum lineOfBusinessEnum) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("lineOfBusiness", lineOfBusinessEnum.toString());
        return namedParameterJdbcTemplate.query(getPlanDetailsByLineOfBusiness, sqlParameterSource, new ColumnMapRowMapper());
    }


    public List<ProductClaimTypeDto> getAllProductClaimMapDetail() {
        List<Map<String, Object>> productClaimDetail = namedParameterJdbcTemplate.query(getAllProductClaimMappingDetails, new ColumnMapRowMapper());
        return mapAllProductClaimDetail(productClaimDetail);


    }


    public ProductClaimTypeDto getProductClaimMapDetailById(String productClaimId) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("productClaimId", productClaimId);
        List<Map<String, Object>> productClaimDetail = namedParameterJdbcTemplate.query(getProductClaimMapDetailByProductClaimId, sqlParameterSource, new ColumnMapRowMapper());
        List<ProductClaimTypeDto> productClaimTypeDtoList = mapAllProductClaimDetail(productClaimDetail);
        if (isEmpty(productClaimTypeDtoList))
            return null;
        return productClaimTypeDtoList.get(0);
    }

    public List<ProductClaimTypeDto> searchProductClaimMap(LineOfBusinessEnum lineOfBusinessEnum, String planName) {
        //adding parameters to sqlParameterSource
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource().addValue("lineOfBusiness", lineOfBusinessEnum != null ? lineOfBusinessEnum.name() : "").addValue("planName", planName);
         List<Map<String, Object>> productClaimDetail = namedParameterJdbcTemplate.query(getAllProductClaimMappingDetailsByLineOfBusinessAndPlanName, sqlParameterSource, new ColumnMapRowMapper());
        return mapAllProductClaimDetail(productClaimDetail);


    }

    private List<ProductClaimTypeDto> mapAllProductClaimDetail(List<Map<String, Object>> productClaimDetail){

        CoverageClaimTypeDto prvCoverageClaimTypeDto = new CoverageClaimTypeDto();
        List<CoverageClaimTypeDto> coverageClaimTypeDtoList=new ArrayList<CoverageClaimTypeDto>();
        List<CoverageClaimTypeDto> prvCoverageClaimTypeDtoList=new ArrayList<CoverageClaimTypeDto>();
        ProductClaimTypeDto prvProductClaimTypeDto=new ProductClaimTypeDto();
        List<ProductClaimTypeDto> productClaimTypeDtoList =new ArrayList<ProductClaimTypeDto>();

        int count=0;
        int noOfPlanChanges=0;
        String prvPlanCode=null;
        String curPlanCode=null;
        boolean planChanges=false;

        for (Map<String, Object> productClaims : productClaimDetail) {
            count++;
            String planCode = (String) productClaims.get("planCode");
            String productClaimId =productClaims.get("productClaimId").toString();
            //String productClaimId=String.valueOf(productClaims.get("productClaimId"));
            String coverageId = (String) productClaims.get("coverageId");
            String claim=(String)productClaims.get("claimType");

            if (!planCode.equals(prvProductClaimTypeDto.getPlanCode())) {
                curPlanCode=planCode;
                ProductClaimTypeDto productClaimTypeDto= new ProductClaimTypeDto();
                productClaimTypeDto.setProductClaimId(productClaimId);
                productClaimTypeDto.setPlanCode(planCode);
                productClaimTypeDto.setPlanName((String) productClaims.get("planName"));
                String lineOfBusiness = (String) productClaims.get("lineOfBusiness");
                productClaimTypeDto.setLineOfBusinessDescription(LineOfBusinessEnum.valueOf(lineOfBusiness).getDescription());
                productClaimTypeDto.setLineOfBusiness(LineOfBusinessEnum.valueOf(lineOfBusiness));
                productClaimTypeDtoList.add(productClaimTypeDto);
                prvProductClaimTypeDto=productClaimTypeDto;
            }

            if(count>1){
                if( (prvPlanCode!=null)&&(!prvPlanCode.equals(curPlanCode)) ){
                    planChanges=true;
                    noOfPlanChanges++;
                }
            }
            //add the list of coverageClaims to productClaimType Dto under previous planCode when plan code  changes
            if(planChanges){

                for(ProductClaimTypeDto productClaimTypeDto:productClaimTypeDtoList) {
                    if(productClaimTypeDto.getPlanCode().equals(prvPlanCode)){
                        productClaimTypeDto.updateWithCoverageClaimDto(prvCoverageClaimTypeDtoList);
                        coverageClaimTypeDtoList = new ArrayList<CoverageClaimTypeDto>();
                        prvCoverageClaimTypeDto=new CoverageClaimTypeDto();
                    }
                }
            }
            //creating coverageClaimTypeDtoS list ,if coverage id changes ,make new one
            if (!coverageId.equals(prvCoverageClaimTypeDto.getCoverageId())){

                CoverageClaimTypeDto coverageClaimTypeDto = new CoverageClaimTypeDto();
                coverageClaimTypeDto.setCoverageId(coverageId);
                coverageClaimTypeDto.setCoverageName( (String) productClaims.get("coverageName"));
                Set<String> claimTypes=new LinkedHashSet<String>();
                claimTypes.add(claim);
                coverageClaimTypeDto.setClaimTypes(claimTypes);
                coverageClaimTypeDto=coverageClaimTypeDto.updateWithClaimTypeMap(claimTypes);
                coverageClaimTypeDtoList.add(coverageClaimTypeDto);
                prvCoverageClaimTypeDtoList=coverageClaimTypeDtoList;
                prvCoverageClaimTypeDto=coverageClaimTypeDto;
            }
            //if coverage id is same make update to claimTypeSet previously saved coverageClaimTypeDto in list
            else {
                Set<String> stringClaimSet = prvCoverageClaimTypeDto.getClaimTypes();
                stringClaimSet.add(claim);
                prvCoverageClaimTypeDto.setClaimTypes(stringClaimSet);
                for(CoverageClaimTypeDto coverageClaimTypeDto:coverageClaimTypeDtoList) {
                    if ((prvCoverageClaimTypeDto.getCoverageId()).equals(coverageClaimTypeDto.getCoverageId())) {
                        Set<String> claimSet = coverageClaimTypeDto.getClaimTypes();
                        claimSet.add(claim);
                        coverageClaimTypeDto.setClaimTypes(claimSet);
                        coverageClaimTypeDto = coverageClaimTypeDto.updateWithClaimTypeMap(claimSet);
                        prvCoverageClaimTypeDto = coverageClaimTypeDto;
                    }
                }
            }
            planChanges=false;

            //mapping for only one record
            if(productClaimDetail.size()==1){
                ProductClaimTypeDto productClaimTypeDto=productClaimTypeDtoList.get(0)  ;
                if(productClaimTypeDto.getPlanCode().equals(curPlanCode)){
                    productClaimTypeDto.updateWithCoverageClaimDto(prvCoverageClaimTypeDtoList);
                    coverageClaimTypeDtoList = new ArrayList<CoverageClaimTypeDto>();
                    prvCoverageClaimTypeDto=new CoverageClaimTypeDto();
                    prvCoverageClaimTypeDtoList=coverageClaimTypeDtoList;
                }
            }


            //when plans changes more than once
            if(count==productClaimDetail.size()&& noOfPlanChanges>=1) {
                for (ProductClaimTypeDto productClaimTypeDto : productClaimTypeDtoList) {
                    if (productClaimTypeDto.getPlanCode().equals(curPlanCode)) {
                        productClaimTypeDto.updateWithCoverageClaimDto(prvCoverageClaimTypeDtoList);
                        coverageClaimTypeDtoList = new ArrayList<CoverageClaimTypeDto>();
                        prvCoverageClaimTypeDtoList=coverageClaimTypeDtoList;
                    }
                }
            }

            //when plan code does not change but more than one row
            if(count==productClaimDetail.size()&& noOfPlanChanges==0){
                for(ProductClaimTypeDto productClaimTypeDto:productClaimTypeDtoList) {
                    if(productClaimTypeDto.getPlanCode().equals(prvPlanCode)){
                        productClaimTypeDto.updateWithCoverageClaimDto(prvCoverageClaimTypeDtoList);
                        coverageClaimTypeDtoList = new ArrayList<CoverageClaimTypeDto>();
                        prvCoverageClaimTypeDtoList=coverageClaimTypeDtoList;
                    }
                }
            }

            prvPlanCode=planCode;
        }
        return productClaimTypeDtoList;
    }


}

























