package tasks;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jfree.util.Log;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import application.ArgumentsHolder;
import cmdline.CmdLineParser;
import cmdline.Command;
import cmdline.CommandImpl;
import garbagecleaner.FKTProperties;
import garbagecleaner.ProcessFiles;
import garbagecleaner.ProcessFilesFabric;
import javafx.concurrent.Task;
import statmanager.UnsupportedStatFormatException;
import util.FilesUtil;
import utils.Functions;

public class ParseTask<T> extends Task <T>{

	private String[] args;
	ArgumentsHolder arg_hld;
	private File jarPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
	private String propertiesPath = jarPath.getParentFile().getAbsolutePath() + "\\conf\\";
	private String resultPath = jarPath.getParentFile().getAbsolutePath() + "\\results\\";
	private FKTProperties context = FKTProperties.getProperties();
	

	public ParseTask(String... args) {
		this.args = args;
		context.jarPath=jarPath;
		context.propertiesPath=propertiesPath;
		context.resultPath=resultPath;
		
		Log.info("Context updated:\n"+context.toString());
	} 

	public ParseTask(ArgumentsHolder args) throws Exception {
		this.arg_hld = args;
		this.args=args.getCMDLine();
		
		context.jarPath=jarPath;
		context.propertiesPath=propertiesPath;
		context.resultPath=resultPath;
		
		Log.info("Context updated:\n"+context.toString());
	} 
	
	@Override
	protected T call() throws Exception {
		// TODO Auto-generated method stub
		CmdLineParser cmdParser = new CmdLineParser();
		JCommander commander = cmdParser.getCommander();

		try {
			

			if (arg_hld.getOut()==null){// if no result file name is provided then generate it
				arg_hld.setOut(Functions.generateFilename("result", "out"));
				args=arg_hld.getCMDLine(); //update command line parameters
			}
			
			//String path = Paths.get("").toAbsolutePath().toString() + "\\results";

			commander.parse(args);
			Command cmd = cmdParser.getCommandObj(commander.getParsedCommand(),context);

			executeParsing((CommandImpl) cmd).getStatData();

			List result = Arrays.asList(
					FilesUtil.read(new FileInputStream((context.resultPath + "\\" + arg_hld.getOut())))
					);

			return (T) result;
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

	private CommandImpl executeParsing(CommandImpl cmd) {
		List<String> l = cmd.getExtensions();
		int size = l.size();
		int i = 1;

		l.forEach(s -> {

			final List<String> path=cmd.getPaths();
			ProcessFilesFabric.create((CommandImpl) cmd, s).start(path);
						
				updateProgress(i + 1, path.size());
				

		});

		return cmd;
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
