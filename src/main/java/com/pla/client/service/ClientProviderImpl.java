package com.pla.client.service;

import com.pla.publishedlanguage.contract.IClientProvider;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import org.springframework.stereotype.Service;

/**
 * Created by Samir on 6/22/2015.
 */
@Service(value = "clientProvider")
public class ClientProviderImpl implements IClientProvider {
    @Override
    public ClientDetailDto getClientDetail(String clientCode) {
        return null;
    }
}
