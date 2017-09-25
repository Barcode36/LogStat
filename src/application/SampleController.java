package application;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.sun.javafx.tk.FileChooserType;

import cmdline.CmdLineParser;
import cmdline.Command;
import cmdline.CommandImpl;
import cmdline.CommandParse;
import garbagecleaner.ENUMERATIONS;
import garbagecleaner.ProcessFilesFabric;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import statmanager.StatisticManager;
import statmanager.UnsupportedStatFormatException;
import tasks.ParseTask;
import tasks.PrintTask;
import ui.ResultTab;
import util.FilesUtil;
import utils.Functions;
import enums.*;

import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleController {

	private Logger logger = LoggerFactory.getLogger(SampleController.class);
	private File jarPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
	private String propertiesPath = jarPath.getParentFile().getAbsolutePath() + "\\conf\\";
	private String resultPath = jarPath.getParentFile().getAbsolutePath() + "\\results\\";

	@FXML
	private ProgressBar bar;

	@FXML
	private TextArea txt_statfile;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField txtb_folderpath;

	@FXML
	private Button btn_parse;

	@FXML
	private TextField txt_statfile_path;

	@FXML
	private ChoiceBox<Sampling> cb_sampling;

	@FXML
	private TextField txtb_extensions;

	@FXML
	private Button openFolderBtn;

	@FXML
	private Button btn_saveas_statfile;
	@FXML
	private Button btn_openstatfile;

	@FXML
	private Button btn_save_statfile;

	@FXML
	private TextField txtb_result_fname;

	@FXML
	private ComboBox<Commands> cb_cmd;

	@FXML
	private ComboBox<Processor> cb_processor;

	@FXML
	private ComboBox<Format> cb_format;

	@FXML
	private TabPane tabpane;

	@FXML
	private TableView<?> tbl_result;

	@FXML
	private TableView<?> tbl_files;

	@FXML
	private TextArea tb_console;

	@FXML
	private Tab tab_console;

	@FXML
	private Tab tab_result;

	@FXML
	private Tab tab_files;

	@FXML
	private MenuItem menu_result_open;

    @FXML
    private ListView<String> x_axis_lv;
    
    @FXML
    private ChoiceBox<String> yaxis_cb;
    
	FileChooser resultChooser = new FileChooser();

	File lastResult = new File(resultPath);

	@FXML
	void menu_result_open_fired(ActionEvent event) {

		try {
			// Set extension filter
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
					"Output files(*.out,*.stat,*.table,*.block)", "*.out", "*.stat", "*.table", "*.block");
			resultChooser.getExtensionFilters().add(extFilter);

			if (null != lastResult)
				resultChooser.setInitialDirectory(lastResult);

			File selectedFile = resultChooser.showOpenDialog(new Stage());
			if (selectedFile != null) {
				selectedFile.getAbsolutePath();
				// txt_statfile_path.setText(selectedFile.getName());
			}

			lastResult = selectedFile.getParentFile();

			BufferedReader in;
			in = new BufferedReader(new FileReader(selectedFile.getAbsolutePath()));
			String str;

			List<String> data = new ArrayList<String>();

			File f = new File(selectedFile.getAbsolutePath());
			Scanner input = new Scanner(f);

			while ((str = in.readLine()) != null) {
				data.add(str);

			}


			new ResultTab(tabpane,selectedFile.getName(), data);
			
		} catch (IOException ex) {

			logger.error("Can't open file ", ex);

		} catch (Exception e) {

			logger.error("Can't fill tableview", e);
		}

	}

	@FXML
	void cb_cmd_fired(ActionEvent event) {

	}

	// need it to output logging into textarea
	public TextAreaHandler textAreaHandler = new TextAreaHandler();

	@FXML
	public void initialize() {
		assert btn_save_statfile != null : "fx:id=\"btn_save_statfile\" was not injected: check your FXML file 'Sample.fxml'.";
		assert btn_saveas_statfile != null : "fx:id=\"btn_saveas_statfile\" was not injected: check your FXML file 'Sample.fxml'.";
		assert txtb_folderpath != null : "fx:id=\"txtb_folderpath\" was not injected: check your FXML file 'Sample.fxml'.";
		assert cb_sampling != null : "fx:id=\"cb_sampling\" was not injected: check your FXML file 'Sample.fxml'.";
		assert btn_openstatfile != null : "fx:id=\"btn_openstatfile\" was not injected: check your FXML file 'Sample.fxml'.";
		assert txtb_extensions != null : "fx:id=\"txtb_extensions\" was not injected: check your FXML file 'Sample.fxml'.";
		assert openFolderBtn != null : "fx:id=\"openFolderBtn\" was not injected: check your FXML file 'Sample.fxml'.";
		assert txtb_result_fname != null : "fx:id=\"txtb_result_fname\" was not injected: check your FXML file 'Sample.fxml'.";
		assert cb_cmd != null : "fx:id=\"cb_cmd\" was not injected: check your FXML file 'Sample.fxml'.";
		assert cb_processor != null : "fx:id=\"cb_processor\" was not injected: check your FXML file 'Sample.fxml'.";
		assert cb_format != null : "fx:id=\"cb_format\" was not injected: check your FXML file 'Sample.fxml'.";
		assert txt_statfile != null : "fx:id=\"txt_statfile\" was not injected: check your FXML file 'Sample.fxml'.";

		assert txt_statfile != null : "fx:id=\"txt_statfile\" was not injected: check your FXML file 'Sample.fxml'.";

		assert bar != null : "fx:id=\"bar\" was not injected: check your FXML file 'Sample.fxml'.";

		assert tbl_result != null : "fx:id=\"tbl_result\" was not injected: check your FXML file 'Sample.fxml'.";
		assert tb_console != null : "fx:id=\"tb_console\" was not injected: check your FXML file 'Sample.fxml'.";

		txtb_folderpath.setText("D:\\Share\\distrib\\3dparty\\GarbageCleaner\\logs");

		// bar.autosize();
		tbl_result.autosize();
		tb_console.autosize();

		cb_cmd.getItems().clear();
		cb_cmd.getItems().addAll(Commands.values());
		cb_cmd.getSelectionModel().select(Commands.genesys);

		cb_format.getItems().clear();
		cb_format.getItems().addAll(Format.values());
		cb_format.getSelectionModel().select(Format.STAT);

		cb_processor.getItems().clear();
		cb_processor.getItems().addAll(Processor.values());
		cb_processor.getSelectionModel().select(Processor.TIME);

		cb_sampling.getItems().clear();
		cb_sampling.getItems().addAll(Sampling.values());
		cb_sampling.getSelectionModel().select(Sampling.ZERO);

		// configuration to send log events to textarea
		OutputStream os = new TextAreaOutputStream(tb_console);

		MyStaticOutputStreamAppender.setStaticOutputStream(os);

		// textAreaHandler.setTextArea(tb_console);

		// configuration to update Files Tab with list of selected files
		txtb_extensions.textProperty().addListener((obs, oldText, newText) -> {
			printFolderFilesToConsole();
			// ...
		});

		// checking classpath
		ClassLoader cl = ClassLoader.getSystemClassLoader();

		URL[] urls = ((URLClassLoader) cl).getURLs();

		for (URL url : urls) {
			logger.debug("Classpath: {}", url.getFile());
		}

		printFolderFilesToConsole();

	}

	private static class TextAreaOutputStream extends OutputStream {

		private TextArea textArea;

		private Task task;

		public TextAreaOutputStream(TextArea textArea) {
			this.textArea = textArea;
			
		}

		private StringBuilder buffer=new StringBuilder();
		int buffer_length=1000;
		
		@Override
		public void write(int b) throws IOException {
			
			buffer.append((char) b);
			
			//if(buffer.length()>buffer_length){
			if (b==13){// carriage return?
			Platform.runLater(new Runnable() {
		        public void run() {
		            textArea.appendText(buffer.toString());
		            buffer.delete(0, buffer.length());
		        }
		    });
			
			}
		}
	}

	public

	DirectoryChooser directoryChooser = new DirectoryChooser();
	File lastDir;

	@FXML
	void openFolderBtn_action(ActionEvent event) {

		if (null != lastDir)
			directoryChooser.setInitialDirectory(lastDir);

		File selectedDirectory = directoryChooser.showDialog(new Stage());
		if (selectedDirectory != null) {
			selectedDirectory.getAbsolutePath();
			txtb_folderpath.setText(selectedDirectory.toString());
		}

		lastDir = selectedDirectory;
		printFolderFilesToConsole();

	}

	private void printFolderFilesToConsole() {

		ArgumentsHolder arghld = new ArgumentsPrintImpl(Commands.print, txtb_folderpath.getText(),
				txtb_extensions.getText());
		try {

			PrintTask print = new PrintTask(arghld);

			Map<String, String> result = print.executeParsing();

			/*
			 * result.forEach((k, v) -> {
			 * 
			 * tb_console.appendText(k + "," + v + "\n");
			 * 
			 * 
			 * });
			 */

			new DynamicTableViewColumnCount().start(result, tbl_files);

		} catch (UnsupportedStatFormatException e) {
			logger.error("UnsupportedStatFormatException", e);
		} catch (Exception e) {
			logger.error("Something wrong", e);
		}

	}

	FileChooser fileChooser = new FileChooser();

	File lastFile = new File("D:\\Share\\distrib\\3dparty\\GarbageCleaner\\conf");

	@FXML
	void btn_openstatfile_fired(ActionEvent event) {
		try {

			// Set extension filter
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
					"Properties files(*.properties,*.ini)", "*.properties", "*.ini");
			fileChooser.getExtensionFilters().add(extFilter);

			if (null != lastFile)
				fileChooser.setInitialDirectory(lastFile);

			File selectedFile = fileChooser.showOpenDialog(new Stage());
			if (selectedFile != null) {
				selectedFile.getAbsolutePath();
				txt_statfile_path.setText(selectedFile.getName());
			}

			lastFile = selectedFile.getParentFile();

			BufferedReader in;
			in = new BufferedReader(new FileReader(selectedFile.getAbsolutePath()));
			String str;

			StringBuilder sb = new StringBuilder();
			File f = new File(selectedFile.getAbsolutePath());
			Scanner input = new Scanner(f);

			while ((str = in.readLine()) != null) {
				sb.append(str).append("\n");
				;
			}

			txt_statfile.setText(sb.toString());

			// save statfile to default /conf folder as CommandParse reads stats
			// from file only and only from this location
			if (Files.notExists(Paths.get(propertiesPath + "" + selectedFile.getName()), LinkOption.NOFOLLOW_LINKS))
				btn_save_statfile_fired(event);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("File is not found",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Can't write to the file", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Something went wrong",e);
		}
	}

	@FXML
	void btn_save_statfile_fired(ActionEvent event) throws Exception {

		// check if directory exists

		if(txt_statfile_path.getText().length()==0){
			txt_statfile_path.setText(Functions.generateFilename("statfile", "properties"));
		}
		
		if (txt_statfile.getText().length() != 0) {
			SaveFile(txt_statfile.getText(), new File(propertiesPath + txt_statfile_path.getText()));
		}else{
			throw new Exception("No defined statistics!");
		}
		

	}

	@FXML
	void btn_saveas_statfile_fired(ActionEvent event) {

		File jarPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		String propertiesPath = jarPath.getParentFile().getAbsolutePath() + "\\conf";

		fileChooser.setInitialDirectory(new File(propertiesPath));

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Properties files(*.properties,*.ini)",
				"*.properties", "*.ini");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(new Stage());

		txt_statfile_path.setText(file.getName());
		
		if (file != null) {
			SaveFile(txt_statfile.getText(), file);
		}

	}

	/*
	 * genesys -d <pathtologfolder> -ext <file ext> -processor
	 * (time|simple|table) -sample (0|1|10|60|24) -format
	 * (csv|sql|stat|block|table) -statfile <path to statfile> -out <name of
	 * result file>
	 * 
	 */
	@SuppressWarnings("unchecked")
	@FXML
	void btn_parse_fired(ActionEvent event) throws Exception {
		Button src = (Button) event.getSource();
		src.setDisable(true);

		try {
			btn_save_statfile_fired(event);
			
			ArgumentsHolder arg = new ArgumentsParseImpl(cb_cmd.getValue(), cb_processor.getValue(),
					cb_sampling.getValue(), cb_format.getValue(), txtb_folderpath.getText(), txtb_extensions.getText(),
					txt_statfile_path.getText(), txtb_result_fname.getText());

			ParseTask<List> task = new ParseTask<List>(arg);

			//tb_console.textProperty().bind(task.messageProperty());

			task.setOnSucceeded(e -> {
				try {

					new ResultTab(tabpane,arg.getOut(), task.getValue());
					
				} catch (Exception ex) {
					logger.error("Something went wrong", ex);
				}
				;
			}

			);

			task.setOnFailed(e -> {
				tb_console.appendText("Task failed!\n");
				tb_console.appendText(task.getMessage());
				tb_console.appendText(task.getException().getStackTrace().toString());

			});

			bar.progressProperty().bind(task.progressProperty());
			new Thread(task).start();
		} catch (Exception e) {
			logger.error("Something went wrong",e);
		} finally {
			if (src.isDisabled())
				src.setDisable(false);
		}

	}
	
	private TableView createTab(String name){
		
		Tab tab = new Tab();
        tab.setText(name);
        tab.setId(name);
        HBox hbox = new HBox();
        TableView tbl=new TableView();
        tbl.setId("tbl"+tab.getId());
        
        hbox.getChildren().add(tbl);
        hbox.setAlignment(Pos.CENTER);
        tbl.prefHeightProperty().bind(hbox.heightProperty());
        tbl.prefWidthProperty().bind(hbox.widthProperty());
        
        tab.setContent(hbox);
        tabpane.getTabs().add(tab);

		return tbl;
	}

	private void parse(String[] s) throws Exception {
		CmdLineParser cmdParser = new CmdLineParser();
		JCommander commander = cmdParser.getCommander();

		try {

			if (s[14].length() == 0)// if no result fname is provided generate
									// it
				s[14] = "testresult_" + System.nanoTime();
			String path = Paths.get("").toAbsolutePath().toString() + "\\results";

			commander.parse(s);
			Command cmd = cmdParser.getCommandObj(commander.getParsedCommand());

			Map<String, String> results = run((CommandImpl) cmd).getStatData();

			int i = Integer.valueOf(results.get("Found"));
			assertTrue(Files.exists(Paths.get(path + "\\" + s[14])));

			String[] result = FilesUtil.read(new FileInputStream((path + "\\" + s[14])));

		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			commander.usage();
			throw ex;
			// System.exit(1);
		} catch (UnsupportedStatFormatException ex) {
			System.out.println(ex.getMessage());
			commander.usage();
			throw ex;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		} finally {
			cmdParser.flush();
		}
	}

	private CommandImpl run(CommandImpl cmd) {
		cmd.getExtensions().forEach(s -> {
			ProcessFilesFabric.create((CommandImpl) cmd, s).start(cmd.getPaths());

		});
		return cmd;
	}

	private void SaveFile(String content, File file) {
		try {
			FileWriter fileWriter = null;

			fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
	

}
