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
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
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
import tasks.PopulateChartTaskImpl;
import tasks.PopulateJFreeBarChartTaskImpl;
import tasks.PopulateJFreeChartTaskImpl;
import util.Benchmark;

public class JFreeBarChartResultTab extends ResultTab{

	private static Logger logger = LoggerFactory.getLogger(JFreeBarChartResultTab.class);
	
	public JFreeBarChartResultTab(TabPane tabpane, String name, List<String> data) {
		super(tabpane, name, data);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected Object createChart() {
		// TODO Auto-generated method stub
		return null;
	}

	private JFreeChart createJFreeChart(String name, CategoryDataset dataset) {
		// TODO Auto-generated method stub
        JFreeChart chart = ChartFactory.createLineChart(
                name,    // title
                "Time",             // x-axis label
                "Value",      // y-axis label
                dataset);

            String fontName = "Palatino";
            chart.getTitle().setFont(new Font(fontName, Font.BOLD, 18));
            chart.setBackgroundPaint(Color.white);
            CategoryPlot plot = (CategoryPlot) chart.getPlot();

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            /*
            BarRenderer renderer = (LineRenderer) plot.getRenderer();
            renderer.setDrawBarOutline(false);
            */
            chart.getLegend().setFrame(BlockBorder.NONE);
            return chart;
	}

	@Override
	protected PopulateJFreeBarChartTaskImpl getPopulateChartTask(List<TableColumn<Integer, String>> f, String selectedXaxis,
			List<String> selectedYaxis) {
		// TODO Auto-generated method stub
		return new PopulateJFreeBarChartTaskImpl(f,selectedXaxis,selectedYaxis);
	}



	@Override
	protected EventHandler<ActionEvent> getChartButtonEventHandler() {
		// TODO Auto-generated method stub
		
		
		
		
		return new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

			
				PopulateJFreeBarChartTaskImpl task = getPopulateChartTask(getTableData(), getSelectedXaxis(), getSelectedYaxis());
				
				bar.progressProperty().bind(task.progressProperty());
				
				new Thread(task).start();

				task.setOnSucceeded(ev -> {
					try {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								Benchmark.tick();
						    	CategoryDataset dataset = task.getValue();
						        Benchmark.tock("create dataset");
						        
						        Benchmark.tick();
						        JFreeChart chart = createJFreeChart(tab.getText(),dataset); 
						        ChartViewer viewer = new ChartViewer(chart);  
						        Benchmark.tock("Chart created");
								chart_sp_hbox.getChildren().add(viewer);
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
