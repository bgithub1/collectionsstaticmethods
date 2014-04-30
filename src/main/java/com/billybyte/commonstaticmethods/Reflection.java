package com.billybyte.commonstaticmethods;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import org.codehaus.janino.CompileException;
import org.codehaus.janino.ScriptEvaluator;
import org.codehaus.janino.Parser.ParseException;
import org.codehaus.janino.Scanner.ScanException;



public class Reflection {
	public static Object getFieldByFieldName(Object o,String fieldName,Object...oList) {
		try {
			String getterName = "get"+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1,fieldName.length());
			
			Method getName = o.getClass().getMethod(getterName);
			if(oList!=null && oList.length>0){
				return getName.invoke(o,oList);
			}else{
				return getName.invoke(o);
			}
			
		} catch (Exception e) {
			return null;
		} 		
	}
	
	public static boolean setterMethodExists(Object o,String fieldName,Object...oList){
		String setterName = "set"+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1,fieldName.length());
		
		Class[] parmClasses=new Class[oList.length];
		for(int i=0;i<oList.length;i++){
			parmClasses[i]=oList[i].getClass();
		}
		Method setName;
		try {
			setName = o.getClass().getMethod(setterName,parmClasses);
			return true;
		} catch (Exception e){
			return false;
		} 

	}
	
	public static void setFieldByFieldName(Object o,String fieldName,Object...oList) {
		try {
			String getterName = "set"+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1,fieldName.length());
			
			Class[] parmClasses=new Class[oList.length];
			for(int i=0;i<oList.length;i++){
				parmClasses[i]=oList[i].getClass();
			}
			Method getName;
			getName = o.getClass().getMethod(getterName,parmClasses);
			if(oList!=null && oList.length>0){
				getName.invoke(o,oList);
			}else{
				getName.invoke(o);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 		
	}

	public static Method createGetMethod(Object o,String fieldName) {
		String getterName = "get"+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1,fieldName.length());
		
		Method getName=null;
		try {
			getName = o.getClass().getMethod(getterName);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return getName;

	}


	public static Method createGetMethod(String fieldName,Class c) {
		String getterName = "get"+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1,fieldName.length());
		
		Method getName=null;
		try {
			getName = c.getMethod(getterName);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return getName;

	}
	
	
	public static boolean setFieldByFieldName(boolean onExceptionConvertObjectifiedPrimitivesToPrimitives,Object o,String fieldName,Object...oList) {
		try {
			String getterName = "set"+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1,fieldName.length());
			
			Class[] parmClasses=new Class[oList.length];
			for(int i=0;i<oList.length;i++){
				parmClasses[i]=oList[i].getClass();
			}
			Method getName=null;
			try {
				getName = o.getClass().getMethod(getterName,parmClasses);
			} catch (Exception e) {
				if(onExceptionConvertObjectifiedPrimitivesToPrimitives){
					for(int i = 0;i<oList.length;i++){
						if(Number.class.isAssignableFrom(oList[i].getClass())){
							if(Double.class.isAssignableFrom(oList[i].getClass())){
								oList[i] = (double)((Double)oList[i]).doubleValue();
								parmClasses[i] = double.class;
							}else if(Integer.class.isAssignableFrom(oList[i].getClass())){
								oList[i] = (int)((Integer)oList[i]).intValue();
								parmClasses[i] = int.class;
							}else if(Long.class.isAssignableFrom(oList[i].getClass())){
								oList[i] = (long)((Long)oList[i]).longValue();
								parmClasses[i] = long.class;
							}else if(Short.class.isAssignableFrom(oList[i].getClass())){
								oList[i] = (short)((Short)oList[i]).shortValue();
								parmClasses[i] = short.class;
							}else{
							}
						}else if(Boolean.class.isAssignableFrom(oList[i].getClass())){
							oList[i] = (boolean)((Boolean)oList[i]).booleanValue();
							parmClasses[i] = boolean.class;
						}else{
						}
						
					}
					getName = o.getClass().getMethod(getterName,parmClasses);
				}
			}
			if(oList!=null && oList.length>0){
				getName.invoke(o,oList);
			}else{
				getName.invoke(o);
			}
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 		
	}
	
	
	public static Method createSetMethod(Object o, String fieldName, Object...oList){
		String setterName = "set"+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1,fieldName.length());
		
		Class[] parmClasses=new Class[oList.length];
		for(int i=0;i<oList.length;i++){
			parmClasses[i]=oList[i].getClass();
		}
		Method getName=null;
		try {
			getName = o.getClass().getMethod(setterName,parmClasses);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return getName;
	}

	public static Method createSetMethod(Object o, String fieldName, Class...cList){
		String setterName = "set"+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1,fieldName.length());
		
		Class[] parmClasses=new Class[cList.length];
		for(int i=0;i<cList.length;i++){
			parmClasses[i]=cList[i];
		}
		Method getName=null;
		try {
			getName = o.getClass().getMethod(setterName,parmClasses);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return getName;
	}

	
	public static void setFieldByMethod(Method setterMethod,Object o,Object...oList){
		try {
			if(oList!=null && oList.length>0){
				setterMethod.invoke(o,oList);
			}else{
				setterMethod.invoke(o);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}
	

	public static Object newInstanceFromString(String value, Class<?> objectType){
		// first see if the objectType has a construct from a string
		try {
			Constructor<?> constuctor = objectType.getConstructor(String.class);
			return constuctor.newInstance(value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	public static Object newNoArgInstance(Object objectToBeInstantiated){
		try {
			Constructor<?> constuctor = objectToBeInstantiated.getClass().getConstructor();
			return constuctor.newInstance();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
		
	
	public static Object newClone(Object objectToBeCloned){
		// first see if the objectType has a construct from itself
		try {
			Constructor<?> constuctor = objectToBeCloned.getClass().getConstructor(objectToBeCloned.getClass());
			return constuctor.newInstance(objectToBeCloned);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns a map of methods using the name (String) as key
	 * Allows repeated access to the methods of a class when supplied one example instance
	 * @param instance
	 * @return
	 */
	public static <K> Map<String,Method> getMethodMap(K instance){
		Map<String,Method> methodMap = new HashMap<String,Method>();
		Method[] methods = instance.getClass().getMethods();
		for(Method method:methods){
			methodMap.put(method.getName(), method);
		}
		return methodMap;
	}
	
	public static Process createProcessFromProcessBuilder(
	   		Class klass,
    		String[] vmArgs,
    		String[] args,
    		String workingDirectory) throws IOException,
    		InterruptedException {
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome +
			File.separator + "bin" +
			File.separator + "java";

		String vmArgsString ="";
		for(String arg:vmArgs){
			vmArgsString=vmArgsString+arg+" ";
		}
		String classpath = System.getProperty("java.class.path");
		String className = klass.getCanonicalName();
		List<String> argList = new ArrayList<String>();
		argList.add(javaBin);
		for(String arg:vmArgs){
			argList.add(arg);
		}
//		argList.add(vmArgsString);
		argList.add("-cp");
		argList.add(classpath);
		argList.add(className);
		for(String arg:args){
			argList.add(arg);
		}
		
		ProcessBuilder builder = new ProcessBuilder(argList);
		if(workingDirectory!=null){
			builder.directory(new File(workingDirectory));
		}
		Process process = builder.start();
	    inheritIO(process.getInputStream(), System.out);
	    inheritIO(process.getErrorStream(), System.err);
		
	    return process;
	}
	
	/**
	 * Build a process, execute it, and wait for it to end.
	 *   Also, by default, redirect it's output to it's parent,
	 *   	which is whatever process is calling this method.
	 * @param klass
	 * @param vmArgs
	 * @param args
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
    public static int runProcessFromProcessBuilder(
    		Class klass,
    		String[] vmArgs,
    		String[] args,
    		String workingDirectory) throws IOException,
    		InterruptedException {
    	Process process = 
    			createProcessFromProcessBuilder(klass, vmArgs, args, workingDirectory);
//		String javaHome = System.getProperty("java.home");
//		String javaBin = javaHome +
//			File.separator + "bin" +
//			File.separator + "java";
//
//		String vmArgsString ="";
//		for(String arg:vmArgs){
//			vmArgsString=vmArgsString+arg+" ";
//		}
//		String classpath = System.getProperty("java.class.path");
//		String className = klass.getCanonicalName();
//		List<String> argList = new ArrayList<String>();
//		argList.add(javaBin);
//		for(String arg:vmArgs){
//			argList.add(arg);
//		}
////		argList.add(vmArgsString);
//		argList.add("-cp");
//		argList.add(classpath);
//		argList.add(className);
//		for(String arg:args){
//			argList.add(arg);
//		}
////		ProcessBuilder builder = new ProcessBuilder(
////		javaBin, "-cp", classpath, className);
//		ProcessBuilder builder = new ProcessBuilder(argList);
//		if(workingDirectory!=null){
//			builder.directory(new File(workingDirectory));
//		}
//		Process process = builder.start();
	    inheritIO(process.getInputStream(), System.out);
	    inheritIO(process.getErrorStream(), System.err);
		process.waitFor();
		return process.exitValue();
    }

    /**
     * Launch a process with a countdown latch to signify process has started
     * @param klass
     * @param vmArgs
     * @param args
     * @param workingDirectory
     * @param latch
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static int runProcessFromProcessBuilder(
    		Class klass,
    		String[] vmArgs,
    		String[] args,
    		String workingDirectory,
    		CountDownLatch latch) throws IOException,
    		InterruptedException {
    	Process process = 
    			createProcessFromProcessBuilder(klass, vmArgs, args, workingDirectory);

    	latch.countDown();
    	
	    inheritIO(process.getInputStream(), System.out);
	    inheritIO(process.getErrorStream(), System.err);
		process.waitFor();
		return process.exitValue();
    }

    /**
     * Redirect InputStreams of a child process to the parent, or some other
     *   process.
     * @param src InputStream of process being run
     * @param dest a PrintStream like System.out, of the parent
     */
    private static void inheritIO(final InputStream src, final PrintStream dest) {
        new Thread(new Runnable() {
            public void run() {
                Scanner sc = new Scanner(src);
                while (sc.hasNextLine()) {
                    dest.println(sc.nextLine());
                }
            }
        }).start();
    }
    
    
    
    /**
     * This is a runnable that will call Reflection.runProcessFromProcessBuilder
     * 		asynchronously, when you are starting something like a service
     * 		where you don't want to wait for it's System.exit, in order to 
     * 		return from Reflection.runProcessFromProcessBuilder.
     * @author bperlman1
     *
     */
	public static class ProcessLauncher implements Runnable{
		
		public ProcessLauncher(Class<?> clazz,String[] vmArgs, String[] args,
				String workingDirectory) {
			super();
			this.clazz = clazz;
			this.args = args;
			this.vmArgs = vmArgs;
			this.workingDirectory = workingDirectory;
			this.latch = null;
		}

		public ProcessLauncher(Class<?> clazz,String[] vmArgs, String[] args,
				String workingDirectory, CountDownLatch latch) {
			super();
			this.clazz = clazz;
			this.args = args;
			this.vmArgs = vmArgs;
			this.workingDirectory = workingDirectory;
			this.latch = latch;
		}

		private final Class<?> clazz;
		private final String[] args;
		private final String[] vmArgs;
		private final String workingDirectory;
		private final CountDownLatch latch;
		
		@Override
		public void run() {
			int pResponse;
			try {
				if(latch!=null) {
					pResponse = Reflection.runProcessFromProcessBuilder(clazz, vmArgs, args, workingDirectory, latch);
				} else {
					pResponse = Reflection.runProcessFromProcessBuilder(clazz, vmArgs, args, workingDirectory);
				}
				Utils.prt("process exit code = "+pResponse);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void launchProcess(Class<?> clazz,
			String[] vmArgs,
			String[] args,
			String workingDirectory){
		Thread t = new Thread(new ProcessLauncher(clazz,vmArgs, args,workingDirectory));
		t.start();
	}

	public static Double getDoubleFromExpression(String expression){
		 try {
			ScriptEvaluator se  = new ScriptEvaluator("return 1.0*"+expression+";",Double.class);
			 Double ret = (Double)se.evaluate(new Object[]{});
			 return ret;
		} catch (CompileException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ScanException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		 return null;
	 }

	 public static Integer getIntegerFromExpression(String expression){
		 try {
			ScriptEvaluator se  = new ScriptEvaluator("return "+expression+";",Integer.class);
			 Integer ret = (Integer)se.evaluate(new Object[]{});
			 return ret;
		} catch (CompileException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ScanException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		 return null;
	 }

		/**
		 * Get a Map of methods which have no argument getters
		 *  from a pojo, but passing the pojo and an array of 
		 *   field names as arguments.
		 *   Example: to get the shortName, settle and size of a SettlementDataInteface
		 *     instance:
		 *     	List<Method>  settleMethods = 
		 *     			getMethodList(new SettlementDataImmute(...),new String[]{"shortName","price","size"});
		 * @param pojo
		 * @param propNames
		 * @return
		 */
		public static Map<String,Method> getMethodMapNoArgGetter(Object pojo,String[] propNames){
			Map<String,Method> mList= new HashMap<String, Method>();
			for(String prop:propNames){
				Method m = null;
				try {
					String mName = 
						"get"+prop.substring(0,1).toUpperCase()+
						(prop.length()<2?"":prop.subSequence(1, prop.length()));
					m = 
						pojo.getClass().getMethod(
								mName, new Class[]{});
				} catch (SecurityException e) {
//					e.printStackTrace();
				} catch (NoSuchMethodException e) {
//					e.printStackTrace();
				}
				mList.put(prop,m);
			}
			return mList;

		}

		/**
		 * Get a list of field values from a pojo, via the methods passed to the pojo.
		 * Use this in conjunction with getMethodList(Object o,String[] propNames).
		 * @param pojo
		 * @param mList
		 * @return List<Object>  a list of objects that correspond to the methods in mList
		 */
		public static List<Object> getFieldsFromPojo(Object pojo,List<Method> mList){
			List<Object> ret = new ArrayList<Object>();
			for(Method m : mList){
				Object fieldObject=null;
				if(m==null){
					Utils.prtNoNewLine("null,");
				}else{
					try {
						try {
							fieldObject = m.invoke(pojo, new Object[]{});
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				ret.add(fieldObject);
			}
			return ret;
		}

		/**
		 * Print lists of objects using fieldNames array
		 * @param oList
		 * @param fieldNames - names of fields with no arg getter
		 */
		public static void printListObjects(
				List<Object> oList, 
				String[] fieldNames){
			if(oList==null || oList.size()<1)return;
			Map<String,Method> mList = Reflection.getMethodMapNoArgGetter(oList.get(0),fieldNames);
			for(Object o:oList){
				printObjectFields(o,mList,fieldNames);
			}
		}
		
		/**
		 * Print lall fields in an object using a fieldNames array and a Method map
		 * @param o
		 * @param mMap
		 * @param fieldNames
		 */
		public static void printObjectFields(
				Object o,
				Map<String,Method> mMap,
				String[] fieldNames){
//			
			if(o==null)return;
			for(String fieldName:fieldNames){
				Method m = mMap.get(fieldName);
				if(m==null){
					Utils.prtNoNewLine("null,");
				}
				try {
					Object fieldObject=null;
					try {
						fieldObject = m.invoke(o, new Object[]{});
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					Utils.prtNoNewLine(
							fieldObject==null?"null":fieldObject.toString()+",");
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			Utils.prt(" ");

		}

}
