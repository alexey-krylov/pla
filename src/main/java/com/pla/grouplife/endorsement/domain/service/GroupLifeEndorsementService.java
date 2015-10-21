package com.pla.grouplife.endorsement.domain.service;

import com.google.common.collect.Lists;
import com.pla.grouplife.endorsement.domain.model.GLEndorsementProcessor;
import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsement;
import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsementStatusAudit;
import com.pla.grouplife.endorsement.presentation.dto.GLEndorsementApproverCommentDto;
import com.pla.grouplife.endorsement.repository.GLEndorsementStatusAuditRepository;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.EndorsementId;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 8/27/2015.
 */
@Service
public class GroupLifeEndorsementService {

    private GroupLifeEndorsementRoleAdapter groupLifeEndorsementRoleAdapter;

    private GLEndorsementNumberGenerator glEndorsementNumberGenerator;

    private GLFinder glFinder;

    @Autowired
    private GLEndorsementStatusAuditRepository glEndorsementStatusAuditRepository;

    @Autowired
    public GroupLifeEndorsementService(GroupLifeEndorsementRoleAdapter groupLifeEndorsementRoleAdapter, GLEndorsementNumberGenerator glEndorsementNumberGenerator, GLFinder glFinder) {
        this.groupLifeEndorsementRoleAdapter = groupLifeEndorsementRoleAdapter;
        this.glEndorsementNumberGenerator = glEndorsementNumberGenerator;
        this.glFinder = glFinder;
    }

    public GroupLifeEndorsement createEndorsement(String policyId, GLEndorsementType glEndorsementType, UserDetails userDetails) {
        GLEndorsementProcessor glEndorsementProcessor = groupLifeEndorsementRoleAdapter.userToEndorsementProcessor(userDetails);
        String endorsementId = ObjectId.get().toString();
        String endorsementNumber = glEndorsementNumberGenerator.getEndorsementNumber(GroupLifeEndorsement.class, LocalDate.now());
        Map<String, Object> policyMap = glFinder.findPolicyById(policyId);
        String policyNumber = ((PolicyNumber) policyMap.get("policyNumber")).getPolicyNumber();
        String policyHolderName =  policyMap.get("proposer")!=null?((Proposer) policyMap.get("proposer")).getProposerName():null;
        return glEndorsementProcessor.createEndorsement(endorsementId, endorsementNumber, policyId, policyNumber, policyHolderName, glEndorsementType);
    }

    public List<GLEndorsementApproverCommentDto> findApproverComments(String endorsementId) {
        List<GroupLifeEndorsementStatusAudit> audits = glEndorsementStatusAuditRepository.findByEndorsementId(new EndorsementId(endorsementId));
        List<GLEndorsementApproverCommentDto> endorsementApproverCommentsDtos = Lists.newArrayList();
        if (isNotEmpty(audits)) {
            endorsementApproverCommentsDtos = audits.stream().map(new Function<GroupLifeEndorsementStatusAudit, GLEndorsementApproverCommentDto>() {
                @Override
                public GLEndorsementApproverCommentDto apply(GroupLifeEndorsementStatusAudit groupLifeEndorsementStatusAudit) {
                    GLEndorsementApproverCommentDto endorsementApproverCommentDto = new GLEndorsementApproverCommentDto();
                    try {
                        BeanUtils.copyProperties(endorsementApproverCommentDto, groupLifeEndorsementStatusAudit);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return endorsementApproverCommentDto;
                }
            }).collect(Collectors.toList());
        }
        return endorsementApproverCommentsDtos;
    }
}
