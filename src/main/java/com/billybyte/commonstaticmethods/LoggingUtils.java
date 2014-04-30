package com.billybyte.commonstaticmethods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.billybyte.commonstaticmethods.resources.ResourcesCommonStaticMethods;



/**
 * General Logging facade that can use any logger, including another logging facade like
 *  slf4j.
 *  
 *  This class behaves in two ways.
 *  
 *  1.  Like a singleton, in that it will only call:
 *  				PropertyConfigurator.configure(p);
 *  				PRINT_ERROR_TO_LOG.set(true);
 *  				Thread.UncaughtExceptionHandler
 *  
 *  JUST ONE TIME.  
 *  
 *  2.  As a logger facade to call a logger (default is log4j).
 *  
 *  
 *  
 * @author bperlman1
 *
 */
public class LoggingUtils {
	public static final String DEFAULT_PROPERTIES_FILENAME = "common_log4j.properties";
	public static final Class<?> DEFAULT_CLASS_IN_PKG_OF_PROP_FILE = ResourcesCommonStaticMethods.class;
	
	private static AtomicReference<PropCong> INSTANCE = new AtomicReference<PropCong>(null);
	static AtomicBoolean PRINT_ERROR_TO_LOG = new AtomicBoolean(false);
	private final Logger logger;
	
	private static class PropCong{
		private PropCong(String propertiesFileNameOrPath,
			Class<?> classInPkgOfPropFile){
			Properties p = new Properties();
			try {
				InputStream is = null;
				if(classInPkgOfPropFile==null){
					is = new FileInputStream(new File(propertiesFileNameOrPath));
					p.load(is) ;
					is.close();
				}else{
					is = Utils.getInputStreamAsResource(
							classInPkgOfPropFile, propertiesFileNameOrPath);
					p.load(is) ;
				}
				PropertyConfigurator.configure(p);
				PRINT_ERROR_TO_LOG.set(true);
				Thread.setDefaultUncaughtExceptionHandler(new UncaughtHandler());
			} catch (IOException e) {
				throw Utils.IllState(e);
			}
		}
	}
	
	
//	private static final String LOG_PROPERTIES_FILE_NAME = "common_log4j.properties";
//	private static final AtomicReference<String> loggingPropertiesFileName =
//			new AtomicReference<String>(LOG_PROPERTIES_FILE_NAME);
//	private static Object loadProperties_Lock = new Object();
//	@SuppressWarnings("unused")
//	private static Properties p = initPropertiesFromResource();
	
	/**
	 * This constructor will set the PropertyConfigurator log4j.properties file only once.
	 * See the constructor LoggingUtils(Class<?> classToLog) for getting a class's logger.
	 * 
	 * @param propertiesFileNameOrPath
	 * @param classInPkgOfPropFile
	 */
	public LoggingUtils(
			String propertiesFileNameOrPath,
			Class<?> classInPkgOfPropFile){
		INSTANCE.compareAndSet(null, INSTANCE.get()==null?new PropCong(propertiesFileNameOrPath, classInPkgOfPropFile):null);
		logger = null;
	}
	
	public LoggingUtils(Class<?> classToLog){
		logger = Logger.getLogger(classToLog);
	}

	public void error(String text){
		if(logger!=null)logger.error(text);
	}

	public void info(String text){
		if(logger!=null)logger.info(text);
	}

	public void fatal(String text){
		if(logger!=null)logger.fatal(text);
	}

	public void debug(String text){
		if(logger!=null && logger.isEnabledFor(org.apache.log4j.Level.DEBUG)){
			logger.debug(text);
		}
	}

	
	public static void logError(Class<?> clazz, String text){
		if(PRINT_ERROR_TO_LOG.get()){
			Logger.getLogger(clazz).error(text);
		}
	}
	
	public static void logFatal(Class<?> clazz,String text){
		if(PRINT_ERROR_TO_LOG.get()){
			Logger.getLogger(clazz).fatal(text);
		}
	}

	/**
	 * Implements Thread.UncaughtExceptionHandler by calling Logger.fatal(text);
	 * @author bperlman1
	 *
	 */
	private static class UncaughtHandler implements
    Thread.UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			if(PRINT_ERROR_TO_LOG.get()){
			    String s =  Utils.stackTraceAsString(e);
				logFatal(Utils.class, s);
			}
		}
		
	}

}
