package com.pla.individuallife.proposal.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.identifier.QuestionId;
import com.pla.individuallife.proposal.domain.model.AgentCommissionShareModel;
import com.pla.individuallife.proposal.domain.model.Question;
import com.pla.individuallife.proposal.presentation.dto.AgentDetailDto;
import com.pla.individuallife.proposal.presentation.dto.QuestionDto;
import com.pla.individuallife.proposal.query.ILProposalFinder;
import com.pla.individuallife.quotation.query.ILQuotationDto;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 7/31/2015.
 */
@Service
public class ILProposalService {


    @Autowired
    private ILQuotationFinder quotationFinder;

    @Autowired
    private ILProposalFinder proposalFinder;


    public boolean hasProposalForQuotation(String quotationId){
        if (isEmpty(quotationId))
            return false;
        ILQuotationDto dto = quotationFinder.getQuotationById(quotationId);
        Map proposalMap = proposalFinder.findProposalByQuotationNumber(dto.getQuotationNumber());
        if (isNotEmpty(proposalMap))
            return true;
        return false;
    }

}
