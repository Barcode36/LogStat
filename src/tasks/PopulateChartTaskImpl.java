package tasks;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;
import javafx.scene.control.TableColumn;

public abstract class PopulateChartTaskImpl<T> extends Task<T>implements PopulateChartTask{

	private Logger logger = LoggerFactory.getLogger(PopulateBarChartTaskImpl.class);
	
	
	final protected List<TableColumn<Integer,String>> f;
	final protected  List<String> selectedYaxis;
	final protected  String selectedXaxis;
	
	protected TableColumn<Integer,String> xaxis;
	protected List<TableColumn<Integer,String>> yaxis; 

	
	
	public PopulateChartTaskImpl(List<TableColumn<Integer,String>> f,String selectedXaxis,List<String> selectedYaxis){
		
		this.f=f;
		this.selectedXaxis=selectedXaxis;
		this.selectedYaxis=selectedYaxis;
		
		xaxis=f.stream().filter(p->p.getText().equals(selectedXaxis))
				.findAny().get();
				
		yaxis= f.stream().filter(p->selectedYaxis.contains(p.getText()))
				.map(p->(TableColumn<Integer, String>)p)
				.collect(Collectors.toList());
		
	}
	
	abstract protected T call() throws Exception;

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
