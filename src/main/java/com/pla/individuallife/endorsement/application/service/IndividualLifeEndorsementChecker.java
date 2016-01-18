package com.pla.individuallife.endorsement.application.service;

import com.pla.individuallife.endorsement.domain.model.IndividualLifeEndorsement;
import com.pla.individuallife.endorsement.query.ILEndorsementFinder;
import com.pla.individuallife.policy.finder.ILPolicyFinder;
import com.pla.individuallife.sharedresource.model.ILEndorsementType;
import com.pla.sharedkernel.domain.model.Relationship;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Raghu on 30-Nov-15.
 */
@Service
public class IndividualLifeEndorsementChecker {

    private ILEndorsementFinder ilEndorsementFinder;

    private ILPolicyFinder ilPolicyFinder;


    @Autowired
    IndividualLifeEndorsementChecker(ILEndorsementFinder ilEndorsementFinder, ILPolicyFinder ilPolicyFinder){
        this.ilEndorsementFinder = ilEndorsementFinder;
        this.ilPolicyFinder =  ilPolicyFinder;
    }

    public Boolean isValidMemberAddition(String policyNumber){

        return true;
    }

    public Boolean isValidMemberPromotion(String policyNumber){

        return true;
    }
}
