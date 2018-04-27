__A grave of the benchmarks.__

1) SplitBenchmark

| Benchmark                               |  (fileName) | Mode | Cnt | Score |  Error | Units |
|-----------------------------------------|-------------|------|-----|-------|--------|-------|
| SplitBenchmark.splitWithApacheCommons                 | commands-100.txt | avgt | 60 |  38,971 | ± 2,587 | us/op |
| SplitBenchmark.splitWithApacheCommonsChar             | commands-100.txt | avgt | 60 |   9,591 | ± 0,988 | us/op |
| SplitBenchmark.splitWithApacheCommonsSingleCharString | commands-100.txt | avgt | 60 |   9,166 | ± 0,110 | us/op |
| SplitBenchmark.splitWithJdkSplitRegex                 | commands-100.txt | avgt | 60 | 105,281 | ± 7,684 | us/op |
| SplitBenchmark.splitWithJdkSplitSymbol                | commands-100.txt | avgt | 60 |   7,827 | ± 0,106 | us/op |

2) ArrayListRemoveBenchmark

| Benchmark                                    |     (predicate)  | Mode | Cnt |         Score |          Error | Units |
|----------------------------------------------|------------------|------|-----|---------------|----------------|-------|
| ArrayListRemoveBenchmark.addToNewArray       |           FIRST  | avgt |  60 |    700684,157 | ±    10225,250 | ns/op |
| ArrayListRemoveBenchmark.addToNewArray       |          MIDDLE  | avgt |  60 |    704816,615 | ±    13503,878 | ns/op |
| ArrayListRemoveBenchmark.addToNewArray       |           TENTH  | avgt |  60 |    768920,931 | ±    13925,465 | ns/op |
| ArrayListRemoveBenchmark.addToNewArray       |             ALL  | avgt |  60 |    100327,359 | ±     2501,374 | ns/op |
| ArrayListRemoveBenchmark.addToNewArray       |            NONE  | avgt |  60 |    723835,901 | ±    40071,251 | ns/op |
| ArrayListRemoveBenchmark.addToNewArray       |            HALF  | avgt |  60 |    588069,134 | ±    12826,197 | ns/op |
| ArrayListRemoveBenchmark.addToNewArray       |  THIRTY_PERCENT  | avgt |  60 |    670828,402 | ±    12954,104 | ns/op |
| ArrayListRemoveBenchmark.addToNewArray       | SEVENTY_PERCENT  | avgt |  60 |    488185,970 | ±     9025,812 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse |           FIRST  | avgt |  60 |    124108,376 | ±     3433,402 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse |          MIDDLE  | avgt |  60 |    128809,452 | ±    19726,430 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse |           TENTH  | avgt |  60 |  80570807,510 | ±   994850,310 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse |             ALL  | avgt |  60 |    501654,732 | ±    18842,438 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse |            NONE  | avgt |  60 |    312419,452 | ±    10632,864 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse |            HALF  | avgt |  60 | 183871554,147 | ±  1340969,239 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse |  THIRTY_PERCENT  | avgt |  60 | 161018468,083 | ±  1389948,872 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse | SEVENTY_PERCENT  | avgt |  60 | 150831595,176 | ±  1283203,894 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator       |           FIRST  | avgt |  60 |    402929,387 | ±    15881,734 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator       |          MIDDLE  | avgt |  60 |    388957,469 | ±    16447,551 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator       |           TENTH  | avgt |  60 |  93639576,706 | ±   941375,567 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator       |             ALL  | avgt |  60 | 926905541,717 | ±  8021295,207 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator       |            NONE  | avgt |  60 |    342826,608 | ±    12099,603 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator       |            HALF  | avgt |  60 | 451611121,942 | ± 13878857,607 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator       |  THIRTY_PERCENT  | avgt |  60 | 261118276,989 | ±  9524141,372 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator       | SEVENTY_PERCENT  | avgt |  60 | 590174225,167 | ± 15630979,550 | ns/op |
| ArrayListRemoveBenchmark.removeIf            |           FIRST  | avgt |  60 |    477346,426 | ±    10824,131 | ns/op |
| ArrayListRemoveBenchmark.removeIf            |          MIDDLE  | avgt |  60 |    684946,897 | ±    21742,447 | ns/op |
| ArrayListRemoveBenchmark.removeIf            |           TENTH  | avgt |  60 |    725700,269 | ±    28433,472 | ns/op |
| ArrayListRemoveBenchmark.removeIf            |             ALL  | avgt |  60 |    313652,282 | ±    17767,394 | ns/op |
| ArrayListRemoveBenchmark.removeIf            |            NONE  | avgt |  60 |    121859,804 | ±    14927,641 | ns/op |
| ArrayListRemoveBenchmark.removeIf            |            HALF  | avgt |  60 |    672402,158 | ±    97785,856 | ns/op |
| ArrayListRemoveBenchmark.removeIf            |  THIRTY_PERCENT  | avgt |  60 |    966575,065 | ±    24445,133 | ns/op |
| ArrayListRemoveBenchmark.removeIf            | SEVENTY_PERCENT  | avgt |  60 |    559628,796 | ±    23601,927 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava     |           FIRST  | avgt |  60 |    501654,488 | ±    26534,735 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava     |          MIDDLE  | avgt |  60 |    706631,149 | ±    37187,005 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava     |           TENTH  | avgt |  60 |    750322,465 | ±    30353,259 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava     |             ALL  | avgt |  60 |    329946,675 | ±    17937,418 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava     |            NONE  | avgt |  60 |    110750,785 | ±    15539,217 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava     |            HALF  | avgt |  60 |    829251,835 | ±   104789,940 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava     |  THIRTY_PERCENT  | avgt |  60 |   1007558,783 | ±    30884,682 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava     | SEVENTY_PERCENT  | avgt |  60 |    824254,267 | ±    93943,991 | ns/op |
| ArrayListRemoveBenchmark.smartAddToNewArray  |           FIRST  | avgt |  60 |    721356,804 | ±    24297,247 | ns/op |
| ArrayListRemoveBenchmark.smartAddToNewArray  |          MIDDLE  | avgt |  60 |    725861,133 | ±    36381,470 | ns/op |
| ArrayListRemoveBenchmark.smartAddToNewArray  |           TENTH  | avgt |  60 |    784675,695 | ±    29292,992 | ns/op |
| ArrayListRemoveBenchmark.smartAddToNewArray  |             ALL  | avgt |  60 |    249496,742 | ±    15931,425 | ns/op |
| ArrayListRemoveBenchmark.smartAddToNewArray  |            NONE  | avgt |  60 |    127153,172 | ±    16903,700 | ns/op |
| ArrayListRemoveBenchmark.smartAddToNewArray  |            HALF  | avgt |  60 |    561347,027 | ±    15995,438 | ns/op |
| ArrayListRemoveBenchmark.smartAddToNewArray  |  THIRTY_PERCENT  | avgt |  60 |    759577,398 | ±    35262,122 | ns/op |
| ArrayListRemoveBenchmark.smartAddToNewArray  | SEVENTY_PERCENT  | avgt |  60 |    562246,956 | ±    30446,303 | ns/op |



Benchmark                                        Mode  Cnt        Score        Error  Units
StringConcatBenchmark.concatOnlyStrings         thrpt   60  1341301,665 ± 104355,562  ops/s
StringConcatBenchmark.concatWithChars           thrpt   60  1125947,334 ±  75695,534  ops/s
StringConcatBenchmark.concatWithNonFinalString  thrpt   60  1172512,849 ±  41598,409  ops/s
StringConcatBenchmark.concatWithSb              thrpt   60  1149982,455 ±  55737,219  ops/s
StringConcatBenchmark.concatWithoutConstants    thrpt   60  1180320,262 ±  50428,752  ops/s
StringConcatBenchmark.concatWoChars             thrpt   60  1120209,761 ±  45089,278  ops/s
