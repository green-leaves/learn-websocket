package com.greenleaves.learnwebsocket;

import com.greenleaves.learnwebsocket.faker.RateFaker;
import com.greenleaves.learnwebsocket.service.SubscriptionService;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;
import java.util.*;

@Slf4j
@SpringBootApplication
public class LearnWebsocketApplication implements CommandLineRunner {

    private List<Disposable> disposables = new ArrayList<>();

    private SubscriptionService subscriptionService;
    private RateFaker rateFaker;

    public LearnWebsocketApplication(SubscriptionService subscriptionService, RateFaker rateFaker) {
        this.subscriptionService = subscriptionService;
        this.rateFaker = rateFaker;
    }

    public static void main(String[] args) {
        SpringApplication.run(LearnWebsocketApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //subscriptionService.subscribeRate("EURSGD").subscribe(rate -> log.info("Subcribed: {}", rate));
        //subscriptionService.subscribeRate("EURUSD").subscribe(rate -> log.info("Subcribed: {}", rate));
        //subscriptionService.subscribeRate("USDSGD").subscribe(rate -> log.info("Subcribed: {}", rate));

        rateFaker.startPublishingRate();
    }

    @PreDestroy
    public void shutdown() {
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
    }
}
