package ui;

import java.util.List;

import javafx.scene.control.TabPane;

public class ResultTabFactory {
	
	public static ResultTab getInstance(TabPane tabpane, String name, List<String> data){
		return new BarChartResultTab(tabpane,name,data);
	}

}
