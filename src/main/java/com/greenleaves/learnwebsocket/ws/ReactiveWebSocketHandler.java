package com.greenleaves.learnwebsocket.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenleaves.learnwebsocket.model.Rate;
import com.greenleaves.learnwebsocket.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {


    private final ObjectMapper json = new ObjectMapper();

    private SubscriptionService subscriptionService;

    private Map<String, Map<String, Flux<Rate>>> sessionMap = new HashMap<>();

    public ReactiveWebSocketHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession
                .send(webSocketSession.receive().map(payload -> {
                    String[] payloadArr = payload.getPayloadAsText().split("\\|");
                    String ccyPair = payloadArr[1];
                    if (payloadArr[0].equals("S")) {
                        Flux<Rate> rateFlux = subscriptionService.subscribeFluxRate(ccyPair);
                        sessionMap.computeIfAbsent(webSocketSession.getId(), (map) -> new HashMap<>()).putIfAbsent(ccyPair, rateFlux);
                    } else if (payloadArr[0].equals("U")) {
                        sessionMap.get(webSocketSession.getId()).get(ccyPair).subscribe().dispose();
                    }
                    return Flux.merge(sessionMap.get(webSocketSession.getId()).values());
                }).flatMap(flux -> flux.map(rate -> webSocketSession.textMessage(toJson(rate)))));
//        return webSocketSession
//                .send(webSocketSession.receive().map(payload -> {
//                    String ccyPair = payload.getPayloadAsText();
//                    Flux<Rate> rateFlux = subscriptionService.subscribeFluxRate(ccyPair);
//                    return rateFlux.map(this::toJson);
//                }).flatMap(rate -> rate.map(webSocketSession::textMessage)));
    }

    public String toJson(Rate rate) {
        try {
            return json.writeValueAsString(rate);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

}