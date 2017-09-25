package tasks;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import cmdline.CmdLineParser;
import cmdline.Command;
import cmdline.CommandImpl;
import cmdline.CommandPrint;
import garbagecleaner.ProcessFiles;
import garbagecleaner.ProcessFilesFabric;
import javafx.concurrent.Task;
import statmanager.UnsupportedStatFormatException;
import util.FilesUtil;
import application.ArgumentsHolder;

public class PrintTask extends Task {

	String[] args;
	String folder_path;
	String ext;
	
	public PrintTask(ArgumentsHolder args) throws Exception{
		
		this.args=args.getCMDLine();
		this.folder_path=args.getFolder();
		this.ext=args.getExtention();
	}
	
	@Override
	protected Map<String,String> call() throws Exception {
		return executeParsing();
		
	}
	

	public Map<String,String> executeParsing() throws UnsupportedStatFormatException{
		
		// TODO Auto-generated method stub
		CmdLineParser cmdParser = new CmdLineParser();
		JCommander commander = cmdParser.getCommander();
		
		try{
		
		commander.parse("print", "-d", folder_path, "-ext", ext);
		Command cmd = cmdParser.getCommandObj(commander.getParsedCommand());

		ProcessFiles pf = ProcessFilesFabric.create((CommandImpl) cmd, ext);
		
		Map<String,String> result=run((CommandImpl) cmd).getStatData();
		
		Map<String,String> map=new LinkedHashMap<String,String>();
		map.put("Found", result.get("Found"));
		
		result.forEach((k,v)->{
			if(StringUtils.isNumeric(k))
				map.put(k, v);
			
		});
		
		
		return map;
		
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
	
	private Map<String,String> sortMap(Map<String,String> map){
		return map.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		
	}
}
