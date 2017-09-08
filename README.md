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

| Benchmark                                                      | (predicate)| Mode |Cnt |   Score        |   Error         | Units |
|----------------------------------------------------------------|------------|------|----|----------------|-----------------|-------|
| ArrayListRemoveBenchmark.listIteratorReverse                        | FIRST | avgt |  9 |     123393,219 | ±     10253,112 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse                       | MIDDLE | avgt |  9 |     127665,305 | ±      4859,684 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse                        | TENTH | avgt |  9 |   68180431,334 | ±   6267452,917 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse                          | ALL | avgt |  9 |     298373,704 | ±     14468,914 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse                         | NONE | avgt |  9 |      96292,747 | ±     12220,716 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse                         | HALF | avgt |  9 |  233234647,750 | ± 140080298,330 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse               | THIRTY_PERCENT | avgt |  9 |  151481915,823 | ±  11605417,835 | ns/op |
| ArrayListRemoveBenchmark.listIteratorReverse              | SEVENTY_PERCENT | avgt |  9 |  144748725,893 | ±   7275535,132 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator                              | FIRST | avgt |  9 |     205643,490 | ±     66427,630 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator                             | MIDDLE | avgt |  9 |     145738,642 | ±      7987,619 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator                              | TENTH | avgt |  9 |   78284527,614 | ±   7976874,606 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator                                | ALL | avgt |  9 |  795196303,111 | ±  72430965,609 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator                               | NONE | avgt |  9 |     109528,871 | ±     16415,551 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator                               | HALF | avgt |  9 |  389336660,519 | ±  35297553,753 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator                     | THIRTY_PERCENT | avgt |  9 |  241248075,117 | ±  22290316,144 | ns/op |
| ArrayListRemoveBenchmark.naiveIterator                    | SEVENTY_PERCENT | avgt |  9 |  559293329,611 | ±  46318943,193 | ns/op |
| ArrayListRemoveBenchmark.removeIf                                   | FIRST | avgt |  9 |     456017,943 | ±     59352,133 | ns/op |
| ArrayListRemoveBenchmark.removeIf                                  | MIDDLE | avgt |  9 |     661989,140 | ±     39036,207 | ns/op |
| ArrayListRemoveBenchmark.removeIf                                   | TENTH | avgt |  9 |     649231,827 | ±     43315,733 | ns/op |
| ArrayListRemoveBenchmark.removeIf                                     | ALL | avgt |  9 |     295054,611 | ±     66741,071 | ns/op |
| ArrayListRemoveBenchmark.removeIf                                    | NONE | avgt |  9 |      95331,104 | ±      9911,999 | ns/op |
| ArrayListRemoveBenchmark.removeIf                                    | HALF | avgt |  9 |     527825,645 | ±     91734,440 | ns/op |
| ArrayListRemoveBenchmark.removeIf                          | THIRTY_PERCENT | avgt |  9 |     943399,479 | ±     81679,759 | ns/op |
| ArrayListRemoveBenchmark.removeIf                         | SEVENTY_PERCENT | avgt |  9 |     534893,911 | ±     38488,844 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava                            | FIRST | avgt |  9 |     500164,950 | ±     47804,792 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava                           | MIDDLE | avgt |  9 |     653966,658 | ±    120305,892 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava                            | TENTH | avgt |  9 |     706673,318 | ±     52828,623 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava                              | ALL | avgt |  9 |     330734,107 | ±     71679,718 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava                             | NONE | avgt |  9 |     136392,639 | ±     72503,264 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava                             | HALF | avgt |  9 |     971662,364 | ±     59935,976 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava                   | THIRTY_PERCENT | avgt |  9 |    1017839,771 | ±     26515,274 | ns/op |
| ArrayListRemoveBenchmark.removeWithGuava                  | SEVENTY_PERCENT | avgt |  9 |     460608,771 | ±     13416,554 | ns/op |
