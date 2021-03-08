package com.greenleaves.learnwebsocket.utils;


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RandomUtils extends RandomStringUtils {

    public static Pair<BigDecimal, BigDecimal> randomRate(double left, double right) {
        double bid = new RandomDataGenerator().nextUniform(left, right);
        double ask = new RandomDataGenerator().nextUniform(bid, right);

        return Pair.of(
                BigDecimal.valueOf(bid).setScale(4, RoundingMode.HALF_UP),
                BigDecimal.valueOf(ask).setScale(4, RoundingMode.HALF_UP));
    }
}
