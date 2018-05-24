package com.jandiehendriks.kwetter.messaging;

import com.jandiehendriks.kwetter.domain.KwetterUser;
import com.jandiehendriks.kwetter.dto.UserDto;
import com.jandiehendriks.kwetter.util.JsonUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class RegistrationSender {

    @Autowired
    private RabbitTemplate template;

    private JsonUtil jsonUtil = new JsonUtil();

    public void send(KwetterUser kwetterUser) {
        UserDto userDto = new UserDto();
        userDto.setUsername(kwetterUser.getUsername());
        userDto.setEmail(kwetterUser.getEmail());

        this.template.convertAndSend(
                "NEW_REGISTRATION_QUEUE",
                jsonUtil.encode(userDto));
    }
}
