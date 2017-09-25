package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enums.Commands;
import enums.Format;
import enums.Processor;
import enums.Sampling;
import utils.Functions;

public abstract class ArgumentsHolder {

	protected String cmd = "";
	protected String[] arguments;
	protected String command;
	protected String folder;
	protected String ext;
	protected String processor;
	protected String sample;
	protected String format;
	protected String statfile;
	protected String out;
	
	private Logger logger = LoggerFactory.getLogger(ArgumentsHolder.class);
	
	public ArgumentsHolder(Commands cmd, Processor proc, Sampling sample, Format format, String folder, String ext,
			String statfile, String out) {

		
		command=cmd.toString();
		this.folder=folder;
		this.ext=ext;
		this.processor=proc.toString();
		this.sample=sample.getValue().toString();
		this.format=format.toString();
		this.statfile=statfile;
		this.out=out;
		
		

	}

	public ArgumentsHolder(Commands cmd,  String folder, String ext) {

		command=cmd.toString();
		this.folder=folder;
		this.ext=ext;
		
		arguments = new String[] { cmd.toString(), "-d", folder, "-ext", ext};
		
	}
	
	public abstract String[] getCMDLine() throws Exception;
	
	protected List<String> check(String arg, String val, List<String> t) throws Exception {
	
		if (null!=val&&val.length()!=0){
			t.add(arg);
			t.add(val);

		}else
			throw new Exception("Argument "+arg+" is not defined");
		
		return t;
	
	}
	
	public String getFolder(){return folder;}
	public String getExtention(){return ext;}
	public String getCommand(){return command;}
	public String getOut(){return out;}
	public void setOut(String out){this.out=out;}
	
}

class ArgumentsParseImpl extends ArgumentsHolder{

	private Logger logger = LoggerFactory.getLogger(ArgumentsParseImpl.class);
	
	public ArgumentsParseImpl(Commands cmd, Processor proc, Sampling sample, Format format, String folder, String ext,
			String statfile, String out) {
		super(cmd, proc, sample, format, folder, ext, statfile, out);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String[] getCMDLine() throws Exception {
		List<String> t=new ArrayList<String>();
		
		if (null!=command)t.add(command);
		else throw new Exception("Command is not defined");
		
		t=check("-d",folder,t);
		t=check("-ext",ext,t);
		t=check("-processor", processor, t);
		t=check("-sample", sample,t);
		t=check("-format", format, t);
		t=check("-statfile", statfile, t);
		
		if (out==null||out.length()==0) 
			out=Functions.generateFilename("result_"+format+"_"+sample, "out");
		else
			out=Functions.generateFilename(getOut()+"_"+format+"_"+sample, "out");
		
		t=check("-out",out,t);
		
		
		arguments=t.toArray(new String[t.size()]);
		logger.info(Arrays.toString(arguments));
		
		return arguments;
	}
	
	
}

class ArgumentsPrintImpl extends ArgumentsHolder{

	private Logger logger = LoggerFactory.getLogger(ArgumentsPrintImpl.class);
	
	public ArgumentsPrintImpl(Commands cmd, String folder, String ext) {
		super(cmd, folder, ext);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String[] getCMDLine() throws Exception {
		List<String> t=new ArrayList<String>();
		
		if (null!=command)t.add(command);
		else throw new Exception("Command is not defined");
		
		t=check("-d",folder,t);
		t=check("-ext",ext,t);

		
		arguments = t.toArray(arguments);
		logger.info(Arrays.toString(arguments));
		
		return arguments;
	}

}

