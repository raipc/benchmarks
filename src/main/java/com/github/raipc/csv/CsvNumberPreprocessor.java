package com.github.raipc.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public final class CsvNumberPreprocessor {
    @Getter
    private final char decimalSeparator;
    @Getter
    private final char groupingSeparator;

//    @NotNull
    private final String[] searchSymbols;
    private final String[] replacement;

    CsvNumberPreprocessor(char decimalSeparator, char groupSeparator, String[] nanFilter) {
        this.groupingSeparator = groupSeparator;
        this.decimalSeparator = decimalSeparator;
        final List<String> nanFilterList = new ArrayList<>();
        final List<String> replacementList = new ArrayList<>();
        if (nanFilter != null) {
            Collections.addAll(nanFilterList, nanFilter);
            for (int i = 0; i < nanFilter.length; i++) {
                replacementList.add(StringUtils.EMPTY);
            }
        }
        if (decimalSeparator != '.') {
            nanFilterList.add("" + decimalSeparator);
            replacementList.add(".");
        }
        if (groupSeparator != '\0') {
            nanFilterList.add("" + groupSeparator);
            replacementList.add(StringUtils.EMPTY);
        }
        this.searchSymbols = nanFilterList.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        this.replacement = replacementList.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public String preProcess(String value) {
        return StringUtils.replaceEach(value, searchSymbols, replacement);
    }

    public String normalizeInteger(String value) {
        return groupingSeparator == '\0' ? value : StringUtils.remove(value, groupingSeparator);
    }
}