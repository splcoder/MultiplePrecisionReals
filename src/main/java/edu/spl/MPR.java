package edu.spl;

import java.lang.ref.Cleaner;

/**
 * Multiple Precision in Reals
 *
 * See: https://www.baeldung.com/java-destructor
 * 		https://openjdk.org/jeps/421
 *		https://www.baeldung.com/java-phantom-reference
 */
public class MPR extends Number implements Comparable<MPR>, AutoCloseable {
	static {
		//System.loadLibrary( "mpr" );
		System.load("C:\\Users\\Sergio\\Desktop\\Cursos Certificacion\\Curso Java\\Projects\\MultiplePrecisionReals\\build\\libs\\mpr\\shared\\mpr.dll");
	}
	//------------------------------------------------------------------------------------------------------------------
	// https://www.enyo.de/fw/notes/java-finalization-revisited.html
	// https://docs.oracle.com/javase%2F9%2Fdocs%2Fapi%2F%2F/java/lang/ref/Cleaner.html
	// >>> If the MPR is used in a try-finally block then the close method calls the cleaning action.
	// >>> If the close method is not called, the cleaning action is called by the Cleaner when the MPR instance has become phantom reachable.
	private final static Cleaner cleaner = Cleaner.create();
	private final State state;
	private final Cleaner.Cleanable cleanable;
	private static class State implements Runnable {
		final long handle;
		State( long handle ){ this.handle = handle; }
		public void run(){ MPR.freeMPR( handle ); }		// Deallocate
	}
	@Override
	public void close(){ cleanable.clean(); }			// AutoCloseable
	//------------------------------------------------------------------------------------------------------------------
	public static final int DIGITS_PRECISION;
	private static native int fixDigitsPrecision( int digits );
	static {
		DIGITS_PRECISION = fixDigitsPrecision( 50 );	// TODO <<< Set here the digits precision required for your project
	}
	// Constants -------------------------------------------------------------------------------------------------------
	private static native long initConstant( int cte );
	public static final MPR NAN				= new MPR( -6, true );
	public static final MPR INF_P			= new MPR( -5, true );
	public static final MPR INF_N			= new MPR( -4, true );
	public static final MPR MAX				= new MPR( -3, true );	// largest finite number
	public static final MPR MIN_P			= new MPR( -2, true );	// smallest positive number
	//public static final MPR PRECISION		= new MPR( -1, true );	// TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	public static final MPR ZERO			= new MPR( 0, true );
	public static final MPR ONE				= new MPR( 1, true );
	// Math constants --------------------------------------------------------------------------------------------------
	public static final MPR M_E_M			= new MPR( 2, true );	// Euler–Mascheroni constant (also called Euler's constant)
	public static final MPR M_E				= new MPR( 3, true );
	public static final MPR M_PI			= new MPR( 4, true );
	public static final MPR M_LN2			= new MPR( 5, true );
	public static final MPR M_CAT			= new MPR( 6, true );	// Catalan
	//------------------------------------------------------------------------------------------------------------------
	// "Pointer" to data
	private final long ptr;
	private static native void freeMPR( long ptr );
	// Constructors ----------------------------------------------------------------------------------------------------
	private MPR( long ptr ){
		this.ptr = ptr;

		state = new State( ptr );
		cleanable = cleaner.register( this, state );
	}
	private MPR( int cte, boolean init ){ this( initConstant( cte ) ); }
	private static native long initWithDouble( double value );
	private static native long initWithLong( long value );
	private static native long initWithString( String value );
	private static native long initWithCopy( long ptr );
	public static MPR valueOf( double value ){ return new MPR( initWithDouble( value ) ); }
	public static MPR valueOf( long value ){ return new MPR( initWithLong( value ) ); }
	public static MPR valueOf( String value ){ return new MPR( initWithString( value ) ); }
	public static MPR valueOf( MPR value ){ return new MPR( initWithCopy( value.ptr ) ); }	// Copy

	private static native String toStr( long ptr, int prec );
	@Override
	public String toString(){
		return toStr( this.ptr, DIGITS_PRECISION );
	}

	// Number methods --------------------------------------------------------------------------------------------------
	private static native long toLong( long ptr );
	private static native double toDouble( long ptr );
	@Override
	public int intValue(){ return (int)toLong( ptr ); }

	@Override
	public long longValue(){ return toLong( ptr ); }

	@Override
	public float floatValue(){ return (float)toDouble( ptr ); }

	@Override
	public double doubleValue(){ return toDouble( ptr ); }
	//------------------------------------------------------------------------------------------------------------------
	@Override
	public int compareTo( MPR o ){
		return 0;	// TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	}

	// Fast access functions: + - * / ----------------------------------------------------------------------------------
	private static native long operation( long lPtr, long rPtr, int ope );
	private static native long operation2( long lPtr, double r, int ope );
	public MPR add( MPR v ){ return new MPR( operation( this.ptr, v.ptr, 0 ) ); }
	public MPR add( double v ){ return new MPR( operation2( this.ptr, v, 0 ) ); }
	public MPR sub( MPR v ){ return new MPR( operation( this.ptr, v.ptr, 1 ) ); }
	public MPR sub( double v ){ return new MPR( operation2( this.ptr, v, 1 ) ); }
	public MPR mul( MPR v ){ return new MPR( operation( this.ptr, v.ptr, 2 ) ); }
	public MPR mul( double v ){ return new MPR( operation2( this.ptr, v, 2 ) ); }
	public MPR div( MPR v ){ return new MPR( operation( this.ptr, v.ptr, 3 ) ); }
	public MPR div( double v ){ return new MPR( operation2( this.ptr, v, 3 ) ); }
}
