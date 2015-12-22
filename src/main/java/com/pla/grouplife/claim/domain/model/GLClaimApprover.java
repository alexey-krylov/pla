package com.pla.grouplife.claim.domain.model;

import org.joda.time.DateTime;

import java.util.Set;

/**
 * Created by ak
 */
public class GLClaimApprover {

        private String userName;

        public GLClaimApprover(String userName) {
            this.userName = userName;
        }

        public GroupLifeClaim submitApproval(DateTime approvalOn, String approvalComment, GroupLifeClaim groupLifeClaim, ClaimStatus status) {
            groupLifeClaim = groupLifeClaim.markApproverApproval(this.userName, approvalOn, approvalComment, status);
            return groupLifeClaim;
        }

        public GroupLifeClaim updateWithDocuments(GroupLifeClaim aggregate, Set<GLClaimDocument> documents) {
        return aggregate.withClaimDocuments(documents);
       }


    }

