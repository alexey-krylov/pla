package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.identifier.QuotationId;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by pradyumna on 13-06-2015.
 */
@Entity(name = "individual_quotation_ar")
public class ILQuotationAR extends AbstractAnnotatedAggregateRoot<String> {

    @AggregateIdentifier
    @Id
    @Column(name = "quotation_ar_id")
    private String quotationARId;

    private int versionNumber;

    @OneToMany(mappedBy = "parentQuotationId")
    @Cascade(CascadeType.ALL)
    private Collection<ILQuotation> quotationList = new ArrayList();


    public ILQuotationAR() {
    }

    public ILQuotationAR(String quotationId, ILQuotation quotation) {
        this.quotationARId = quotationId;
        this.versionNumber = 0;
        quotationList.add(quotation);
    }

    public void versionQuotation(ILQuotation quotation) {
        quotationList.add(quotation);
    }

    public ILQuotation nextVersion(ILQuotationProcessor quotationCreator,
                                   ILQuotation source, QuotationId quotationId,
                                   String quotationNumber) {
        this.versionNumber++;
        checkArgument(isNotEmpty(quotationNumber));
        checkArgument(quotationCreator != null, "User is does not have Quotation Preprocessor Role.");
        ILQuotation newQuotation = source.cloneQuotation(quotationCreator, quotationId,
                quotationNumber, versionNumber);
        versionQuotation(newQuotation);
        return newQuotation;
    }

}
