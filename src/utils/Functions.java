package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Functions {

	public static String generateFilename(String prefix, String suffix){
		
		return prefix+"_"+(new SimpleDateFormat("YMd_Hmmss_SSS")).format(new Date(System.currentTimeMillis()))+"."+suffix;
		
	}
}
