package com.pla.core.hcp.query;

import com.pla.core.hcp.domain.model.HCP;
import com.pla.core.hcp.domain.model.HCPCode;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 12/17/2015.
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

    public List<Map<String, Object>> getAllHCPByHCPCode(String hcpCode) {
        return namedParameterJdbcTemplate.queryForList("SELECT hcp_code as hcpCode, hcp_name as hcpName from hcp where hcp_code like :hcpCode order by hcp_name",new MapSqlParameterSource("hcpCode","%"+hcpCode+"%"));
    }

    public HCP getHCPByHCPCode(String hcpCode) {
        return namedParameterJdbcTemplate.queryForObject("SELECT * from hcp where hcp_code=:hcpCode", new MapSqlParameterSource("hcpCode", hcpCode), new RowMapper<HCP>() {
            @Override
            public HCP mapRow(ResultSet rs, int rowNum) throws SQLException {
                HCP hcp = new HCP()
                        .updateWithHCPCode(new HCPCode(rs.getString("hcp_code")))
                        .updateWithHCPName(rs.getString("hcp_name"))
                        .updateWithHcpAddress(rs.getString("address_line1"),rs.getString("address_line2"), rs.getString("postal_code"), rs.getString("province"), rs.getString("town"));
                return hcp;
            }
        });

    }

    public List<Map<String, Object>> getAllHCPByHCPName(String hcpName) {
        return namedParameterJdbcTemplate.queryForList("SELECT hcp_code as hcpCode, hcp_name as hcpName from hcp where hcp_name like :hcpName order by hcp_name",new MapSqlParameterSource("hcpName","%"+hcpName+"%"));
    }
}
