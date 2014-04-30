package com.billybyte.collectionsstaticmethods.testcases;

import java.util.Map;

import com.billybyte.commonstaticmethods.Utils;

import junit.framework.TestCase;

public class TestUtils extends TestCase{
	public void testPrt(){
		Utils.prt("hello world");
	}
	
	public void testArgPairs(){
		String args[] = {"p11   p12","p21 p22","p31   p32"};
		Map<String,String> argMap = Utils.getArgPairsSeparatedBySpaces(args);
		boolean[] allOk = {true,true,true};
		if(!argMap.containsKey("p11") && argMap.get("p11").compareTo("p12")!=0) allOk[0]=false;
		if(!argMap.containsKey("p21") && argMap.get("p21").compareTo("p22")!=0) allOk[1]=false;
		if(!argMap.containsKey("p31") && argMap.get("p31").compareTo("p32")!=0) allOk[2]=false;
		boolean allGood = true;
		for(int i = 0;i<allOk.length;i++){
			if(allOk[i]){
				Utils.prtObMess(this.getClass(), i+" : "+allOk[i]);
			}else{
				Utils.prtObErrMess(this.getClass(), i+" : "+allOk[i]);
				allGood = false;
			}
		}
		assertEquals(true, allGood);
	}
}
