/* Copyright Homeaway, Inc 2005-2007. All Rights Reserved.
 * No unauthorized use of this software.
 */
package org.perf4j.chart;

import junit.framework.TestCase;
import org.perf4j.GroupedTimingStatistics;
import org.perf4j.StopWatch;
import org.perf4j.helpers.StatsValueRetriever;

import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Tests the GoogleChartGenerator
 */
public class GoogleChartGeneratorTest extends TestCase {
    public static final long START_TIME = 1229903820000L;

    private ResourceBundle expectedChartUrls;

    protected void setUp() throws Exception {
        expectedChartUrls = ResourceBundle.getBundle("org/perf4j/chart/googleChartTestExpectedValues");
    }

    public void testNoData() throws Exception {
        GoogleChartGenerator chart = new GoogleChartGenerator();

        verifyUrl(chart.getChartUrl(), "noData");
    }

    public void testThreeDataPoints() throws Exception {
        GoogleChartGenerator chart = new GoogleChartGenerator();

        StopWatch stopWatch = new StopWatch(START_TIME + 2000L, 2000L, "tag", null);
        GroupedTimingStatistics statistics =
                new GroupedTimingStatistics(Arrays.asList(stopWatch), START_TIME, START_TIME + 30000L, false);
        chart.appendData(statistics);

        stopWatch = new StopWatch(START_TIME + 32000L, 3000L, "tag", null);
        statistics =
                new GroupedTimingStatistics(Arrays.asList(stopWatch), START_TIME + 30000L, START_TIME + 60000L, false);
        chart.appendData(statistics);

        stopWatch = new StopWatch(START_TIME + 62000L, 1500L, "tag", null);
        statistics =
                new GroupedTimingStatistics(Arrays.asList(stopWatch), START_TIME + 60000L, START_TIME + 90000L, false);
        chart.appendData(statistics);

        verifyUrl(chart.getChartUrl(), "threeDataPoints");
    }

    public void testTwoSeriesThreeDataPoints() throws Exception {
        GoogleChartGenerator chart = new GoogleChartGenerator();
        GoogleChartGenerator tpsChart = new GoogleChartGenerator(StatsValueRetriever.TPS_VALUE_RETRIEVER);

        StopWatch watch1 = new StopWatch(START_TIME + 2000L, 2000L, "tag1", null);
        StopWatch watch2 = new StopWatch(START_TIME + 2000L, 1000L, "tag2", null);
        GroupedTimingStatistics statistics =
                new GroupedTimingStatistics(Arrays.asList(watch1, watch2), START_TIME, START_TIME + 30000L, false);
        chart.appendData(statistics);
        tpsChart.appendData(statistics);

        watch1 = new StopWatch(START_TIME + 32000L, 3000L, "tag1", null);
        watch2 = new StopWatch(START_TIME + 32000L, 2500L, "tag2", null);
        StopWatch watch2b = new StopWatch(START_TIME + 32000L, 2000L, "tag2", null);
        statistics = new GroupedTimingStatistics(Arrays.asList(watch1, watch2, watch2b),
                                                 START_TIME + 30000L,
                                                 START_TIME + 60000L,
                                                 false);
        chart.appendData(statistics);
        tpsChart.appendData(statistics);

        watch1 = new StopWatch(START_TIME + 62000L, 1500L, "tag1", null);
        statistics =
                new GroupedTimingStatistics(Arrays.asList(watch1), START_TIME + 60000L, START_TIME + 90000L, false);
        chart.appendData(statistics);
        tpsChart.appendData(statistics);

        verifyUrl(chart.getChartUrl(), "twoSeriesThreeDataPoints");
        verifyUrl(tpsChart.getChartUrl(), "twoSeriesThreeDataPointsTps");
    }

    protected void verifyUrl(String url, String name) {
        System.out.println(name + "=" + url);
        assertEquals(expectedChartUrls.getString(name), url);
    }
}
