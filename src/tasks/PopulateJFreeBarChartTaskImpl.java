package tasks;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
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

public class PopulateJFreeBarChartTaskImpl extends PopulateChartTaskImpl<CategoryDataset> {

	private static Logger logger = LoggerFactory.getLogger(PopulateJFreeBarChartTaskImpl.class);
	
	public PopulateJFreeBarChartTaskImpl(List<TableColumn<Integer,String>> f, String selectedXaxis, List<String> selectedYaxis) {
		super(f, selectedXaxis, selectedYaxis);
		
	}

	protected CategoryDataset call() throws Exception {
		// TODO Auto-generated method stub
		try{
		

		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		int progress=0;
		int rows=0;
		
		Benchmark.tick();
        
		for(TableColumn y: yaxis){
        	
        	rows=y.getTableView().getItems().size();
        	
        	
        	if (rows>0){
        		
        		for (int i=1;i<rows;i++){
        			
        			String y_str=(String)y.getCellData(i); //System.out.print("y: "+y_str);
        			String x_str=(String) xaxis.getCellData(i);//System.out.println("x: "+x_str);
        			
        			dataset.addValue(Double.parseDouble(y_str),y.getText(),x_str);
        					
        			
        			updateProgress(progress++,yaxis.size()*rows);
        		}
        		
        		
        		//dataset.addSeries(series1);
        	}
        	
        }
        	Benchmark.tock("Chart populated in:");
        	
        	int x=dataset.getRowCount()*rows;
        	
        	logger.info("There are "+x+" datapoints");
        	return dataset;
        	
        
		}catch(NumberFormatException ex){
			logger.error("Can't build chart as one of series is not numeric", ex);
		}
		return null;

	}


}
