package com.greenleaves.learnwebsocket.service;


import com.greenleaves.learnwebsocket.model.Rate;
import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.*;

@Service
public class SubscriptionService {

    // ccyPair
    public Map<String, Subject<Rate>> marketRates = new HashMap<>();

    public Map<String, Sinks.Many<Rate>> rateSinks = new HashMap<>();

    public static final List<String> hierarchy = Collections.unmodifiableList(Arrays.asList("EUR", "GBP", "USD", "JPY", "SGD", "HKD", "VND"));

    public Subject<Rate> subscribeRate(String ccyPair) {
        Subject<Rate> rateSubject = marketRates.get(ccyPair);

        if (rateSubject == null) {
            if (!ccyPair.contains("USD")) {
                Subject<Rate> rateSubject1 = ReplaySubject.create(1);
                Subject<Rate> rateSubject2 = ReplaySubject.create(1);

                marketRates.putIfAbsent("EURUSD", rateSubject1);
                marketRates.putIfAbsent("USDSGD", rateSubject2);

                Observable<Rate> rateObservable = Observable.combineLatest(rateSubject1, rateSubject2,
                        (r1, r2) -> crossRate(ccyPair, r1, r2)
                );

                rateSubject = ReplaySubject.create(1);
                rateObservable.subscribe(rateSubject);
                marketRates.putIfAbsent(ccyPair, rateSubject);

            } else {
                rateSubject = ReplaySubject.create(1);
                marketRates.putIfAbsent(ccyPair, rateSubject);
            }
        }

        return rateSubject;
    }

    private Rate crossRate(String ccyPair, Rate r1, Rate r2) {
        return Rate.builder()
                .ccyPair(ccyPair)
                .bidRate(r1.getBidRate().multiply(r2.getBidRate()))
                .askRate(r1.getAskRate().multiply(r2.getAskRate()))
                .build();
    }

    public Flux<Rate> subscribeFluxRate(String ccyPair) {
        Sinks.Many<Rate> rateSink = rateSinks.get(ccyPair);
        if (rateSink == null) {
            if (!ccyPair.contains("USD")) {
                Pair<String, String> breakDown = breakDown(ccyPair);
                Flux<Rate> fluxRate1 = subscribeFluxRate(breakDown.getLeft());
                Flux<Rate> fluxRate2 = subscribeFluxRate(breakDown.getRight());

                return Flux.combineLatest(fluxRate1, fluxRate2, (r1, r2) -> crossRate(ccyPair, r1, r2));
            } else {
                rateSink = Sinks.many().replay().latest();
                rateSinks.putIfAbsent(ccyPair, rateSink);
            }
        }

        return rateSink.asFlux();
    }

    public void onUpdate(Rate rate) {
        Subject<Rate> rateSubject = marketRates.get(rate.getCcyPair());
        Sinks.Many<Rate> rateSink = rateSinks.get(rate.getCcyPair());
        if (rateSubject != null) {
            rateSubject.onNext(rate);
        }

        if (rateSink != null) {
            rateSink.tryEmitNext(rate);
        }

    }

    private Pair<String, String> breakDown(String ccyPair) {
        String base = ccyPair.substring(0, 3);
        String term = ccyPair.substring(3, 6);
        return Pair.of(getUSDPair(base), getUSDPair(term));

    }

    private String getUSDPair(String ccy) {
        if (hierarchy.indexOf(ccy) <= hierarchy.indexOf("USD")) {
            return ccy.concat("USD");
        } else {
            return "USD".concat(ccy);
        }
    }
}
