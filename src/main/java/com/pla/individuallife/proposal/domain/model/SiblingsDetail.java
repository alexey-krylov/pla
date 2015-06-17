package com.pla.individuallife.proposal.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by ASUS on 12-Jun-15.
 */

@Getter
@Setter
@NoArgsConstructor
public class SiblingsDetail {

    private int alive;
    private int death;
    private int deathAge;
    private String healthState;
    private String deathCause;

    SiblingsDetail(int alive,int death,int deathAge,String healthState,String deathCause)
    {
        this.alive=alive;
        this.death=death;
        this.deathAge=deathAge;
        this.healthState=healthState;
        this.deathCause=deathCause;
    }
}
