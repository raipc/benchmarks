package com.github.raipc.csv;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;

public class FormatBasedNumberParser implements NumberParser {
    private final DecimalFormat formatter;

    public FormatBasedNumberParser(String format, char decimalSep, char groupingSep) {
        this.formatter = new DecimalFormat(format);
        this.formatter.setParseBigDecimal(true);
        final DecimalFormatSymbols newSymbols = new DecimalFormatSymbols();
        newSymbols.setDecimalSeparator(decimalSep);
        newSymbols.setGroupingSeparator(groupingSep);
        this.formatter.setDecimalFormatSymbols(newSymbols);
    }

    @Override
    public BigDecimal parse(String value) {
        value = StringUtils.replace(StringUtils.replace(value, "e", "E"), "E+", "E");
        return (BigDecimal) formatter.parse(value, new ParsePosition(0));
    }

    @Override
    public boolean canParse(String value) {
        return parse(value) != null;
    }
}