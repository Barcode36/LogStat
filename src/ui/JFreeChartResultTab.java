package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.override.MyChartFactory;
import org.jfree.chart.override.MyDateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.HorizontalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import tasks.PopulateBarChartTaskImpl;
import tasks.PopulateJFreeChartTaskImpl;
import util.Benchmark;

public class JFreeChartResultTab extends ResultTab{

	private static Logger logger = LoggerFactory.getLogger(JFreeChartResultTab.class);
	
	public JFreeChartResultTab(TabPane tabpane, String name, List<String> data) {
		super(tabpane, name, data);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected Object createChart() {
		// TODO Auto-generated method stub
		return null;
	}

	private JFreeChart createJFreeChart(String name, XYDataset dataset) {
		// TODO Auto-generated method stub
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                name,    // title
                "Time",             // x-axis label
                "Value",      // y-axis label
                dataset);

            String fontName = "Palatino";
            chart.getTitle().setFont(new Font(fontName, Font.BOLD, 18));
            /*
            chart.addSubtitle(new TextTitle("Source: http://www.ico.org/historical/2010-19/PDF/HIST-PRICES.pdf", 
                    new Font(fontName, Font.PLAIN, 14)));
             */
            
            XYPlot plot = (XYPlot) chart.getPlot();
            plot.setDomainPannable(true);
            plot.setRangePannable(true);
            plot.setDomainCrosshairVisible(true);
            plot.setRangeCrosshairVisible(true);
            plot.getDomainAxis().setLowerMargin(0.0);
            plot.getDomainAxis().setLabelFont(new Font(fontName, Font.BOLD, 14));
            plot.getDomainAxis().setTickLabelFont(new Font(fontName, Font.PLAIN, 12));
            plot.getRangeAxis().setLabelFont(new Font(fontName, Font.BOLD, 14));
            plot.getRangeAxis().setTickLabelFont(new Font(fontName, Font.PLAIN, 12));
            chart.getLegend().setItemFont(new Font(fontName, Font.PLAIN, 14));
            chart.getLegend().setFrame(BlockBorder.NONE);
            chart.getLegend().setHorizontalAlignment(HorizontalAlignment.CENTER);
            
            DateAxis dateaxis = (DateAxis) plot.getDomainAxis();
            
            dateaxis.setStandardTickUnits(MyDateAxis.createStandardDateTickUnits(TimeZone.getDefault(),Locale.getDefault()));
            
            //@SuppressWarnings("unused")
    		//DateTickUnit s=dateaxis.getTickUnit();
            
            dateaxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1, new SimpleDateFormat("dd.MMM.yy hh:mm:ss.sss", Locale.US)), true,false);
            
            dateaxis.setVerticalTickLabels(true);
            dateaxis.setAutoRange(true);
            

            
            XYItemRenderer r = plot.getRenderer();
            if (r instanceof XYLineAndShapeRenderer) {
                XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
                renderer.setBaseShapesVisible(false);
                renderer.setDrawSeriesLineAsPath(false);
                // set the default stroke for all series
                renderer.setAutoPopulateSeriesStroke(false);
                renderer.setBaseStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, 
                        BasicStroke.JOIN_BEVEL), false);
                renderer.setSeriesPaint(0, Color.RED);
                renderer.setSeriesPaint(1, new Color(24, 123, 58));
                renderer.setSeriesPaint(2, new Color(149, 201, 136));
                renderer.setSeriesPaint(3, new Color(1, 62, 29));
                renderer.setSeriesPaint(4, new Color(81, 176, 86));
                renderer.setSeriesPaint(5, new Color(0, 55, 122));
                renderer.setSeriesPaint(6, new Color(0, 92, 165));
            }

            return chart;

	}

	@Override
	protected PopulateJFreeChartTaskImpl getPopulateChartTask(List<TableColumn<Integer, String>> f, String selectedXaxis,
			List<String> selectedYaxis) {
		// TODO Auto-generated method stub
		return new PopulateJFreeChartTaskImpl(f,selectedXaxis,selectedYaxis);
	}



	@Override
	protected EventHandler<ActionEvent> getChartButtonEventHandler() {
		// TODO Auto-generated method stub
		
		
		
		
		return new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

			
				PopulateJFreeChartTaskImpl task = getPopulateChartTask(getTableData(), getSelectedXaxis(), getSelectedYaxis());
				
				bar.progressProperty().bind(task.progressProperty());
				
				new Thread(task).start();

				task.setOnSucceeded(ev -> {
					try {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								Benchmark.tick();
						    	XYDataset dataset = task.getValue();
						        Benchmark.tock("create dataset");
						        
						        Benchmark.tick();
						        JFreeChart chart = createJFreeChart(tab.getText(),dataset); 
						        ChartViewer viewer = new ChartViewer(chart);  
						        Benchmark.tock("Chart created");
								chart_sp_hbox.getChildren().clear();
								chart_sp_hbox.getChildren().add(viewer);
								viewer.prefHeightProperty().bind(chart_sp_hbox.heightProperty());
								viewer.prefWidthProperty().bind(chart_sp_hbox.widthProperty());
								
							}
						});

					} catch (Exception ex) {
						logger.error("Something went wrong", ex);
					}
					;
				}

				);

			}
		};
	}




}
