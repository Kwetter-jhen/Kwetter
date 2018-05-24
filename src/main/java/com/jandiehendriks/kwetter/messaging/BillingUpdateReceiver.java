package com.jandiehendriks.kwetter.messaging;

import com.jandiehendriks.kwetter.domain.KwetterException;
import com.jandiehendriks.kwetter.dto.UserDto;
import com.jandiehendriks.kwetter.service.KwetterUserService;
import com.jandiehendriks.kwetter.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

@RabbitListener(queues = "BILLING_USER_UPDATE")
public class BillingUpdateReceiver {
    private static final Logger log =
            LoggerFactory.getLogger(BillingUpdateReceiver.class);
    private JsonUtil jsonUtil = new JsonUtil();

    @Autowired
    private KwetterUserService kwetterUserService;

    @RabbitHandler
    public void receive(String message) {
        UserDto userDto = jsonUtil.decode(message, UserDto.class);

        try {
            kwetterUserService.updateBillingInfo(
                    userDto.getUsername(), userDto.getBillingId());
        } catch (KwetterException e) {
            log.error(e.getMessage());
        }
    }
}
