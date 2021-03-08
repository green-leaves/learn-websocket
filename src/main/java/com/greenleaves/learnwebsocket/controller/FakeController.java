package com.greenleaves.learnwebsocket.controller;

import io.reactivex.Single;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fake")
public class FakeController {


    @GetMapping("/")
    public Mono<String> get() {
        return Mono.just("Fake");
    }
}
