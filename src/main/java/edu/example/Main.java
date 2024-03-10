package edu.example;

import edu.spl.MPR;

public class Main {
	public static void main( String[] args ){
		{
			MPR myOne = MPR.M_PI;
			double dOne = myOne.doubleValue();
			long lOne = myOne.longValue();
			System.out.println( "PI: " + myOne + ", double: " + dOne + ", long: " + lOne );
		}
		MPR rand = MPR.randomSeed();
		System.out.println( rand );
		for( int i = 0; i < 5; i++ ){
			rand = MPR.random();
			System.out.println( rand );
		}
		System.out.println( "Saliendo..." );
	}
}