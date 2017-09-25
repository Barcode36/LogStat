package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.experimental.theories.DataPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class DynamicTableViewColumnCount 
{

	private Logger logger = LoggerFactory.getLogger(DynamicTableViewColumnCount.class);

	public void start(Map<String,String> data, TableView tableview) throws Exception{
	
		List<String> list_data=new ArrayList<String>();
		
		data.forEach((k, v) -> {
			String s=k+","+v;
			list_data.add(s);
		}
		);
		
		start(list_data,tableview);
		
	}
	
    public void start(List<String> data, TableView tableview) throws Exception
    {
        tableview.getItems().clear();
        tableview.getColumns().clear();
        
    	TableView<Integer> tableView =tableview;

        // make sample data
        List<Row> rows = makeSampleData(data);
        Row header = rows.get(0);
        
        int max = getMaxCells(rows);
        makeColumns(max, tableView, rows);
        
        IntStream.range(1, rows.size()).forEach(tableView.getItems()::add);
        
        logger.info("Table "+tableview.getId()+" populated");


    		List<String> f=(List<String>)tableview.getColumns().stream().map(s->((TableColumn)s).getText())
    			    .collect(Collectors.toList());
		f.stream().forEach(System.out::println);
		
    }

    public void makeColumns(int count, TableView<Integer> tableView,List<Row> data) 
    {
    	
    	
    	for (int m = 0; m < count; m++)
        {
    		final int var=m;
    		String header_name=data.get(0).getCells().get(m).toString();
    		
            TableColumn<Integer, String> column = new TableColumn<>(header_name);
            column.setCellValueFactory(param -> {
            	
            	try{
            	int index = param.getValue();
            	//System.out.println("found var: "+var);
                List<Cell> cells = data.get(index).getCells();
                
                //System.out.println(Arrays.toString(cells.toArray(new String[cells.size()])));
                
                ReadOnlyObjectWrapper c=new ReadOnlyObjectWrapper<>(cells.get(var).toString());
                
                
                //System.out.println("found value: "+c.getValue());
                return c;
            	}catch(IndexOutOfBoundsException ex){
            		logger.error("Check the data conformity - rows should have values for all columns", ex);
            	}catch(Exception ex){
            		logger.error("Something went wrong", ex);
            	}
            	return null;
            });
            tableView.getColumns().add(column);
        }
    }

    public int getMaxCells(List<Row> rows)
    {
        int max = 0;
        for (Row row : rows)
            max = Math.max(max, row.getCells().size());
        return max;
    }

    public List<Row> makeSampleData(List<String> data)
    {
        
    	
    	//System.out.println("data.size:"+data.size());
    	
        List<Row> rows = new ArrayList<Row>();
        for (int i = 0; i < data.size(); i++)
        {
            Row e = new Row();
            
            String[] s=data.get(i).split(",");
            //System.out.println(i+"s.size:"+s.length);
            for (int j = 0; j < s.length; j++)
            {
                e.getCells().add(new Cell(s[j]));
            }
            rows.add(e);
        }
        return rows;
    }

    static class Row
    {
        private final List<Cell> list = new ArrayList<>();

        public List<Cell> getCells()
        {
            return list;
        }
    }

    static class Cell
    {
        private final String value;

        public Cell(String value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return value;
        }
    }
}