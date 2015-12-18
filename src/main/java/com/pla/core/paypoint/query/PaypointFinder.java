package com.pla.core.paypoint.query;

import com.google.common.base.Preconditions;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
/**
 * Created by Rudra on 12/12/2015.
 */
@Finder
@Service
public class PaypointFinder {

    public static final String FIND_ALL_PAY_POINT_LIST_= "select pay_point_id AS payPointId, pay_point_name AS payPointName,pay_point_status AS payPointStatus, pay_point_grade AS payPointGrade from pay_point";

    public static final String FIND_PAY_POINT_RECORD_FOR_VIEW= "SELECT pay_point_id AS payPointId, pay_point_name AS payPointName,pay_point_status AS payPointStatus, \n" +
            "pay_point_grade AS payPointGrade, contact_pay_point_address_line1 AS payPointContactAddressLine1,\n" +
            "contact_pay_point_address_line2 AS payPointContactAddressLine2, contact_pay_point_postal_code AS payPointContactAddressPostalCode,\n" +
            "contact_pay_point_province AS payPointContactAddressProvince ,contact_pay_point_town AS payPointContactAddressTown,\n" +
            "pysical_pay_point_address_line1 AS payPointPhysicalAddressLine1, pysical_pay_point_address_line2 AS payPointPhysicalAddressLine2,\n" +
            "pysical_pay_point_postal_code AS payPointPhysicalAddressPostalCode, pysical_pay_point_province AS payPointPhysicalAddressProvince,\n" +
            "pysical_pay_point_town AS payPointPhysicalAddressTown, staff_compliment AS staffCompliment,minimum_income AS minimumIncome,pay_point_charge AS payPointCharge, \n" +
            "first_name AS firstName, sur_name AS surName, phone_number AS phoneNumber, email_id AS emailId, pay_point_liasion AS payPointLiasion ,pay_point_email_id AS payPointEmailId ,\n" +
            "ddacc_id AS ddaccId, bank_name AS bankName, bank_branch_name AS bankBranchName, bank_code AS bankCode,prompt_date_schedules AS promptDateSchedules, \n" +
            "prompt_date_premium AS promptDatePremium FROM pay_point WHERE pay_point_id =:payPointId";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<Map<String,Object>> getAllPayPointDetail(){
        return namedParameterJdbcTemplate.queryForList(FIND_ALL_PAY_POINT_LIST_, EmptySqlParameterSource.INSTANCE);
    }
}
