package com.pla.grouplife.sharedresource.event;

import com.pla.sharedkernel.identifier.PolicyId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by Admin on 11-Dec-15.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GLPolicyInsuredDeleteEvent {

    private PolicyId policyId;
    private List<String> deletedFamilyIds;
}
