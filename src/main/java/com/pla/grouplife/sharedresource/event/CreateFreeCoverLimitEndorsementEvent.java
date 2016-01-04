package com.pla.grouplife.sharedresource.event;

import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.sharedkernel.identifier.PolicyId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Admin on 28-Dec-15.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFreeCoverLimitEndorsementEvent {

  private PolicyId policyId;
  private Set<Insured> insureds;
  private Set<InsuredDependent> insuredDependents;
  private BigDecimal freeCoverLimit;

}
