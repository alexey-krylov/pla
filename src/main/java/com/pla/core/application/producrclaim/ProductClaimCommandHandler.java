package com.pla.core.application.producrclaim;

import com.pla.core.domain.model.CoverageClaimMapper;
import com.pla.core.domain.model.ProductClaimMapper;
import com.pla.core.dto.CoverageClaimTypeDto;
import com.pla.sharedkernel.identifier.CoverageId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Admin on 9/2/2015.
 */
@Component
public class ProductClaimCommandHandler {

    private JpaRepositoryFactory jpaRepositoryFactory;


    private static final Logger LOGGER = LoggerFactory.getLogger(ProductClaimCommandHandler.class);

    @Autowired
    public ProductClaimCommandHandler(JpaRepositoryFactory jpaRepositoryFactory) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
    }


    @CommandHandler
    public void createProductClaimCommandHandler(CreateProductClaimCommand createProductClaimCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Command Received*****" + createProductClaimCommand);
        }
        JpaRepository<ProductClaimMapper, Long> coverageRepository = jpaRepositoryFactory.getCrudRepository(ProductClaimMapper.class);
        List<CoverageClaimMapper> coverageClaimMappers =  createProductClaimCommand.getCoverageClaimType().parallelStream().map(new CoverageClaimTransformer()).collect(Collectors.toList());
        ProductClaimMapper productClaimMapper = ProductClaimMapper.create(createProductClaimCommand.getPlanCode(), createProductClaimCommand.getLineOfBusiness());
        productClaimMapper =  productClaimMapper.withCoverageClaimMappers(coverageClaimMappers);
        try {
            coverageRepository.save(productClaimMapper);
        } catch (RuntimeException e) {
            LOGGER.error("*****Saving Product Claim mapping failed*****", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @CommandHandler
    public void updateProductClaimCommandHandler(UpdateProductClaimCommand updateProductClaimCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Command Received*****" + updateProductClaimCommand);
        }
        JpaRepository<ProductClaimMapper, Long> productClaimRepository = jpaRepositoryFactory.getCrudRepository(ProductClaimMapper.class);
        JpaRepository<CoverageClaimMapper, Long> coverageClaimRepository = jpaRepositoryFactory.getCrudRepository(CoverageClaimMapper.class);
        ProductClaimMapper productClaimMapper = productClaimRepository.findOne(updateProductClaimCommand.getProductClaimId());
        List<CoverageClaimMapper> coverageClaimMappers = productClaimMapper.getCoverageClaimMappers();
        for (CoverageClaimMapper coverageClaimMapper : coverageClaimMappers){
            CoverageClaimMapper deleteCoverageClaimMap = coverageClaimRepository.findOne(coverageClaimMapper.getCoverageClaimId());
            coverageClaimRepository.delete(deleteCoverageClaimMap);
        }
        List<CoverageClaimMapper> updateCoverageClaimMap = updateProductClaimCommand.getCoverageClaimType().parallelStream().map(new CoverageClaimTransformer()).collect(Collectors.toList());
        productClaimMapper = productClaimMapper.withCoverageClaimMappers(updateCoverageClaimMap);
        try {
            productClaimRepository.save(productClaimMapper);
        } catch (RuntimeException e) {
            LOGGER.error("*****Error in updating Product Claim Mapping*****", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    private class CoverageClaimTransformer implements Function<CoverageClaimTypeDto,CoverageClaimMapper> {
        @Override
        public CoverageClaimMapper apply(CoverageClaimTypeDto coverageClaimTypeDto) {
            return CoverageClaimMapper.create(new CoverageId(coverageClaimTypeDto.getCoverageId()),coverageClaimTypeDto.getClaimTypes());
        }
    }
}
