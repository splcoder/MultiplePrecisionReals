package edu.example;

import edu.spl.MPR;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
	public static void main( String[] args ){
		System.out.println( "Precision: " + MPR.PRECISION );
		{
			MPR myOne = MPR.M_PI;
			double dOne = myOne.doubleValue();
			long lOne = myOne.longValue();
			System.out.println( "PI: " + myOne + ", double: " + dOne + ", long: " + lOne + ", round: " + MPR.round( myOne ) + ", ceil: " + MPR.ceil( myOne ) );
			System.out.println( "< 0 ?: " + myOne.isNeg() );

			myOne = MPR.M_E;
			System.out.println( "e = " + myOne );

			MPR pi2 = myOne.sqr();
			System.out.println( "PI^2: " + pi2 + ", nan ?: " + pi2.isNan() + ", inf ?: " + pi2.isInfP() );

			MPR fromModf[] = MPR.modf( MPR.M_PI );
			System.out.println( "frac: " + fromModf[0] + ", int: " + fromModf[1] );
		}
		MPR rand = MPR.randomSeed();
		System.out.println( rand );
		for( int i = 0; i < 5; i++ ){
			rand = MPR.random();
			System.out.println( rand );
		}

		System.out.println( "Cos(1): " + MPR.cos( 1 ) );

		MPR sc[] = MPR.sinCos( 1. );
		System.out.println( "sin: " + sc[0] + ", cos: " + sc[1] );

		System.out.println( "Ending..." );
	}
}