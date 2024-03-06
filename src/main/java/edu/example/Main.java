package edu.example;

import edu.spl.MPR;

public class Main {
	public static void main( String[] args ){
		System.out.printf( "Hello and welcome!" );

		for( int i = 1; i <= 5; i++ ){
			System.out.println( "i = " + i );
		}

		MPR.print();
	}
}