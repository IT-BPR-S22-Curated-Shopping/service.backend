package bpr.service.backend.websocket.controllers;

import bpr.service.backend.websocket.models.IdentityDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class PresentationController {

    @MessageMapping("/presenterId")
    @SendTo("/presentation/uuid")
    public IdentityDTO sendUUID() {
        return new IdentityDTO("000001-000-000001-1337");
    }
}
