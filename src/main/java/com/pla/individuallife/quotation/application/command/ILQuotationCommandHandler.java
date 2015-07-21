package com.pla.individuallife.quotation.application.command;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.individuallife.quotation.domain.model.*;
import com.pla.individuallife.quotation.domain.service.ILQuotationRoleAdapter;
import com.pla.individuallife.quotation.domain.service.ILQuotationService;
import com.pla.individuallife.quotation.presentation.dto.PlanDetailDto;
import com.pla.individuallife.quotation.presentation.dto.ProposedAssuredDto;
import com.pla.individuallife.quotation.presentation.dto.ProposerDto;
import com.pla.individuallife.quotation.presentation.dto.RiderDetailDto;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.time.LocalDate;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Karunakar on 5/13/2015.
 *
 * Modified by Pradyumna 06/14/2015.
 *
 */
@Component
public class ILQuotationCommandHandler {

    @Autowired
    private IIdGenerator idGenerator;
    @Autowired
    private ILQuotationService quotationService;
    @Autowired
    private Repository<ILQuotation> ilQuotationRepository;

    @CommandHandler
    public QuotationId createQuotation(ILCreateQuotationCommand cmd) {
        ILQuotationProcessor quotationProcessor = ILQuotationRoleAdapter.userToQuotationProcessor(cmd.getUserDetails());
        QuotationId quotationId = new QuotationId(idGenerator.nextId());
        ProposedAssured proposedAssured = ProposedAssured.proposedAssuredBuilder()
                .withFirstName(cmd.getFirstName())
                .withSurname(cmd.getSurname())
                .withNrcNumber(cmd.getNrcNumber())
                .withTitle(cmd.getTitle()).build();
        quotationService.createQuotation(quotationProcessor, quotationId, new AgentId(cmd.getAgentId()),
                proposedAssured, new PlanId(cmd.getPlanId()));
        return quotationId;
    }

    @CommandHandler
    public QuotationId updateProposerDetail(ILUpdateQuotationWithProposerCommand cmd) {
        ILQuotation quotation = ilQuotationRepository.load(new QuotationId(cmd.getQuotationId()));
        ILQuotationProcessor quotationProcessor = ILQuotationRoleAdapter.userToQuotationProcessor(cmd.getUserDetails());
        ProposerDto dto = cmd.getProposerDto();
        Proposer proposer = new Proposer(dto.getTitle(), dto.getFirstName(), dto.getSurname(), dto.getNrcNumber(),
                dto.getDateOfBirth(), dto.getGender(), dto.getMobileNumber(), dto.getEmailAddress());

        if (quotation.requireVersioning()) {
            QuotationId newQuotationId = quotationService.updateProposerWithVersion(quotationProcessor, quotation, proposer);
            return newQuotationId;

        } else {
            quotation.updateWithProposer(quotationProcessor, proposer);
            return new QuotationId(cmd.getQuotationId());
        }
    }

    @CommandHandler
    public QuotationId updateProposedAssuredDetail(ILUpdateQuotationWithAssuredCommand cmd) {

        ILQuotation quotation = ilQuotationRepository.load(new QuotationId(cmd.getQuotationId()));
        ILQuotationProcessor quotationProcessor = ILQuotationRoleAdapter.userToQuotationProcessor(cmd.getUserDetails());
        ProposedAssuredDto dto = cmd.getProposedAssured();
        ProposedAssured proposedAssured = ProposedAssured.proposedAssuredBuilder()
                .withGender(dto.getGender())
                .withTitle(dto.getTitle())
                .withFirstName(dto.getFirstName())
                .withNrcNumber(dto.getNrcNumber())
                .withEmailAddress(dto.getEmailAddress())
                .withMobileNumber(dto.getMobileNumber())
                .withDateOfBirth(dto.getDateOfBirth())
                .withOccupation(dto.getOccupation())
                .withSurname(dto.getSurname()).build();

        if (quotation.requireVersioning()) {
            QuotationId newQuotationId = quotationService.updateAssuredDetailWithVersion(quotationProcessor,
                    quotation, proposedAssured,
                    cmd.isAssuredTheProposer());
            return newQuotationId;

        } else {
            quotation.updateWithAssured(quotationProcessor,
                    proposedAssured, cmd.isAssuredTheProposer());
            return new QuotationId(cmd.getQuotationId());
        }

    }

    @CommandHandler(commandName = "QUOTATION_VERSION_CMD")
    public void updateQuotationVersion(GenericCommandMessage cmd) {
        Map payload = (Map) cmd.getPayload();
        ILQuotation quotation = ilQuotationRepository.load(payload.get("quotationId"));
        quotation.assignVersion((int) payload.get("version"));
    }


    @CommandHandler
    public QuotationId updatePlanDetail(ILUpdateQuotationWithPlanCommand cmd) {
        ILQuotation quotation = ilQuotationRepository.load(new QuotationId(cmd.getQuotationId()));
        ILQuotationProcessor quotationProcessor = ILQuotationRoleAdapter.userToQuotationProcessor(cmd.getUserDetails());
        PlanDetailDto dto = cmd.getPlanDetailDto();
        PlanDetail planDetail = new PlanDetail(new PlanId(dto.getPlanId())
                , dto.getPolicyTerm(), dto.getPremiumPaymentTerm(), dto.getSumAssured());
        Set<RiderDetail> riders = new HashSet();
        if (dto.getRiderDetails() != null) {
            for (RiderDetailDto each : dto.getRiderDetails()) {
                riders.add(new RiderDetail(each.getCoverageId(),
                        each.getSumAssured(), each.getCoverTerm(), each.getWaiverOfPremium()));
            }
        }
        if (quotation.requireVersioning()) {
            QuotationId newQuotationId = quotationService.updateWithPlanWithVersion(quotationProcessor,
                    quotation, planDetail,
                    riders);
            return newQuotationId;

        } else {
            quotation.updateWithPlanAndRider(quotationProcessor, planDetail, riders);
            return new QuotationId(cmd.getQuotationId());
        }
    }

    @CommandHandler
    public void generateQuotation(ILGenerateQuotationCommand cmd) {
        ILQuotation quotation = ilQuotationRepository.load(new QuotationId(cmd.getQuotationId()));
        quotation.generateQuotation(LocalDate.now());
    }

    @CommandHandler
    public void convertedILQuotation(ILQuotationConvertedCommand cmd) {
        ILQuotation quotation = ilQuotationRepository.load(cmd.getQuotationId());
        quotation.closeQuotation();
    }
}
