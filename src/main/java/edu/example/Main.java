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
			MPR my = new MPR();
			MPR mpr2 = my;
			my = null;
			System.gc();
		}
		System.out.println( "Wait some seconds..." );

		MPR myOne = MPR.ONE;
		System.out.println( "UNO: " + myOne );

		//waitSeconds( 10 );

		List<Integer> myList = new ArrayList<>();
		for( int i = 0; i < 1000; i++ )	myList.add( i );
		myList = null;

		//waitSeconds( 10 );

		System.out.println( "Saliendo..." );

		BigDecimal bg = BigDecimal.ONE;
	}
}