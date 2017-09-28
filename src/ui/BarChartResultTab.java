package ui;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import tasks.PopulateBarChartTaskImpl;
import util.Benchmark;

public class BarChartResultTab extends ResultTab {
	
	private static Logger logger = LoggerFactory.getLogger(BarChartResultTab.class);

	public BarChartResultTab(TabPane tabpane, String name, List<String> data) {
		super(tabpane, name, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Object createChart(){ 
	
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
	    
		
		final LineChart<String, Number> bc = new LineChart<String, Number>(xAxis, yAxis);
	
		bc.setCreateSymbols(false);
	    bc.setAnimated(false);
	    
		return bc;
	}

	@Override
	protected PopulateBarChartTaskImpl getPopulateChartTask(List<TableColumn<Integer,String>> f,String selectedXaxis,List<String> selectedYaxis) {
		
		return new PopulateBarChartTaskImpl(f,selectedXaxis,selectedYaxis);
		
	}

	
	protected EventHandler<ActionEvent> getChartButtonEventHandler() {
		// TODO Auto-generated method stub
		
		LineChart bc=(LineChart)createChart();
		chart_sp_hbox.getChildren().add(bc);
		
		bc.prefWidthProperty().bind(chart_sp_hbox.widthProperty());
		bc.prefHeightProperty().bind(chart_sp_hbox.heightProperty());
		
		return new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

				
				System.out.println(bc.getData().size());
				bc.getData().clear();
				
				chart_sp_hbox.getChildren().clear();
				chart_sp_hbox.getChildren().add(bc);
				bc.setTitle(tab.getText());
				bc.getYAxis().setLabel("Value");
				bc.getXAxis().setLabel("Time");
				
				
				

				PopulateBarChartTaskImpl task = getPopulateChartTask(getTableData(), getSelectedXaxis(), getSelectedYaxis());
				
				bar.progressProperty().bind(task.progressProperty());
				
				new Thread(task).start();

				task.setOnSucceeded(ev -> {
					try {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								Benchmark.tick();
								List<XYChart.Series<String,Double>> l=task.getValue();
								
								
								if (l.size()>100){
									if(bc.getXAxis().isTickMarkVisible())
										bc.getXAxis().setTickMarkVisible(false);
									else
										bc.getXAxis().setTickMarkVisible(true);
									
									bc.getXAxis().setTickLabelsVisible(false);
								}
								
								bc.getData().addAll(FXCollections.observableList(l));
								Benchmark.tock("BarChart series added");
								
								
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
