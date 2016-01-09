package com.pla.grouphealth.claim.cashless.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Mohan Sharma on 1/9/2016.
 */
@Document(collection = "PRE_AUTHORIZATION_REQUEST")
@Getter
@ToString
@EqualsAndHashCode(callSuper = false, doNotUseGetters = true)
public class PreAuthorizationRequest extends AbstractAnnotatedAggregateRoot<PreAuthorizationRequestId> {

    @Id
    @AggregateIdentifier
    @JsonSerialize(using = ToStringSerializer.class)
    private PreAuthorizationRequestId preAuthorizationRequestId;

    @Override
    public PreAuthorizationRequestId getIdentifier() {
        return preAuthorizationRequestId;
    }
}
