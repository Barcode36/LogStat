package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.DynamicTableViewColumnCount;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import tasks.PopulateChartTask;
import util.Benchmark;
import javafx.scene.control.TableColumn;


@SuppressWarnings("unchecked")
public class ResultTab {

	private static Logger logger = LoggerFactory.getLogger(ResultTab.class);

	Tab tab;
	TabPane tabpane;

	TableView tbl;
	HBox hbox;

	TabPane tab_sp;
	ListView yaxis;
	ComboBox xaxis;
	LineChart bc;

	String YAXIS = "Y-axis";
	String XAXIS = "X-axis";

	List<String> data;
	DynamicTableViewColumnCount table_generator;
	Button btn;
	ProgressBar bar;
	HBox chart_sp_hbox;

	public ResultTab(TabPane tabpane, String name, List<String> data) {

		try {
			this.data = data;

			// create new Tab
			tab = new Tab();
			tab.setText(name);
			tab.setId(name);
			tab.setClosable(true);
			

			this.tabpane = tabpane;

			// SplitPane to hold Table and Charts related objects
			//tab_sp = new SplitPane();
			tab_sp = new TabPane();
			
			Tab tableTab=new Tab();
			tableTab.setText("Data");
			tableTab.setClosable(false);
			Tab chartTab=new Tab();
			chartTab.setText("Chart");
			chartTab.setClosable(false);
					
			tab_sp.getTabs().addAll(tableTab,chartTab);
			
			HBox tab_sp_hbox = new HBox();

			chartTab.setContent(tab_sp_hbox);
			
			// SplitPane to hold char and chart comboboxes
			SplitPane chart_sp = new SplitPane();

			// HBox to hold chart
			chart_sp_hbox = new HBox();

			// HBox to hold comboboxes and listview
			HBox lview_sp_hbox = new HBox();

			chart_sp.getItems().addAll(chart_sp_hbox, lview_sp_hbox);
			chart_sp.setOrientation(Orientation.VERTICAL);

			//bc = createbarchar();

			hbox = new HBox();
			tbl = new TableView();
			tbl.setId("tbl" + tab.getId());

			// populate tableview with data
			table_generator = new DynamicTableViewColumnCount();
			table_generator.start(data, tbl);

			tableTab.setContent(tbl);
			
			hbox.getChildren().addAll(tab_sp);
			hbox.setAlignment(Pos.CENTER);

			tab_sp_hbox.getChildren().add(chart_sp);
			
			bc=createbarchar();
			chart_sp_hbox.getChildren().add(bc);

			
			GridPane grid = createGridPane();
			lview_sp_hbox.getChildren().add(grid);

			//tab_sp.getItems().addAll(tab_sp_hbox, tbl);
			
			//tab_sp.setDividerPositions(0.3f, 0.6f);

			tab_sp.prefHeightProperty().bind(hbox.heightProperty());
			tab_sp.prefWidthProperty().bind(hbox.widthProperty());

			tab_sp_hbox.prefWidthProperty().bind(tab_sp.widthProperty());
			tab_sp_hbox.prefHeightProperty().bind(tab_sp.heightProperty());
			
			chart_sp_hbox.prefWidthProperty().bind(chart_sp.widthProperty());
			chart_sp_hbox.prefHeightProperty().bind(chart_sp.heightProperty());
			bc.prefWidthProperty().bind(chart_sp_hbox.widthProperty());
			bc.prefHeightProperty().bind(chart_sp_hbox.heightProperty());
			
			chart_sp.prefWidthProperty().bind(tab_sp_hbox.widthProperty());
			chart_sp.prefHeightProperty().bind(tab_sp_hbox.heightProperty());

			tab.setContent(hbox);
			tabpane.getTabs().add(tab);

			// populate data for ComboBox and ListView
			populateChartDataSeries();

		} catch (Exception ex) {

			logger.error("Can't create Tab", ex);

		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LineChart createbarchar() {

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
	    
		
		final LineChart<String, Number> bc = new LineChart<String, Number>(xAxis, yAxis);

		bc.setCreateSymbols(false);
	    bc.setAnimated(false);
	    
		return bc;
	}
	
	
/*	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LineChart createJFreechart() {

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
	    
		
		final LineChart<String, Number> bc = new LineChart<String, Number>(xAxis, yAxis);

		bc.setCreateSymbols(false);
	    bc.setAnimated(false);
	    
	    
	    CategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset); 
        ChartViewer viewer = new ChartViewer(chart);
        //viewer.addChartMouseListener(this);
	    
	    
		return bc;
	}*/	

	public final void populateChart() {

		try {
			TableColumn<Integer, String> xaxis = f.stream().filter(p -> p.getText().equals(selectedXaxis)).findAny()
					.get();

			List<TableColumn<Integer, String>> yaxis = f.stream().filter(p -> selectedYaxis.contains(p.getText()))
					.map(p -> (TableColumn) p).collect(Collectors.toList());

			bc.getData().clear();
			System.out.println(bc.getData().size());
			bc.setTitle(tab.getText());
			bc.getYAxis().setLabel("Value");
			bc.getXAxis().setLabel(xaxis.getText());

			for (TableColumn y : yaxis) {
				XYChart.Series series1 = new XYChart.Series();
				series1.setName(y.getText());
				int rows = y.getTableView().getItems().size();

				Benchmark.tick();
				if (rows > 0) {
					for (int i = 1; i < rows; i++) {

						String y_str = (String) y.getCellData(i); // System.out.print("y:
																	// "+y_str);
						String x_str = (String) xaxis.getCellData(i);// System.out.println("x:
																		// "+x_str);

						series1.getData().add(new XYChart.Data(x_str, Double.parseDouble(y_str)));
					}
				}

				Benchmark.tock("Chart populated in:");
				bc.getData().add(series1);

			}
		} catch (NumberFormatException ex) {
			logger.error("Can't build chart as one of series is not numeric", ex);
		}

	}

	public final void populateChartDataSeries() {
		generateYaxis(tbl);
		generateXaxis(tbl);
	}

	private GridPane createGridPane() {

		GridPane grid = new GridPane();

		Text scenetitle = new Text("Chart data series");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label lb_xaxis = new Label(XAXIS);
		grid.add(lb_xaxis, 0, 1);

		xaxis = new ComboBox();
		grid.add(xaxis, 1, 1);

		Label lb_yaxis = new Label(YAXIS);
		grid.add(lb_yaxis, 0, 2);

		yaxis = new ListView();
		grid.add(yaxis, 1, 2);

		btn = new Button("Build graph");

		grid.add(btn, 1, 3);
		
		bar=new ProgressBar();
		
		grid.add(bar, 1, 4);

		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				// populateChart();

				//LineChart b_c=createbarchar();
				
				System.out.println(bc.getData().size());
				bc.getData().clear();
				
				chart_sp_hbox.getChildren().clear();
				chart_sp_hbox.getChildren().add(bc);
				bc.setTitle(tab.getText());
				bc.getYAxis().setLabel("Value");
				bc.getXAxis().setLabel("Time");
				
				
				

				PopulateChartTask task = new PopulateChartTask(f, selectedXaxis, selectedYaxis);
				
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
		});

		return grid;

	}

	private String selectedXaxis = "";

	@SuppressWarnings(value = { "", "rawtypes", "unchecked" })
	private ComboBox generateXaxis(TableView tbl) {

		f = (List<TableColumn<Integer, String>>) tbl.getColumns().stream().map(p -> ((TableColumn) p))
				.collect(Collectors.toList());

		xaxis.getItems().addAll(f.stream().map(p -> p.getText()).collect(Collectors.toList()));

		// Handle ComboBox event.
		xaxis.setOnAction((event) -> {
			selectedXaxis = (String) xaxis.getSelectionModel().getSelectedItem();
			logger.info("ComboBox Action (selected: " + selectedXaxis.toString() + ")");
		});

		return xaxis;

	}

	private List selectedYaxis = new ArrayList<String>();

	private List<TableColumn<Integer, String>> f;

	private ListView generateYaxis(TableView tbl) {
		f = (List<TableColumn<Integer, String>>) tbl.getColumns().stream().map(p -> ((TableColumn) p))
				.collect(Collectors.toList());

		yaxis.getItems().addAll(f.stream().map(p -> p.getText()).collect(Collectors.toList()));

		// Handle ListView selection changes.
		yaxis.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			selectedYaxis.add((String) yaxis.getSelectionModel().getSelectedItem());
			logger.info("ListView Selection Changed (selected: " + newValue.toString() + ")");
			selectedYaxis.stream().forEach(System.out::println);
		});

		return yaxis;
	}

	public TableView getTable() {
		return tbl;
	}

}
