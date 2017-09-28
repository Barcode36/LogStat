package tasks;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import util.Benchmark;
import util.DateTime;

public class PopulateJFreeChartTaskImpl extends PopulateChartTaskImpl<XYDataset> {

	private Logger logger = LoggerFactory.getLogger(PopulateJFreeChartTaskImpl.class);
	
	public PopulateJFreeChartTaskImpl(List<TableColumn<Integer,String>> f, String selectedXaxis, List<String> selectedYaxis) {
		super(f, selectedXaxis, selectedYaxis);
		
	}

	protected XYDataset call() throws Exception {
		// TODO Auto-generated method stub
		try{
		

		List<XYDataset> series=new ArrayList<XYDataset>();
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		
		int progress=0;
		int rows=0;
		
		Benchmark.tick();
        
		for(TableColumn y: yaxis){
        	TimeSeries series1 = new TimeSeries(y.getText());
        	
        	rows=y.getTableView().getItems().size();
        	
        	
        	if (rows>0){
        		//List<XYChart.Data<String,Double>> xychart_list_data=new ArrayList<XYChart.Data<String,Double>>();
        		
        		for (int i=1;i<rows;i++){
        			
        			String y_str=(String)y.getCellData(i); //System.out.print("y: "+y_str);
        			String x_str=(String) xaxis.getCellData(i);//System.out.println("x: "+x_str);
        			
        			series1.add(new Millisecond(util.DateTime.asDate(
        					util.DateTime.SimpleStringToDate(x_str)
        					)), Double.parseDouble(y_str));
        			
        			updateProgress(progress++,yaxis.size()*rows);
        		}
        		
        		
        		dataset.addSeries(series1);
        	}
        	
        }
        	Benchmark.tock("Chart populated in:");
        	
        	int x=dataset.getSeriesCount()*rows;
        	
        	logger.info("There are "+x+" datapoints");
        	return dataset;
        	
        
		}catch(NumberFormatException ex){
			logger.error("Can't build chart as one of series is not numeric", ex);
		}
		return null;

	}


}
