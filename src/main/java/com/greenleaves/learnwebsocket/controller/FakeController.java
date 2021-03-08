package com.greenleaves.learnwebsocket.controller;

import io.reactivex.Single;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fake")
public class FakeController {

    @Autowired
    private RSocketRequester requester;

    @GetMapping("/")
    public Mono<String> get() {
        return Mono.just("Fake");
    }

    @GetMapping(value = "/r", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getR() {
        return requester.route("rsocket").retrieveFlux(String.class);
    }
}
