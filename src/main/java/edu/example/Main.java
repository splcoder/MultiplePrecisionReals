package edu.example;

import edu.spl.MPR;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void waitSeconds( int seconds ){
		try {
			Thread.sleep(seconds * 1000L );
		} catch( InterruptedException ie ){
			Thread.currentThread().interrupt();
		}
	}
	public static void main( String[] args ){
		{
			MPR myOne = MPR.M_PI;
			double dOne = myOne.doubleValue();
			long lOne = myOne.longValue();
			System.out.println( "PI: " + myOne + ", double: " + dOne + ", long: " + lOne );
		}
		System.gc();

		//waitSeconds( 10 );

		System.out.println( "Saliendo..." );

		BigDecimal bg = BigDecimal.ONE;
	}
}