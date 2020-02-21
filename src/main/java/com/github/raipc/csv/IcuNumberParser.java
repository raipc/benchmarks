package com.github.raipc.csv;

import java.math.BigDecimal;

import com.ibm.icu.impl.number.DecimalFormatProperties;
import com.ibm.icu.impl.number.PatternStringParser;
import com.ibm.icu.impl.number.parse.NumberParserImpl;
import com.ibm.icu.impl.number.parse.ParsedNumber;
import com.ibm.icu.text.DecimalFormatSymbols;
import lombok.RequiredArgsConstructor;

import static com.ibm.icu.impl.number.parse.ParsingUtils.PARSE_FLAG_FORCE_BIG_DECIMAL;

@RequiredArgsConstructor
public class IcuNumberParser implements NumberParser {
    private final NumberParserImpl numberParserImpl;

    public IcuNumberParser(String format, char decimalSep, char groupingSep) {
        final DecimalFormatSymbols newSymbols = new DecimalFormatSymbols();
        newSymbols.setDecimalSeparator(decimalSep);
        newSymbols.setGroupingSeparator(groupingSep);
        final DecimalFormatProperties properties = PatternStringParser.parseToProperties(format);
        properties.setParseToBigDecimal(true);
        numberParserImpl = NumberParserImpl.createParserFromProperties(properties, newSymbols, false);
    }

    @Override
    public BigDecimal parse(String value) {
        final ParsedNumber result = new ParsedNumber();
        numberParserImpl.parse(value, false, result);
        if (result.success()) {
            return (BigDecimal)result.getNumber(PARSE_FLAG_FORCE_BIG_DECIMAL);
        }
        return null;
    }

    @Override
    public boolean canParse(String value) {
        return parse(value) != null;
    }
}
