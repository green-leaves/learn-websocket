package com.greenleaves.learnwebsocket.controller;

import com.greenleaves.learnwebsocket.model.Rate;
import com.greenleaves.learnwebsocket.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class RSocketController {

    @Autowired
    private SubscriptionService subscriptionService;

    @MessageMapping("rsocket")
    public Flux<String> get() {
        return subscriptionService.subscribeFluxRate("USDJPY").map(Rate::toString);
    }
}
