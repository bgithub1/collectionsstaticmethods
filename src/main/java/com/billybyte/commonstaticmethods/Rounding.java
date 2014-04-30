package com.billybyte.commonstaticmethods;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Rounding {
	public static double roundToFloor(double number,int digits){
		return new BigDecimal(number).setScale(digits,RoundingMode.FLOOR).doubleValue();
	}
	
	public static double roundToCeiling(double number,int digits){
		return new BigDecimal(number).setScale(digits,RoundingMode.CEILING).doubleValue();
	}
	
	public static double round_by_xs_integer(double number,int rounder){
		int i;
		long j,k;
		j= Math.round(number / rounder);
		k = j * rounder;
		i = (int)Math.round(number - k);
		if(new Integer(i).doubleValue()<=number/2.0){
			return new Long(k).doubleValue();
		}else{
			return new Long(k+rounder).doubleValue();
		}
	}
	
	public static BigDecimal round_by_xs_decimal(double number,double rounder,int sig_digits){
		double x;
		double multiplier;
		multiplier = Math.pow(10.0, sig_digits);
		x = round_by_xs_integer(number * multiplier, new Double(rounder * multiplier).intValue());
		return new BigDecimal(x / multiplier).setScale(sig_digits,RoundingMode.HALF_EVEN);
	}
	
	
	 static int noofdigits(int number){
	     
	     long decimal=1;
	     int count=0;
	     
	     while(true){
	       
	       if(number % decimal == number){
	           
	        return count;   
	       }
	         decimal*=10;
	         count++; 
	         
	     }     
	    
	 }
	 
	 static final BigDecimal multiplier=new BigDecimal("100000000");
	 public static int leastSignificantDigit(BigDecimal number){
		 return 9-leastsignificantdigit(number.multiply(multiplier).longValue());
	 }
	 
	static Integer leastsignificantdigit(long number){   
		int count = 0;
		
		for(int i=0;i<20;i++){
			int mod = (int)(number % (int)Math.pow(10,count));
			if(mod!=0)return  count;
			count++;
		}
		return null;
//	    if (number < 0 ) return ((-1) * number) % 10;
//	    else return number % 10;
	}
	 
	static int mostsignificantdigit(int number){
	    
	    long decimal=1;
	    int msd=0;

	    if(number != 0)
	    while(true){
	       
	        msd =  (int)(( number - number % decimal ) / decimal );
	        if( noofdigits( msd ) == 1 ){
	            if(msd < 0) return msd * (-1);
	            else return msd;
	        }
	        decimal*=10;
	    }
	    else return 0;
	    
	}



}
