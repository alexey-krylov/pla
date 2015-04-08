package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.DiscountFactorItem;
import com.pla.sharedkernel.domain.model.ModalFactorItem;
import com.pla.sharedkernel.domain.model.Tax;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 4/1/2015.
 */

@Document(collection = "organization_information")
@Getter
@Setter(value = AccessLevel.PACKAGE)
@ToString(exclude = {"logger"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class OrganizationGeneralInformation {

    @Id
    private String organizationInformationId;

    private ServiceTax serviceTax;

    private Set<ModelFactorOrganizationInformation> modelFactorItems;

    private Set<DiscountFactorOrganizationInformation> discountFactorItems;


    OrganizationGeneralInformation(String organizationInformationId) {
        this.organizationInformationId = organizationInformationId;
    }

    public static OrganizationGeneralInformation createOrganizationGeneralInformation(String organizationInformationId) {
        return new OrganizationGeneralInformation(organizationInformationId);
    }

    public OrganizationGeneralInformation withServiceTaxOrganizationInformation(Map<Tax, BigDecimal> serviceTaxMap) {
        checkArgument(isNotEmpty(serviceTaxMap));
        Map.Entry<Tax, BigDecimal> serviceTaxItem = serviceTaxMap.entrySet().iterator().next();
        this.serviceTax = new ServiceTax(serviceTaxItem.getKey(), serviceTaxItem.getValue());
        return this;
    }

    public OrganizationGeneralInformation withModalFactorOrganizationInformation(List<Map<ModalFactorItem, BigDecimal>> listOfModalFactorItem) {
        checkArgument(isNotEmpty(listOfModalFactorItem));
        modelFactorItems = listOfModalFactorItem.stream().map(new ModalFactorInformationTransformer()).collect(Collectors.toSet());
        return this;
    }

    public OrganizationGeneralInformation withDiscountFactorOrganizationInformation(List<Map<DiscountFactorItem, BigDecimal>> listOfDiscountFactorItem) {
        checkArgument(isNotEmpty(listOfDiscountFactorItem));
        this.discountFactorItems = listOfDiscountFactorItem.stream().map(new DiscountFactorInformationTransformer()).collect(Collectors.toSet());
        return this;
    }

    private static class ModalFactorInformationTransformer implements Function<Map<ModalFactorItem, BigDecimal>, ModelFactorOrganizationInformation> {
        @Override
        public ModelFactorOrganizationInformation apply(Map<ModalFactorItem, BigDecimal> modalFactorItemMap) {
            Map.Entry<ModalFactorItem, BigDecimal> modelFactorItem = modalFactorItemMap.entrySet().iterator().next();
            ModelFactorOrganizationInformation modelFactorOrganizationInformation = new ModelFactorOrganizationInformation(modelFactorItem.getKey(), modelFactorItem.getValue());
            return modelFactorOrganizationInformation;
        }
    }

    private class DiscountFactorInformationTransformer implements Function<Map<DiscountFactorItem, BigDecimal>, DiscountFactorOrganizationInformation> {
        @Override
        public DiscountFactorOrganizationInformation apply(Map<DiscountFactorItem, BigDecimal> discountFactorItemMap) {
            Map.Entry<DiscountFactorItem, BigDecimal> discountFactorItem = discountFactorItemMap.entrySet().iterator().next();
            DiscountFactorOrganizationInformation discountFactorOrganizationInformation = new DiscountFactorOrganizationInformation(discountFactorItem.getKey(), discountFactorItem.getValue());
            return discountFactorOrganizationInformation;
        }
    }
}
