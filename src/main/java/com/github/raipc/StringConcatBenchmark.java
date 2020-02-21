package com.github.raipc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import static com.github.raipc.StringConcatBenchmark.Query.FORMAT_PARAM_NAME;
import static com.github.raipc.StringConcatBenchmark.Query.FORMAT_PARAM_VALUE;
import static com.github.raipc.StringConcatBenchmark.Query.LIMIT_PARAM_NAME;
import static com.github.raipc.StringConcatBenchmark.Query.METADATA_FORMAT_PARAM_NAME;
import static com.github.raipc.StringConcatBenchmark.Query.QUERY_ID_PARAM_NAME;
import static com.github.raipc.StringConcatBenchmark.Query.Q_PARAM_NAME;

@State(Scope.Thread)
public class StringConcatBenchmark {
    private Query query;
    
    @Setup
    public void prepare() {
        query = new Query("12345678", "SELECT * FROM jvm_memory_used WHERE time > now - 2 * MONTH", "EMBED", 1000L);
    }

    @Benchmark
    public String concatWithNonFinalString() throws UnsupportedEncodingException {
        return QUERY_ID_PARAM_NAME + '=' + query.queryId + '&' +
                Q_PARAM_NAME + '=' + URLEncoder.encode(query.query, "UTF-8") + '&' +
                FORMAT_PARAM_NAME + '=' + FORMAT_PARAM_VALUE + '&' +
                METADATA_FORMAT_PARAM_NAME + '=' + query.metadataFormat + '&' +
                LIMIT_PARAM_NAME + '=' + query. maxRowsCount;
    }

    @Benchmark
    public String concatWithSb() throws UnsupportedEncodingException {
        final String encodedQuery = URLEncoder.encode(query.query, "UTF-8");
        return new StringBuilder().append(QUERY_ID_PARAM_NAME).append('=').append(query.queryId).append('&').append(Q_PARAM_NAME).append('=').append(encodedQuery).append('&').append(FORMAT_PARAM_NAME).append('=').append(FORMAT_PARAM_VALUE).append('&').append(METADATA_FORMAT_PARAM_NAME).append('=').append(query.metadataFormat).append('&').append(LIMIT_PARAM_NAME).append('=').append(query.maxRowsCount).toString();
    }
    
    @Benchmark
    public String concatWithChars() throws UnsupportedEncodingException {
        final String encodedQuery = URLEncoder.encode(query.query, "UTF-8");
        return QUERY_ID_PARAM_NAME + '=' + query.queryId + '&' +
                Q_PARAM_NAME + '=' + encodedQuery + '&' +
                FORMAT_PARAM_NAME + '=' + FORMAT_PARAM_VALUE + '&' +
                METADATA_FORMAT_PARAM_NAME + '=' + query.metadataFormat + '&' +
                LIMIT_PARAM_NAME + '=' + query. maxRowsCount;
    }

    @Benchmark
    public String concatWoChars() throws UnsupportedEncodingException { 
        final String encodedQuery = URLEncoder.encode(query.query, "UTF-8");
        return QUERY_ID_PARAM_NAME + "=" + query.queryId + "&" +
                Q_PARAM_NAME + "=" + encodedQuery + "&" +
                FORMAT_PARAM_NAME + "=" + FORMAT_PARAM_VALUE + "&" +
                METADATA_FORMAT_PARAM_NAME + "=" + query.metadataFormat + "&" +
                LIMIT_PARAM_NAME + "=" + query.maxRowsCount;
    }

    @Benchmark
    public String concatOnlyStrings() throws UnsupportedEncodingException {
        final String encodedQuery = URLEncoder.encode(query.query, "UTF-8");
        final String maxRowsCountStr = "" + query.maxRowsCount;
        return QUERY_ID_PARAM_NAME + "=" + query.queryId + "&" +
                Q_PARAM_NAME + "=" + encodedQuery + "&" +
                FORMAT_PARAM_NAME + "=" + FORMAT_PARAM_VALUE + "&" +
                METADATA_FORMAT_PARAM_NAME + "=" + query.metadataFormat + "&" +
                LIMIT_PARAM_NAME + "=" + maxRowsCountStr;
    }

    @Benchmark
    public String concatWithoutConstants() throws UnsupportedEncodingException {
        final String encodedQuery = URLEncoder.encode(query.query, "UTF-8");
        return "queryId" + '=' + query.queryId + '&' +
                "q" + '=' + encodedQuery + '&' +
                "format" + '=' + FORMAT_PARAM_VALUE + '&' +
                "metadataFormat" + '=' + query.metadataFormat + '&' +
                "limit" + '=' + query.maxRowsCount;
    }
    
    
    static class Query {
        static final String QUERY_ID_PARAM_NAME = "queryId";
        static final String Q_PARAM_NAME = "q";
        static final String FORMAT_PARAM_NAME = "format";
        static final String FORMAT_PARAM_VALUE = "csv";
        static final String METADATA_FORMAT_PARAM_NAME = "metadataFormat";
        static final String LIMIT_PARAM_NAME = "limit";
        
        private String queryId;
        private String query;
        private String metadataFormat;
        private long maxRowsCount;

        Query(String queryId, String query, String metadataFormat, long maxRowsCount) {
            this.queryId = queryId;
            this.query = query;
            this.metadataFormat = metadataFormat;
            this.maxRowsCount = maxRowsCount;
        }
    }
}
