package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.SampleController;

public class Functions {

	private static Logger logger = LoggerFactory.getLogger(Functions.class);
	
	public static String generateFilename(String prefix, String suffix){
		String s=prefix+"_"+(new SimpleDateFormat("YMd_Hmmss_SSS")).format(new Date(System.currentTimeMillis()))+"."+suffix;
		logger.info("Generated filename: "+s);
		return s;
		
	}
}
