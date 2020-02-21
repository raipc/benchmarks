package com.github.raipc.csv;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class FastNumberParser implements NumberParser {
    private final CsvNumberPreprocessor numberPreprocessor;

    public FastNumberParser(char decimalSep, char groupingSep) {
        this.numberPreprocessor = new CsvNumberPreprocessor(decimalSep, groupingSep, new String[0]);
    }

    @Override
    public BigDecimal parse(String value) {
        return new BigDecimal(numberPreprocessor.preProcess(value));
    }

    @Override
    public boolean canParse(String value) {
        return NumberUtils.isCreatable(numberPreprocessor.preProcess(value));
    }
}
