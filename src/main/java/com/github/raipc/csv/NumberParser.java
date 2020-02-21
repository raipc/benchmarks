package com.github.raipc.csv;

import java.math.BigDecimal;

public interface NumberParser {
    BigDecimal parse(String value);

    boolean canParse(String value);
}
