package com.pla.client.event;

import com.pla.client.service.ClientApplicationService;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import com.pla.sharedkernel.event.GHProposerAddedEvent;
import com.pla.sharedkernel.event.GLProposerAddedEvent;
import com.pla.sharedkernel.event.ILProposerAddedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 6/22/2015.
 */
@Component
public class ClientEventListener {

    @Autowired
    private ClientApplicationService clientApplicationService;

    @EventHandler
    public void handle(GLProposerAddedEvent glProposerAddedEvent) {
        ClientDetailDto clientDetailDto = new ClientDetailDto(glProposerAddedEvent.getProposerCode(), glProposerAddedEvent.getProposerName(), glProposerAddedEvent.getAddressLine1(), glProposerAddedEvent.getAddressLine2(), glProposerAddedEvent.getPostalCode(), glProposerAddedEvent.getProvince(), glProposerAddedEvent.getTown(), glProposerAddedEvent.getEmailAddress());
        clientApplicationService.createClient(clientDetailDto);
    }

    @EventHandler
    public void handle(GHProposerAddedEvent ghProposerAddedEvent) {
        ClientDetailDto clientDetailDto = new ClientDetailDto(ghProposerAddedEvent.getProposerCode(), ghProposerAddedEvent.getProposerName(), ghProposerAddedEvent.getAddressLine1(), ghProposerAddedEvent.getAddressLine2(), ghProposerAddedEvent.getPostalCode(), ghProposerAddedEvent.getProvince(), ghProposerAddedEvent.getTown(), ghProposerAddedEvent.getEmailAddress());
        clientApplicationService.createClient(clientDetailDto);
    }


    @EventHandler
    public void handle(ILProposerAddedEvent ghProposerAddedEvent) {
        ClientDetailDto clientDetailDto = new ClientDetailDto(ghProposerAddedEvent.getProposerCode(), ghProposerAddedEvent.getProposerName(), ghProposerAddedEvent.getAddressLine1(), ghProposerAddedEvent.getAddressLine2(), ghProposerAddedEvent.getPostalCode(), ghProposerAddedEvent.getProvince(), ghProposerAddedEvent.getTown(), ghProposerAddedEvent.getEmailAddress());
        clientApplicationService.createClient(clientDetailDto);
    }
}
