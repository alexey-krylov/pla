package com.pla.grouphealth.claim.cashless.application.command.claim;

import com.pla.grouphealth.claim.cashless.application.service.claim.GroupHealthCashlessClaimService;
import com.pla.grouphealth.claim.cashless.application.service.preauthorization.PreAuthorizationService;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimBatch;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorization;
import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimUploadedExcelDataDto;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRequestRepository;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.util.Assert.notEmpty;

/**
 * Author - Mohan Sharma Created on 2/3/2016.
 */
@Component
public class GroupHealthCashlessCommandHandler {
    @Autowired
    private GroupHealthCashlessClaimService groupHealthCashlessClaimService;
    @Autowired
    private PreAuthorizationRequestRepository preAuthorizationRequestRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    PreAuthorizationService preAuthorizationService;
    @Autowired
    SequenceGenerator sequenceGenerator;
    @Autowired
    private Repository<GroupHealthCashlessClaim> groupHealthCashlessClaimAxonRepository;

    @CommandHandler
    public void createClaim(UploadGroupHealthCashlessClaimCommand uploadGroupHealthCashlessClaimCommand){
        List<List<ClaimUploadedExcelDataDto>> refurbishedSet = preAuthorizationService.createSubListBasedOnSimilarCriteria(uploadGroupHealthCashlessClaimCommand.getClaimUploadedExcelDataDtos());
        notEmpty(refurbishedSet, "Error uploading no PreAuthorization data list found to save");
        List<GroupHealthCashlessClaim> groupHealthCashlessClaimList = refurbishedSet.stream().map(new Function<List<ClaimUploadedExcelDataDto>, GroupHealthCashlessClaim>() {
            @Override
            public GroupHealthCashlessClaim apply(List<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos) {
                return groupHealthCashlessClaimService.constructGroupHealthCashlessClaimEntity(claimUploadedExcelDataDtos, uploadGroupHealthCashlessClaimCommand.getBatchDate(), uploadGroupHealthCashlessClaimCommand.getBatchUploaderUserId(), uploadGroupHealthCashlessClaimCommand.getHcpCode());
            }
        }).collect(Collectors.toList());
    }
}
