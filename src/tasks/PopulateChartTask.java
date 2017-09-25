package tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import ui.ResultTab;
import util.Benchmark;

public class PopulateChartTask extends Task<List<XYChart.Series<String,Double>>> {

	private static Logger logger = LoggerFactory.getLogger(PopulateChartTask.class);
	
	
	final protected List<TableColumn<Integer,String>> f;
	final protected  List<String> selectedYaxis;
	final protected  String selectedXaxis;
	//final protected BarChart bc;
	//final protected String title;
	
	
	public PopulateChartTask(List<TableColumn<Integer,String>> f,String selectedXaxis,List<String> selectedYaxis){
		
		//this.bc=bc;
		this.f=f;
		this.selectedXaxis=selectedXaxis;
		this.selectedYaxis=selectedYaxis;
		//this.title=title;
		
	}
	protected List<XYChart.Series<String,Double>> call() throws Exception {
		// TODO Auto-generated method stub
		try{
		
		TableColumn<Integer,String> xaxis=f.stream().filter(p->p.getText().equals(selectedXaxis))
				.findAny().get();
				
		List<TableColumn<Integer,String>> yaxis = f.stream().filter(p->selectedYaxis.contains(p.getText()))
				.map(p->(TableColumn)p)
				.collect(Collectors.toList());
		

		List<XYChart.Series<String,Double>> series=new ArrayList<XYChart.Series<String,Double>>();
		
		int progress=0;
		int rows=0;
		
		Benchmark.tick();
        
		for(TableColumn y: yaxis){
        	XYChart.Series<String,Double> series1 = new XYChart.Series<String,Double>();
        	series1.setName(y.getText());  
        	rows=y.getTableView().getItems().size();
        	
        	
        	if (rows>0){
        		List<XYChart.Data<String,Double>> xychart_list_data=new ArrayList<XYChart.Data<String,Double>>();
        		
        		for (int i=1;i<rows;i++){
        			
        			String y_str=(String)y.getCellData(i); //System.out.print("y: "+y_str);
        			String x_str=(String) xaxis.getCellData(i);//System.out.println("x: "+x_str);
        			
        			xychart_list_data.add(new XYChart.Data<String,Double>(x_str,Double.parseDouble(y_str)));
        			
        			updateProgress(progress++,yaxis.size()*rows);
        		}
        		series1.getData().addAll(xychart_list_data);
        		
        		series.add(series1);
        	}
        	
        }
        	Benchmark.tock("Chart populated in:");
        	
        	int x=series.size()*rows;
        	
        	logger.info("There are "+x+" datapoints");
        	return series;
        	
        
		}catch(NumberFormatException ex){
			logger.error("Can't build chart as one of series is not numeric", ex);
		}
		return null;

	}

	@Override
	protected void succeeded() {
		super.succeeded();
		updateMessage("Done!");
	}

	@Override
	protected void cancelled() {
		super.cancelled();
		updateMessage("Cancelled!");
	}

	@Override
	protected void failed() {
		super.failed();
		updateMessage("Failed!");
	}

}
