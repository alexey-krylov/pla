package com.pla.individuallife.proposal.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Prasant on 12-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class ParentDetail {
    private boolean isAlive;
    private  String healthState;
    private int deathAge;
    private  String deathCause;

    ParentDetail(boolean status,String healthState,int deathAge,String deathCause)
    {
        this.isAlive=status;
        this.healthState=healthState;
        this.deathAge=deathAge;
        this.deathCause=deathCause;
    }
}
