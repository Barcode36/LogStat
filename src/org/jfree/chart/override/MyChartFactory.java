package org.jfree.chart.override;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;

public class MyChartFactory extends ChartFactory{

	private static ChartTheme currentTheme = new StandardChartTheme("JFree");
	
    public static JFreeChart createTimeSeriesChart(String title, 
            String timeAxisLabel, String valueAxisLabel, XYDataset dataset) {
        return createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, 
                dataset, true, true, false);
    }
    
	 public static JFreeChart createTimeSeriesChart(String title,
	            String timeAxisLabel, String valueAxisLabel, XYDataset dataset,
	            boolean legend, boolean tooltips, boolean urls) {

	        ValueAxis timeAxis = new MyDateAxis(timeAxisLabel);
	        timeAxis.setLowerMargin(0.02);  // reduce the default margins
	        timeAxis.setUpperMargin(0.02);
	        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
	        valueAxis.setAutoRangeIncludesZero(false);  // override default
	        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, null);

	        XYToolTipGenerator toolTipGenerator = null;
	        if (tooltips) {
	            toolTipGenerator
	                = StandardXYToolTipGenerator.getTimeSeriesInstance();
	        }

	        XYURLGenerator urlGenerator = null;
	        if (urls) {
	            urlGenerator = new StandardXYURLGenerator();
	        }

	        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,
	                false);
	        renderer.setBaseToolTipGenerator(toolTipGenerator);
	        renderer.setURLGenerator(urlGenerator);
	        plot.setRenderer(renderer);

	        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
	                plot, legend);
	        currentTheme.apply(chart);
	        return chart;

	    }
}
