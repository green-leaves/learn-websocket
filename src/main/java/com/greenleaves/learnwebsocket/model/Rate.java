package com.greenleaves.learnwebsocket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;


@Builder
@Data
@AllArgsConstructor
@ToString
public class Rate {
    private String ccyPair;
    private BigDecimal bidRate;
    private BigDecimal askRate;
}
