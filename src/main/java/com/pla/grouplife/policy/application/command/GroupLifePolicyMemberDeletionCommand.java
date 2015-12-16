package com.pla.grouplife.policy.application.command;

import com.pla.sharedkernel.identifier.PolicyId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Admin on 11-Dec-15.
 */
@Getter
@Setter
@AllArgsConstructor
public class GroupLifePolicyMemberDeletionCommand {
    private PolicyId policyId;
    private List<String> deletedFamilyIds;
}
