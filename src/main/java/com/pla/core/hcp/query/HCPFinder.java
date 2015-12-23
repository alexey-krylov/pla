package com.pla.core.hcp.query;

import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Mohan Sharma on 12/17/2015.
 */
@Finder
@Component
public class HCPFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static String constructQueryByCriteria(String name, String code){
        StringBuffer stringBuffer=new StringBuffer("select hcp_code AS hcpCode from hcp where ");
        if(isNotEmpty(name)){
            stringBuffer.append("hcp_name like :name");
        }
        if(isNotEmpty(name) && isNotEmpty(code))
            stringBuffer.append(" and ");
        if(isNotEmpty(code)){
            stringBuffer.append("hcp_code like :code");
        }
        return  stringBuffer.toString();
    }

    public List<Map<String, Object>> getAllHCPMathingTheCriteria(String hcpName, String hcpCode) {
        return namedParameterJdbcTemplate.queryForList(constructQueryByCriteria(hcpName, hcpCode),new MapSqlParameterSource("name","%"+hcpName+"%").addValue("code","%"+hcpCode+"%"));
    }
}
