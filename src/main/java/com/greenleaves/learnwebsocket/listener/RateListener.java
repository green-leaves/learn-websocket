package com.greenleaves.learnwebsocket.listener;

import com.greenleaves.learnwebsocket.model.Rate;
import com.greenleaves.learnwebsocket.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateListener {

    private Map<String, Rate> rateStore = new ConcurrentHashMap<>();

    private SubscriptionService subscriptionService;

    public RateListener(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void onRateUpdate(Rate rate) {
        rateStore.putIfAbsent(rate.getCcyPair(), rate);

        subscriptionService.onUpdate(rate);
    }
}
