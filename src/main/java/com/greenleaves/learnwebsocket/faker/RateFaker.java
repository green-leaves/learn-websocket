package com.greenleaves.learnwebsocket.faker;

import com.greenleaves.learnwebsocket.listener.RateListener;
import com.greenleaves.learnwebsocket.model.Rate;
import com.greenleaves.learnwebsocket.utils.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class RateFaker {

    private final RateListener rateListener;

    ExecutorService executor = Executors.newFixedThreadPool(10);

    public RateFaker(RateListener rateListener) {
        this.rateListener = rateListener;
    }

    public void startPublishingRate() throws InterruptedException {
        List<Callable<Void>> runnableList = new ArrayList<>();
        runnableList.add(fakeRateUpdate("USDSGD",1.33, 1.40, 10000));
        runnableList.add(fakeRateUpdate("USDJPY",108.72, 108.73, 1000));
        runnableList.add(fakeRateUpdate("EURUSD",1.19, 1.21, 2000));
        executor.invokeAll(runnableList);
    }

    private Callable<Void> fakeRateUpdate(String ccyPair, double left, double right, long interval) {
        return () -> {
            while (true) {
                try {
                    Pair<BigDecimal, BigDecimal> rate = RandomUtils.randomRate(left, right);
                    rateListener.onRateUpdate(new Rate(ccyPair, rate.getLeft(), rate.getRight()));
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
