package com.billybyte.commonstaticmethods;

import java.lang.reflect.InvocationTargetException;

import org.codehaus.janino.CompileException;
import org.codehaus.janino.ScriptEvaluator;
import org.codehaus.janino.Parser.ParseException;
import org.codehaus.janino.Scanner.ScanException;

public class JaninoStuff {
	 public static Double getDoubleFromExpression(String expression){
		 try {
			ScriptEvaluator se  = new ScriptEvaluator("return "+expression+";",Double.class);
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

}
