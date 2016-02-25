package com.pla.core.hcp.application.service;

import com.google.common.collect.Maps;
import com.pla.core.hcp.application.command.CreateOrUpdateHCPCommand;
import com.pla.core.hcp.domain.model.HCP;
import com.pla.core.hcp.domain.model.HCPCategory;
import com.pla.core.hcp.domain.model.HCPCode;
import com.pla.core.hcp.domain.model.HCPStatus;
import com.pla.core.hcp.query.HCPFinder;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.apache.commons.lang.StringUtils;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 12/17/2015.
 */
@DomainService
public class HCPService {
    JpaRepositoryFactory jpaRepositoryFactory;
    private SimpleJpaRepository<HCP, HCPCode> hcpRepository;
    private HCPFinder hcpFinder;
    SequenceGenerator sequenceGenerator;

    @Autowired
    public HCPService(JpaRepositoryFactory jpaRepositoryFactory, HCPFinder hcpFinder, SequenceGenerator sequenceGenerator){
        hcpRepository = jpaRepositoryFactory.getCrudRepository(HCP.class);
        this.hcpFinder = hcpFinder;
        this.sequenceGenerator = sequenceGenerator;
    }

    @Transactional
    public HCP createOrUpdateHCP(CreateOrUpdateHCPCommand createOrUpdateHCPCommand) {
        String runningSequence = StringUtils.EMPTY;
        if(isEmpty(createOrUpdateHCPCommand.getHcpCode())) {
            runningSequence = sequenceGenerator.getSequence(HCP.class);
            runningSequence = String.format("%04d", Integer.parseInt(runningSequence.trim()));
        }
        String hcpCodeFormatted = isNotEmpty(createOrUpdateHCPCommand.getHcpCode()) ? createOrUpdateHCPCommand.getHcpCode() : HCPCode.getHCPCode(StringUtils.substring(createOrUpdateHCPCommand.getTown(), 0, 3), runningSequence, createOrUpdateHCPCommand.getHcpCategory());
        HCP hcp = hcpRepository.findOne(new HCPCode(hcpCodeFormatted));
        if(isEmpty(hcp)){
            hcp = new HCP();
        }
        hcp.updateWithHCPCode(new HCPCode(hcpCodeFormatted))
                .updateWithActivatedOn(createOrUpdateHCPCommand.getActivatedOn())
                .updateWithHcpCategory(createOrUpdateHCPCommand.getHcpCategory())
                .updateWithHcpAddress(createOrUpdateHCPCommand.getAddressLine1(), createOrUpdateHCPCommand.getAddressLine2(), createOrUpdateHCPCommand.getPostalCode(), createOrUpdateHCPCommand.getProvince(), createOrUpdateHCPCommand.getTown())
                .updateWithHCPName(createOrUpdateHCPCommand.getHcpName())
                .updateWithHcpStatus(createOrUpdateHCPCommand.getHcpStatus())
                .updateWithHcpContactDetail(createOrUpdateHCPCommand.getEmailId(), createOrUpdateHCPCommand.getWorkPhoneNumber())
                .updateWithHcpContactPersonDetail(createOrUpdateHCPCommand.getContactPersonDetail(), createOrUpdateHCPCommand.getContactPersonMobile(), createOrUpdateHCPCommand.getContactPersonWorkPhoneNumber(), createOrUpdateHCPCommand.getContactPersonEmailId())
                .updateWithHcpBankDetail(createOrUpdateHCPCommand.getBankName(),createOrUpdateHCPCommand.getBankBranchCode(),createOrUpdateHCPCommand.getBankAccountType(),createOrUpdateHCPCommand.getBankAccountNumber(),createOrUpdateHCPCommand.getBankBranchSortCode());
        return hcpRepository.saveAndFlush(hcp);
    }

    public CreateOrUpdateHCPCommand getHCPByHCPCode(String hcpCode){
        HCP hcp = hcpRepository.findOne(new HCPCode(hcpCode));
        return CreateOrUpdateHCPCommand.setPropertiesFromHCPEntity(hcp);
    }

    public List<CreateOrUpdateHCPCommand> getAllHCP(){
        List<HCP> hcp = hcpRepository.findAll();
        List<CreateOrUpdateHCPCommand> createOrUpdateHCPCommandList = hcp.stream().map(new Function<HCP, CreateOrUpdateHCPCommand>() {
            @Override
            public CreateOrUpdateHCPCommand apply(HCP hcp) {
                return CreateOrUpdateHCPCommand.setPropertiesFromHCPEntity(hcp);
            }
        }).collect(Collectors.toList());
        return createOrUpdateHCPCommandList;
    }

    public Set<String> getAllHCPStatus() {
        return Stream.of(HCPStatus.values()).map(HCPStatus :: name).collect(Collectors.toSet());
    }

    public List<Map<String, String>> getAllHCPCategories() {
        return Stream.of(HCPCategory.values()).map(new Function<HCPCategory, Map<String, String>>() {
            @Override
            public Map<String, String> apply(HCPCategory hcpCategory) {
                Map<String, String> resultSet = Maps.newHashMap();
                resultSet.put("name",hcpCategory.name());
                resultSet.put("description",  hcpCategory.description);
                return resultSet;
            }
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllHCPMathingTheCriteria(String hcpName, String hcpCode) {
        return hcpFinder.getAllHCPMathingTheCriteria(hcpName, hcpCode);
    }

    public List<Map<String, Object>> getAllHCPByHCPCode(String hcpCode) {
        return hcpFinder.getAllHCPByHCPCode(hcpCode);
    }

    public List<Map<String, Object>> getAllHCPByHCPName(String hcpName) {
        return hcpFinder.getAllHCPByHCPName(hcpName);
    }
}
