package com.pla.publishedlanguage.contract;

import com.pla.publishedlanguage.dto.ClientDetailDto;

/**
 * Created by Samir on 6/22/2015.
 */
public interface IClientProvider {

    ClientDetailDto getClientDetail(String clientCode);

}
