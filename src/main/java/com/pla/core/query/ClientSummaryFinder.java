package com.pla.core.query;

import com.google.common.collect.Lists;
import com.pla.client.domain.model.Client;
import com.pla.core.dto.ClientPolicyDetailDto;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.proposal.domain.model.GroupHealthProposal;
import com.pla.grouplife.policy.domain.model.GroupLifePolicy;
import com.pla.grouplife.proposal.domain.model.GroupLifeProposal;
import com.pla.individuallife.policy.domain.model.IndividualLifePolicy;
import com.pla.individuallife.proposal.domain.model.ILProposalAggregate;
import com.pla.individuallife.proposal.domain.model.ILProposalStatusAudit;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import com.pla.sharedkernel.domain.model.Gender;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;


/**
 * Created by nthdimensioncompany
 */

@Service
public class ClientSummaryFinder {

    private MongoTemplate mongoTemplate;

    @Autowired
    public ClientSummaryFinder(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;

    }

    public List<ClientDetailDto> searchClientSummaryDetail(String firstName, String clientId, DateTime dateOfBirth, Gender gender,
                                                           String nrc, String companyName) {

        if (isEmpty(firstName) && isEmpty(clientId) && isEmpty(nrc) && isEmpty(companyName) && dateOfBirth == null && gender == null) {
            return Lists.newArrayList();
        }

        Criteria criteria = new Criteria();

        if (isNotEmpty(firstName)) {
            criteria = Criteria.where("clientName").is(firstName);

        }

        if (isNotEmpty(clientId)) {
            criteria = criteria.and("_id.clientId").is(clientId);

        }

        if (dateOfBirth != null) {
            criteria = criteria.and("dateOfBirth").is(dateOfBirth);

        }
        if (gender != null) {
            criteria = criteria.and("gender").is(gender);

        }
        if (isNotEmpty(nrc)) {
            criteria = criteria.and("nrcNumber").is(nrc);
        }
        if (isNotEmpty(companyName)) {
            criteria = criteria.and("companyName").is(companyName);
        }
        Query query = new Query(criteria);
        List<Client> clientList = mongoTemplate.find(query, Client.class);
        return convertToClientDetailDto(clientList);

    }


    public List<ClientDetailDto> getAllClientSummaryDetail() {

        List<Client> clientList = mongoTemplate.findAll(Client.class);
        if (isEmpty(clientList)) {
            return Lists.newArrayList();
            //return null;
        }
        //ClientDetailDto clientDetailDto=new ClientDetailDto();
        List<ClientDetailDto> clientDetailDtoList = convertToClientDetailDto(clientList);
        return clientDetailDtoList;
    }


    public ClientDetailDto getClientSummaryDetailByClientId(String clientId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id.clientId").is(clientId));
        Client client = mongoTemplate.findOne(query, Client.class);
        List<ClientDetailDto> clientDetailDtoList = convertToClientDetailDto(Lists.newArrayList(client));
        if (isEmpty(clientDetailDtoList))
            return null;
        return clientDetailDtoList.get(0);
    }

    public ClientDetailDto getClientProposalDetails(String clientId) {

         //ientDetailDto clientDetailDto = getClientSummaryDetailByClientId(clientId);
        ClientDetailDto clientDetailDto = new ClientDetailDto();
        List<ClientPolicyDetailDto> clientProposalDetailDtoList = new ArrayList<ClientPolicyDetailDto>();
        List<ClientPolicyDetailDto> clientPolicyDetailDtoList = new ArrayList<ClientPolicyDetailDto>();
        String proposalStatuses[]=new String[]{"DRAFT","PENDING_ACCEPTANCE","APPROVED","RETURNED","PENDING_FIRST_PREMIUM"};
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria=Criteria.where("proposer.clientId").is(clientId).orOperator(Criteria.where("proposedAssured.clientId").is(clientId));
       // criteria = Criteria.where("proposer.proposerCode").is(clientId);
        criteria = criteria.and("proposalStatus").in(proposalStatuses);
        //querying collection ilProposalAggregate on clientId
        List<ILProposalAggregate> individualLifeProposalList = mongoTemplate.find(query, ILProposalAggregate.class);

        ClientPolicyDetailDto clientProposalDto = null;
        if (isEmpty(individualLifeProposalList)) {
            return null;
        }

        for (ILProposalAggregate individualLifeProposal :  individualLifeProposalList) {
            //checking for proposalStatus
           // query.addCriteria(Criteria.where("proposalId").is(individualLifeProposal.getProposalId().getProposalId()));
            //checking for comments by proposalId
            //ILProposalStatusAudit ilProposalStatusAudit = mongoTemplate.findOne(query, ILProposalStatusAudit.class);
            //String comments = ilProposalStatusAudit.getComment();

            //creating client proposalDtos
            clientProposalDto = new ClientPolicyDetailDto();
            clientProposalDto.setNumber(individualLifeProposal.getProposalNumber());
           // clientProposalDto.setUnderWriterComments(comments);
            clientProposalDto.setUnderWriterDecision("");
            //checking for proposer or proposerAssured
            if (individualLifeProposal.getProposer().getIsProposedAssured()) {
                clientProposalDto.setClientType("Proposer");
            } else {
                clientProposalDto.setClientType("Proposed Assured");
            }
            //Adding Dtos to ProposalDtoList
            clientProposalDetailDtoList.add(clientProposalDto);

            criteria = Criteria.where("proposer.proposerCode").is(clientId);
            criteria = criteria.and("status").is("IN_FORCE");

            List<IndividualLifePolicy> individualLifePolicyList = mongoTemplate.find(query, IndividualLifePolicy.class);
            if (isEmpty(individualLifePolicyList)) {
                return null;
            }
            for (IndividualLifePolicy individualLifePolicy : individualLifePolicyList) {
                ClientPolicyDetailDto clientPolicyDetailDto = new ClientPolicyDetailDto();
                //check for comments in ILProposalStatusAudit with proposal id
                query.addCriteria(Criteria.where("proposalId").is(individualLifePolicy.getProposal().getProposalId()));
                ILProposalStatusAudit ilProposalStatusAudit = mongoTemplate.findOne(query, ILProposalStatusAudit.class);
                String comments = ilProposalStatusAudit.getComment();
                clientPolicyDetailDto.setNumber(individualLifePolicy.getPolicyNumber().getPolicyNumber());
               // clientPolicyDetailDto.setUnderWriterComments(comments);
                clientPolicyDetailDto.setUnderWriterDecision("");
                //setting clientType according to IsProposedAssured

                if (individualLifePolicy.getProposer().getIsProposedAssured()) {
                    clientProposalDto.setClientType("Policyholder");
                } else {
                    clientProposalDto.setClientType("Assured");
                }
                clientPolicyDetailDtoList.add(clientPolicyDetailDto);
            }
        }
        clientDetailDto.updateWithClientProposalDetails(clientProposalDetailDtoList);
        clientDetailDto.updateWithClientPolicyDetails(clientProposalDetailDtoList);
        return clientDetailDto;
    }


    private List<ClientDetailDto> convertToClientDetailDto(List<Client> clientList) {
        List<ClientDetailDto> clientDetailDtoList = new ArrayList<ClientDetailDto>();

        for (Client client : clientList) {
            ClientDetailDto clientDetailDto = new ClientDetailDto(client.getClientCode().getClientId(), client.getClientName(), client.getAddress1(), client.getAddress2(),
                    client.getPostalCode(), client.getProvince(), client.getTown(), client.getEmailAddress());


            List<Map<String, Object>> allClientDocuments = client.findClientDocument();
            List<ClientDetailDto.ClientDocumentDetailDto> clientDocumentDetailDtoList = new ArrayList<ClientDetailDto.ClientDocumentDetailDto>();
            for (Map<String, Object> document : allClientDocuments) {
                ClientDetailDto.ClientDocumentDetailDto clientDocumentDetailDto = clientDetailDto.new ClientDocumentDetailDto();
                clientDocumentDetailDto.setDocumentId((String) document.get("documentId"));
                clientDocumentDetailDto.setDocumentType((String) document.get("documentType"));
                clientDocumentDetailDto.setDocumentContent((String) document.get("documentContent"));
                clientDocumentDetailDto.setRoutingLevel((String) document.get("routingLevel"));
                clientDocumentDetailDtoList.add(clientDocumentDetailDto);
            }
            clientDetailDto.setClientDocumentDetailDtoList(clientDocumentDetailDtoList);

            clientDetailDtoList.add(clientDetailDto);
        }
        return clientDetailDtoList;
    }

    public ClientDetailDto getClientDetail(String clientId, String lineOfBusiness) {

        ClientDetailDto clientDetailDto=null;
        if (lineOfBusiness.equals("Group Life")) {

            clientDetailDto=  getGLClientProposalAndPolicyDetail(clientId);

        }
        if (lineOfBusiness.equals("Individual Life")) {

          // clientDetailDto=  getILClientProposalAndPolicyDetail(clientId);

        }

        if (lineOfBusiness.equals("Group Health")) {

            clientDetailDto=  getGHClientProposalAndPolicyDetail(clientId);

        }
        return clientDetailDto;
    }


    public ClientDetailDto getGLClientProposalAndPolicyDetail(String clientId) {

        ClientDetailDto clientDetailDto= getClientSummaryDetailByClientId(clientId);
        //ClientDetailDto clientDetailDto = new ClientDetailDto();
        List<ClientPolicyDetailDto> clientProposalDetailDtoList = new ArrayList<ClientPolicyDetailDto>();
        List<ClientPolicyDetailDto> clientPolicyDetailDtoList = new ArrayList<ClientPolicyDetailDto>();
        Query query = new Query();
        Criteria criteria = new Criteria();
        String proposalStatuses[]=new String[]{"DRAFT","PENDING_ACCEPTANCE","APPROVED","RETURNED","PENDING_FIRST_PREMIUM"};
        //Query query3 = new Query();

       // criteria = Criteria.where("proposer.proposerCode").is(clientId);
        criteria = Criteria.where("insureds.occupationClass").is("Advocates");
        criteria = criteria.and("proposalStatus").in(proposalStatuses);
        //querying collection GLProposal on
        List<GroupLifeProposal> groupLifeProposalList = mongoTemplate.find(query, GroupLifeProposal.class);
        if (isEmpty(groupLifeProposalList)) {
            return null;

        }
        for (GroupLifeProposal groupLifeProposal : groupLifeProposalList) {
            ClientPolicyDetailDto clientProposalDto = new ClientPolicyDetailDto();
            clientProposalDto.setNumber(groupLifeProposal.getProposalNumber().getProposalNumber());
            clientProposalDto.setClientType("");
            clientProposalDto.setUnderWriterComments("");
            clientProposalDto.setUnderWriterComments("");
            clientProposalDetailDtoList.add(clientProposalDto);
        }
          //querying for GlPolicy
        criteria = Criteria.where("proposer.proposerCode").is(clientId);
        criteria = criteria.and("status").is("IN_FORCE");
        List<GroupLifePolicy> groupLifePolicyList = mongoTemplate.find(query, GroupLifePolicy.class);
        if (isEmpty(groupLifePolicyList)) {
             return null;
        }
                for (GroupLifePolicy groupLifePolicy : groupLifePolicyList) {
                    ClientPolicyDetailDto clientPolicyDto = new ClientPolicyDetailDto();
                    clientPolicyDto.setNumber(groupLifePolicy.getPolicyNumber().getPolicyNumber());
                    clientPolicyDto.setClientType("");
                    clientPolicyDto.setUnderWriterComments("");
                    clientPolicyDto.setUnderWriterComments("");
                    clientPolicyDetailDtoList.add(clientPolicyDto);
                }
        clientDetailDto.updateWithClientProposalDetails(clientProposalDetailDtoList);
        clientDetailDto.updateWithClientPolicyDetails(clientPolicyDetailDtoList);
        return clientDetailDto;

        }

    public ClientDetailDto getGHClientProposalAndPolicyDetail(String clientId) {

        ClientDetailDto clientDetailDto= getClientSummaryDetailByClientId(clientId);
        //ClientDetailDto clientDetailDto = new ClientDetailDto();
        List<ClientPolicyDetailDto> clientProposalDetailDtoList = new ArrayList<ClientPolicyDetailDto>();
        List<ClientPolicyDetailDto> clientPolicyDetailDtoList = new ArrayList<ClientPolicyDetailDto>();

        Query query = new Query();
        Criteria criteria = new Criteria();
        String proposalStatuses[]=new String[]{"DRAFT","PENDING_ACCEPTANCE","APPROVED","RETURNED","PENDING_FIRST_PREMIUM"};
        criteria = Criteria.where("proposer.proposerCode").is(clientId);
        criteria = criteria.and("proposalStatus").in(proposalStatuses);
        //querying collection GHProposal
        List<GroupHealthProposal> groupHealthProposalList = mongoTemplate.find(query, GroupHealthProposal.class);
        if (isEmpty(groupHealthProposalList)) {
            return null;
        }
        for (GroupHealthProposal groupHealthProposal : groupHealthProposalList) {
            ClientPolicyDetailDto clientProposalDto = new ClientPolicyDetailDto();
            clientProposalDto.setNumber(groupHealthProposal.getProposalNumber().getProposalNumber());
            clientProposalDto.setClientType("");
            clientProposalDto.setUnderWriterComments("");
            clientProposalDto.setUnderWriterComments("");
            clientProposalDetailDtoList.add(clientProposalDto);
        }
        //querying for GHPolicy
        criteria = Criteria.where("proposer.proposerCode").is(clientId);
        criteria = criteria.and("status").is("IN_FORCE");
        List<GroupHealthPolicy> groupHealthPolicyList = mongoTemplate.find(query, GroupHealthPolicy.class);
        if(isEmpty(groupHealthPolicyList)) {
            return null;
        }
        for (GroupHealthPolicy groupHealthPolicy : groupHealthPolicyList) {
            ClientPolicyDetailDto clientPolicyDto = new ClientPolicyDetailDto();
            clientPolicyDto.setNumber(groupHealthPolicy.getPolicyNumber().getPolicyNumber());
            clientPolicyDto.setClientType("");
            clientPolicyDto.setUnderWriterComments("");
            clientPolicyDto.setUnderWriterComments("");
            clientPolicyDetailDtoList.add(clientPolicyDto);
        }
        clientDetailDto.updateWithClientProposalDetails(clientProposalDetailDtoList);
        clientDetailDto.updateWithClientPolicyDetails(clientPolicyDetailDtoList);
        return clientDetailDto;
    }
}