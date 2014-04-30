package com.billybyte.commonstaticmethods;

import java.math.BigDecimal;
import java.util.TreeMap;

public class InterestRates {
	public static double interpolateLinear(double keyToInterpolate,
			double firstKey,double firstValue,
			double secondKey, double secondValue){
		return (keyToInterpolate-firstKey)/(secondKey-firstKey) *(secondValue-firstValue) + firstValue;
	}

	public static double interpolateLinearFromRateTable(TreeMap<Integer, BigDecimal> rateTable,
			int rateTermInDays){
		if(rateTable.containsKey(rateTermInDays)){
			return rateTable.get(rateTermInDays).doubleValue();
		}
		int firstKey;
		double firstValue;
		int secondKey;
		double secondValue;
		if(rateTermInDays<rateTable.firstKey()){
			firstKey = rateTable.firstKey();
			firstValue = rateTable.get(firstKey).doubleValue();
			secondKey = rateTable.higherKey(firstKey);
			secondValue = rateTable.get(secondKey).doubleValue();
		}else{
			if(rateTermInDays>rateTable.lastKey()){
				firstKey = rateTable.lowerKey(rateTable.lastKey());
				firstValue = rateTable.get(firstKey).doubleValue();
				secondKey = rateTable.lastKey();
				secondValue = rateTable.get(secondKey).doubleValue();
			}else{
				firstKey = rateTable.floorKey(rateTermInDays);
				firstValue = rateTable.get(firstKey).doubleValue();
				secondKey = rateTable.higherKey(firstKey);
				secondValue = rateTable.get(secondKey).doubleValue();
			}
		}
		double interpolatedValue = interpolateLinear(rateTermInDays, firstKey, firstValue, secondKey, secondValue);
		return interpolatedValue;
	}
}
