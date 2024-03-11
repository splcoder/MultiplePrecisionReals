package edu.example;

import edu.spl.MPR;

public class Main {
	public static void main( String[] args ){
		{
			MPR myOne = MPR.M_PI;
			double dOne = myOne.doubleValue();
			long lOne = myOne.longValue();
			System.out.println( "PI: " + myOne + ", double: " + dOne + ", long: " + lOne + ", round: " + MPR.round( myOne ) + ", ceil: " + MPR.ceil( myOne ) );
			System.out.println( "< 0 ?: " + myOne.isNeg() );

			MPR pi2 = myOne.sqr();
			System.out.println( "PI^2: " + pi2 + ", nan ?: " + pi2.isNan() + ", inf ?: " + pi2.isInfP() );
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