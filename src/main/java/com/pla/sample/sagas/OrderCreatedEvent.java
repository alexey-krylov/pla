package com.pla.sample.sagas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
* Author: Nthdimenzion
*/
@ToString
@AllArgsConstructor
@Getter
public class OrderCreatedEvent {
    public final String orderId;
}
