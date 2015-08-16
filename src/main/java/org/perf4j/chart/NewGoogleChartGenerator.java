/**
 * 
 */
package org.perf4j.chart;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.perf4j.GroupedTimingStatistics;
import org.perf4j.TimingStatistics;
import org.perf4j.helpers.StatsValueRetriever;

/**
 * This implementation of StatisticsChartGenerator creates a chart URL in the format expected by the Google Chart API.
 * 
 * @see <a href="http://code.google.com/apis/chart/">Google Chart API</a>
 * @author yuhui.cyh
 *
 */
public class NewGoogleChartGenerator implements StatisticsChartGenerator{

	private LinkedList<GroupedTimingStatistics> data = new LinkedList<GroupedTimingStatistics>();
	private Integer maxDataPoints = 500;
	private StatsValueRetriever valueRetriever;
	private Set<String> enabledTags = null;
	
	public NewGoogleChartGenerator(StatsValueRetriever statsValueRetriever) {
        this.valueRetriever = statsValueRetriever;
    }
	
	public NewGoogleChartGenerator() {
	        this(StatsValueRetriever.MEAN_VALUE_RETRIEVER);
	}
	
	public String getChartUrl() {
		// TODO Auto-generated method stub
		return "";
	}

	public void appendData(GroupedTimingStatistics statistics) {
		if(statistics==null || statistics.getStatisticsByTag().isEmpty()) {
			return;
		}
		if (this.data.size() >= this.maxDataPoints) {
            this.data.removeFirst();
        }
        this.data.add(statistics);
		
	}

	public List<GroupedTimingStatistics> getData() {
		 return Collections.unmodifiableList(this.data);
	}
	
	protected Map<String, List<Number>[]> generateGoogleChartParams() {
        long minTimeValue = Long.MAX_VALUE;
        long maxTimeValue = Long.MIN_VALUE;
        double maxDataValue = Double.MIN_VALUE;
        //this map stores all the data series. The key is the tag name (each tag represents a single series) and the
        //value contains two lists of numbers - the first list contains the X values for each point (which is time in
        //milliseconds) and the second list contains the y values, which are the data values pulled from dataWindows.
        Map<String, List<Number>[]> tagsToXDataAndYData = new TreeMap<String, List<Number>[]>();

        for (GroupedTimingStatistics groupedTimingStatistics : data) {
            Map<String, TimingStatistics> statsByTag = groupedTimingStatistics.getStatisticsByTag();
            long windowStartTime = groupedTimingStatistics.getStartTime();
            long windowLength = groupedTimingStatistics.getStopTime() - windowStartTime;
            //keep track of the min/max time value, this is needed for scaling the chart parameters
            minTimeValue = Math.min(minTimeValue, windowStartTime);
            maxTimeValue = Math.max(maxTimeValue, windowStartTime);

            for (Map.Entry<String, TimingStatistics> tagWithData : statsByTag.entrySet()) {
                String tag = tagWithData.getKey();
                if (this.enabledTags == null || this.enabledTags.contains(tag)) {
                    //get the corresponding value from tagsToXDataAndYData
                    List<Number>[] xAndYData = tagsToXDataAndYData.get(tagWithData.getKey());
                    if (xAndYData == null) {
                        tagsToXDataAndYData.put(tag, xAndYData = new List[]{new ArrayList<Number>(),
                                                                            new ArrayList<Number>()});
                    }

                    //the x data is the start time of the window, the y data is the value
                    Number yValue = this.valueRetriever.getStatsValue(tagWithData.getValue(), windowLength);
                    xAndYData[0].add(windowStartTime);
                    xAndYData[1].add(yValue);

                    //update the max data value, which is needed for scaling
                    maxDataValue = Math.max(maxDataValue, yValue.doubleValue());
                }
            }
        }
        return tagsToXDataAndYData;
    }

	
	
	////======================Getters and setters====================////
	public Integer getMaxDataPoints() {
		return maxDataPoints;
	}

	public void setMaxDataPoints(Integer maxDataPoints) {
		this.maxDataPoints = maxDataPoints;
	}

	public Set<String> getEnabledTags() {
		return enabledTags;
	}

	public void setEnabledTags(Set<String> enabledTags) {
		this.enabledTags = enabledTags;
	}

	
}
