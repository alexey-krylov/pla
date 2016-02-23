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

        public GroupLifeClaim submitApproval(DateTime approvalOn, String approvalComment, GroupLifeClaim groupLifeClaim) {
            groupLifeClaim = groupLifeClaim.markApproverApproval(approvalOn,this.userName,  approvalComment);
            return groupLifeClaim;
        }

        public GroupLifeClaim updateWithDocuments(GroupLifeClaim aggregate, Set<GLClaimDocument> documents) {
        return aggregate.withClaimDocuments(documents);
       }

        public GroupLifeClaim returnClaimRecord(DateTime approvalOn, String approvalComment, GroupLifeClaim groupLifeClaim) {
            groupLifeClaim = groupLifeClaim.markAsReturnedFromApprover(approvalOn, this.userName, approvalComment);
            return groupLifeClaim;
        }

         public GroupLifeClaim rejectClaimRecord(DateTime approvalOn, String approvalComment, GroupLifeClaim groupLifeClaim) {
            groupLifeClaim = groupLifeClaim.markAsRejectedByApprover(approvalOn, this.userName, approvalComment);
            return groupLifeClaim;
        }
    public GroupLifeClaim routeClaimRecordToSeniorApprover(DateTime approvalOn, String approvalComment, GroupLifeClaim groupLifeClaim) {
        groupLifeClaim = groupLifeClaim.markAsRoutedToSeniorApprover(approvalOn, this.userName, approvalComment);
        return groupLifeClaim;
    }



    }

