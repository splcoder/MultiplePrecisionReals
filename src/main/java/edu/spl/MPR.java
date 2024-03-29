package edu.spl;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Multiple Precision in Reals
 * https://www.mpfr.org/mpfr-current/mpfr.html
 * https://github.com/advanpix/mpreal
 *
 * https://github.com/JuliaLang/julia/issues/35796
 * 			Is libmpfr.a (mingw64) compiled with: --enable-thread-safe		???
 *
 * See: https://www.baeldung.com/java-destructor
 * 		https://openjdk.org/jeps/421
 *		https://www.baeldung.com/java-phantom-reference
 */
public class MPR extends Number implements Comparable<MPR>, AutoCloseable {
	static {
		//System.loadLibrary( "mpr" );		// TODO <<< if it gives you problems then change the next line to your path
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
	public static final MPR PRECISION		= new MPR( -1, true );
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
	public static List<MPR> convert( Collection<Double> values ){
		List<MPR> ret = new ArrayList<>( values.size() );
		for( double v : values )	ret.add( MPR.valueOf( v ) );
		return ret;
	}
	public static List<MPR> convert( List<String> values ){
		List<MPR> ret = new ArrayList<>( values.size() );
		for( String v : values )	ret.add( MPR.valueOf( v ) );
		return ret;
	}
	private static native long random( int seed );
	public static MPR randomSeed( int seed ){ return new MPR( random( seed ) ); }
	public static MPR randomSeed(){ return new MPR( random( (int)System.currentTimeMillis() ) ); }
	public static MPR random(){ return new MPR( random( 0 ) ); }

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
	private static native String toStr( long ptr, int prec );
	@Override
	public String toString(){ return toStr( this.ptr, -1 ); }	// convert with the maximum available digits
	public String toString( int prec ){ return toStr( this.ptr, (prec > DIGITS_PRECISION ? -1 : (prec < -1 ? -1 : prec)) ); }
	private static native boolean areEquals( long lPtr, long rPtr );
	@Override
	public boolean equals( Object o ){
		if( ! (o instanceof MPR) )	return false;
		if( this == o )	return true;
		MPR r = (MPR)o;
		return areEquals( this.ptr, r.ptr );		// Check exact
	}
	private static native int compare( long lPtr, long rPtr );
	@Override
	public int compareTo( MPR o ){
		if( o == null )	return 1;
		return compare( this.ptr, o.ptr );
	}
	@Override
	public int hashCode(){
		int hash = 7;
		hash = 31 * hash + Long.hashCode( ptr );
		hash = 31 * hash + Long.hashCode( ptr );
		return hash;
	}

	// Basic functions -------------------------------------------------------------------------------------------------
	private static native boolean check( long ptr, int ope );
	public boolean isNan(){ return check( ptr, 0 ); }
	public boolean isInf(){ return check( ptr, 1 ); }
	public boolean isInfP(){ return check( ptr, 2 ); }
	public boolean isInfN(){ return check( ptr, 3 ); }
	public boolean isFin(){ return check( ptr, 4 ); }	// is finite
	public boolean isZero(){ return check( ptr, 5 ); }
	public boolean isInt(){ return check( ptr, 6 ); }
	public boolean isNeg(){ return check( ptr, 7 ); }

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
	private static native long operation5( long ptr, int ope );
	private static native long operation6( double value, int ope );
	public MPR neg(){ return new MPR( operation5( ptr, 0 ) ); }
	public MPR sqr(){ return new MPR( operation5( ptr, 1 ) ); }
	private static native int getSign( long ptr );
	public int sign(){ return getSign( ptr ); }

	// Addition, Subtraction, Multiplication, Division -----------------------------------------------------------------
	private static native long operation3( double left, long rPtr, int ope );
	private static native long operation4( double left, double right, int ope );
	public static MPR add( MPR l, MPR r ){			return new MPR( operation( l.ptr, r.ptr, 0 ) ); }
	public static MPR add( MPR l, double r ){		return new MPR( operation2( l.ptr, r,	 0 ) ); }
	public static MPR add( double l, MPR r ){		return new MPR( operation3( l, r.ptr,	 0 ) ); }
	public static MPR add( double l, double r ){	return new MPR( operation4( l, r,		 0 ) ); }
	public static MPR sub( MPR l, MPR r ){			return new MPR( operation( l.ptr, r.ptr, 1 ) ); }
	public static MPR sub( MPR l, double r ){		return new MPR( operation2( l.ptr, r,	 1 ) ); }
	public static MPR sub( double l, MPR r ){		return new MPR( operation3( l, r.ptr,	 1 ) ); }
	public static MPR sub( double l, double r ){	return new MPR( operation4( l, r,		 1 ) ); }
	public static MPR mul( MPR l, MPR r ){			return new MPR( operation( l.ptr, r.ptr, 2 ) ); }
	public static MPR mul( MPR l, double r ){		return new MPR( operation2( l.ptr, r,	 2 ) ); }
	public static MPR mul( double l, MPR r ){		return new MPR( operation3( l, r.ptr,	 2 ) ); }
	public static MPR mul( double l, double r ){	return new MPR( operation4( l, r,		 2 ) ); }
	public static MPR div( MPR l, MPR r ){			return new MPR( operation( l.ptr, r.ptr, 3 ) ); }
	public static MPR div( MPR l, double r ){		return new MPR( operation2( l.ptr, r,	 3 ) ); }
	public static MPR div( double l, MPR r ){		return new MPR( operation3( l, r.ptr,	 3 ) ); }
	public static MPR div( double l, double r ){	return new MPR( operation4( l, r,		 3 ) ); }

	// Rounding functions ----------------------------------------------------------------------------------------------
	public static MPR abs( MPR r ){					return new MPR( operation5( r.ptr,       2 ) ); }
	public static MPR abs( double r ){				return new MPR( operation6( r,     		 2 ) ); }
	public static MPR floor( MPR r ){				return new MPR( operation5( r.ptr,       3 ) ); }
	public static MPR floor( double r ){			return new MPR( operation6( r,     		 3 ) ); }
	public static MPR ceil( MPR r ){				return new MPR( operation5( r.ptr,       4 ) ); }
	public static MPR ceil( double r ){				return new MPR( operation6( r,     		 4 ) ); }
	public static MPR trunc( MPR r ){				return new MPR( operation5( r.ptr,       5 ) ); }
	public static MPR trunc( double r ){			return new MPR( operation6( r,     		 5 ) ); }
	public static MPR round( MPR r ){				return new MPR( operation5( r.ptr,       6 ) ); }
	public static MPR round( double r ){			return new MPR( operation6( r,     		 6 ) ); }

	// Power and Root functions ----------------------------------------------------------------------------------------
	public static MPR hypot( MPR l, MPR r ){		return new MPR( operation( l.ptr, r.ptr, 4 ) ); }
	public static MPR hypot( MPR l, double r ){		return new MPR( operation2( l.ptr, r,    4 ) ); }
	public static MPR hypot( double l, MPR r ){		return new MPR( operation3( l, r.ptr,    4 ) ); }
	public static MPR hypot( double l, double r ){	return new MPR( operation4( l, r,        4 ) ); }
	public static MPR sqrt( MPR r ){				return new MPR( operation5( r.ptr,       7 ) ); }
	public static MPR sqrt( double r ){				return new MPR( operation6( r,    		 7 ) ); }
	public static MPR cbrt( MPR r ){				return new MPR( operation5( r.ptr,       8 ) ); }
	public static MPR cbrt( double r ){				return new MPR( operation6( r,   		 8 ) ); }
	public static MPR root( MPR v, int r ){			return new MPR( operation9( r, v.ptr,	 2 ) ); }	// r > 0 (not checked !!!)
	public static MPR root( double v, int r ){		return new MPR( operation10( r, v,		 2 ) ); }
	public static MPR pow( MPR l, MPR r ){			return new MPR( operation( l.ptr, r.ptr, 5 ) ); }
	public static MPR pow( MPR l, double r ){		return new MPR( operation2( l.ptr, r,    5 ) ); }
	public static MPR pow( double l, MPR r ){		return new MPR( operation3( l, r.ptr,    5 ) ); }
	public static MPR pow( double l, double r ){	return new MPR( operation4( l, r,        5 ) ); }

	// Logarithm and Exponential functions -----------------------------------------------------------------------------
	public static MPR exp( MPR r ){					return new MPR( operation5( r.ptr,       9 ) ); }
	public static MPR exp( double r ){				return new MPR( operation6( r,     		 9 ) ); }
	public static MPR exp2( MPR r ){				return new MPR( operation5( r.ptr,       10 ) ); }		// 2^x
	public static MPR exp2( double r ){				return new MPR( operation6( r,   		 10 ) ); }
	public static MPR exp10( MPR r ){				return new MPR( operation5( r.ptr,       11 ) ); }
	public static MPR exp10( double r ){			return new MPR( operation6( r,  		 11 ) ); }
	// For small magnitude values of x, expm1(x) may be more accurate than exp(x)-1.
	public static MPR expm1( MPR r ){				return new MPR( operation5( r.ptr,       12 ) ); }
	public static MPR expm1( double r ){			return new MPR( operation6( r, 			 12 ) ); }
	public static MPR expW( MPR r ){				return new MPR( operation5( r.ptr,       13 ) ); }		// x*(e^x)
	public static MPR expW( double r ){				return new MPR( operation6( r,			 13 ) ); }
	public static MPR ln( MPR r ){					return new MPR( operation5( r.ptr,       14 ) ); }
	public static MPR ln( double r ){				return new MPR( operation6( r,			 14 ) ); }
	// For small magnitude values of x, ln1p(x) may be more accurate than ln(1+x)
	public static MPR ln1p( MPR r ){				return new MPR( operation5( r.ptr,       15 ) ); }
	public static MPR ln1p( double r ){				return new MPR( operation6( r,			 15 ) ); }
	public static MPR log2( MPR r ){				return new MPR( operation5( r.ptr,       16 ) ); }
	public static MPR log2( double r ){				return new MPR( operation6( r,			 16 ) ); }
	public static MPR log10( MPR r ){				return new MPR( operation5( r.ptr,       17 ) ); }
	public static MPR log10( double r ){			return new MPR( operation6( r,			 17 ) ); }
	// Log in base b = log(l)/log(b)
	public static MPR logn( MPR l, MPR b ){			return new MPR( operation( l.ptr, b.ptr, 6 ) ); }
	public static MPR logn( MPR l, double b ){		return new MPR( operation2( l.ptr, b,    6 ) ); }
	public static MPR logn( double l, MPR b ){		return new MPR( operation3( l, b.ptr,    6 ) ); }
	public static MPR logn( double l, double b ){	return new MPR( operation4( l, b,        6 ) ); }

	// Trigonometric functions -----------------------------------------------------------------------------------------
	public static MPR sin( MPR r ){					return new MPR( operation5( r.ptr,       18 ) ); }
	public static MPR sin( double r ){				return new MPR( operation6( r,			 18 ) ); }
	public static MPR cos( MPR r ){					return new MPR( operation5( r.ptr,       19 ) ); }
	public static MPR cos( double r ){				return new MPR( operation6( r,			 19 ) ); }
	public static MPR tan( MPR r ){					return new MPR( operation5( r.ptr,       20 ) ); }
	public static MPR tan( double r ){				return new MPR( operation6( r,			 20 ) ); }
	public static MPR csc( MPR r ){					return new MPR( operation5( r.ptr,       21 ) ); }
	public static MPR csc( double r ){				return new MPR( operation6( r,			 21 ) ); }
	public static MPR sec( MPR r ){					return new MPR( operation5( r.ptr,       22 ) ); }
	public static MPR sec( double r ){				return new MPR( operation6( r,			 22 ) ); }
	public static MPR cot( MPR r ){					return new MPR( operation5( r.ptr,       23 ) ); }
	public static MPR cot( double r ){				return new MPR( operation6( r,			 23 ) ); }
	private static native long[] operation7( long ptr, int ope );		// Returns an array of 2 R values
	private static native long[] operation8( double value, int ope );	// Returns an array of 2 R values
	public static MPR[] sinCos( MPR r ){
		long[] v = operation7( r.ptr, 0 );
		if( v == null )		throw new RuntimeException( "MPR -> the native array could not be allocated" );
		return new MPR[]{ new MPR( v[0] ), new MPR( v[1] ) };
	}
	public static MPR[] sinCos( double r ){
		long[] v = operation8( r, 0 );
		if( v == null )		throw new RuntimeException( "MPR -> the native array could not be allocated" );
		return new MPR[]{ new MPR( v[0] ), new MPR( v[1] ) };
	}
	public static MPR asin( MPR r ){				return new MPR( operation5( r.ptr,       24 ) ); }
	public static MPR asin( double r ){				return new MPR( operation6( r,			 24 ) ); }
	public static MPR acos( MPR r ){				return new MPR( operation5( r.ptr,       25 ) ); }
	public static MPR acos( double r ){				return new MPR( operation6( r,			 25 ) ); }
	public static MPR atan( MPR r ){				return new MPR( operation5( r.ptr,       26 ) ); }
	public static MPR atan( double r ){				return new MPR( operation6( r,			 26 ) ); }
	public static MPR acsc( MPR r ){				return new MPR( operation5( r.ptr,       27 ) ); }
	public static MPR acsc( double r ){				return new MPR( operation6( r,			 27 ) ); }
	public static MPR asec( MPR r ){				return new MPR( operation5( r.ptr,       28 ) ); }
	public static MPR asec( double r ){				return new MPR( operation6( r,			 28 ) ); }
	public static MPR acot( MPR r ){				return new MPR( operation5( r.ptr,       29 ) ); }
	public static MPR acot( double r ){				return new MPR( operation6( r,			 29 ) ); }
	public static MPR atan2( MPR l, MPR b ){		return new MPR( operation( l.ptr, b.ptr, 7 ) ); }
	public static MPR atan2( MPR l, double b ){		return new MPR( operation2( l.ptr, b,    7 ) ); }
	public static MPR atan2( double l, MPR b ){		return new MPR( operation3( l, b.ptr,    7 ) ); }
	public static MPR atan2( double l, double b ){	return new MPR( operation4( l, b,        7 ) ); }

	// Hyperbolic functions --------------------------------------------------------------------------------------------
	public static MPR sinh( MPR r ){				return new MPR( operation5( r.ptr,       30 ) ); }
	public static MPR sinh( double r ){				return new MPR( operation6( r,			 30 ) ); }
	public static MPR cosh( MPR r ){				return new MPR( operation5( r.ptr,       31 ) ); }
	public static MPR cosh( double r ){				return new MPR( operation6( r,			 31 ) ); }
	public static MPR tanh( MPR r ){				return new MPR( operation5( r.ptr,       32 ) ); }
	public static MPR tanh( double r ){				return new MPR( operation6( r,			 32 ) ); }
	public static MPR csch( MPR r ){				return new MPR( operation5( r.ptr,       33 ) ); }
	public static MPR csch( double r ){				return new MPR( operation6( r,			 33 ) ); }
	public static MPR sech( MPR r ){				return new MPR( operation5( r.ptr,       34 ) ); }
	public static MPR sech( double r ){				return new MPR( operation6( r,			 34 ) ); }
	public static MPR coth( MPR r ){				return new MPR( operation5( r.ptr,       35 ) ); }
	public static MPR coth( double r ){				return new MPR( operation6( r,			 35 ) ); }
	public static MPR[] sinhCosh( MPR r ){
		long[] v = operation7( r.ptr, 1 );
		if( v == null )		throw new RuntimeException( "MPR -> the native array could not be allocated" );
		return new MPR[]{ new MPR( v[0] ), new MPR( v[1] ) };
	}
	public static MPR[] sinhCosh( double r ){
		long[] v = operation8( r, 1 );
		if( v == null )		throw new RuntimeException( "MPR -> the native array could not be allocated" );
		return new MPR[]{ new MPR( v[0] ), new MPR( v[1] ) };
	}
	public static MPR asinh( MPR r ){				return new MPR( operation5( r.ptr,       36 ) ); }
	public static MPR asinh( double r ){			return new MPR( operation6( r,			 36 ) ); }
	public static MPR acosh( MPR r ){				return new MPR( operation5( r.ptr,       37 ) ); }
	public static MPR acosh( double r ){			return new MPR( operation6( r,			 37 ) ); }
	public static MPR atanh( MPR r ){				return new MPR( operation5( r.ptr,       38 ) ); }
	public static MPR atanh( double r ){			return new MPR( operation6( r,			 38 ) ); }
	public static MPR acsch( MPR r ){				return new MPR( operation5( r.ptr,       39 ) ); }
	public static MPR acsch( double r ){			return new MPR( operation6( r,			 39 ) ); }
	public static MPR asech( MPR r ){				return new MPR( operation5( r.ptr,       40 ) ); }
	public static MPR asech( double r ){			return new MPR( operation6( r,			 40 ) ); }
	public static MPR acoth( MPR r ){				return new MPR( operation5( r.ptr,       41 ) ); }
	public static MPR acoth( double r ){			return new MPR( operation6( r,			 41 ) ); }

	// Mod, Max and Min functions --------------------------------------------------------------------------------------
	public static MPR max( MPR l, MPR b ){			return new MPR( operation( l.ptr, b.ptr, 8 ) ); }
	public static MPR max( MPR l, double b ){		return new MPR( operation2( l.ptr, b,    8 ) ); }
	public static MPR max( double l, MPR b ){		return new MPR( operation3( l, b.ptr,    8 ) ); }
	public static MPR max( double l, double b ){	return new MPR( operation4( l, b,        8 ) ); }
	public static MPR min( MPR l, MPR b ){			return new MPR( operation( l.ptr, b.ptr, 9 ) ); }
	public static MPR min( MPR l, double b ){		return new MPR( operation2( l.ptr, b,    9 ) ); }
	public static MPR min( double l, MPR b ){		return new MPR( operation3( l, b.ptr,    9 ) ); }
	public static MPR min( double l, double b ){	return new MPR( operation4( l, b,        9 ) ); }
	public static MPR fmod( MPR l, MPR b ){			return new MPR( operation( l.ptr, b.ptr, 10 ) ); }
	public static MPR fmod( MPR l, double b ){		return new MPR( operation2( l.ptr, b,    10 ) ); }
	public static MPR fmod( double l, MPR b ){		return new MPR( operation3( l, b.ptr,    10 ) ); }
	public static MPR fmod( double l, double b ){	return new MPR( operation4( l, b,        10 ) ); }
	public static MPR rem( MPR l, MPR b ){			return new MPR( operation( l.ptr, b.ptr, 11 ) ); }	// Remainder after division
	public static MPR rem( MPR l, double b ){		return new MPR( operation2( l.ptr, b,    11 ) ); }
	public static MPR rem( double l, MPR b ){		return new MPR( operation3( l, b.ptr,    11 ) ); }
	public static MPR rem( double l, double b ){	return new MPR( operation4( l, b,        11 ) ); }
	public static MPR mod( MPR l, MPR b ){			return new MPR( operation( l.ptr, b.ptr, 12 ) ); }	// Modulus after division
	public static MPR mod( MPR l, double b ){		return new MPR( operation2( l.ptr, b,    12 ) ); }
	public static MPR mod( double l, MPR b ){		return new MPR( operation3( l, b.ptr,    12 ) ); }
	public static MPR mod( double l, double b ){	return new MPR( operation4( l, b,        12 ) ); }
	public static MPR[] modf( MPR r ){    // decompose the floating-point number: [fractpart, intpart]
		long[] v = operation7( r.ptr, 2 );
		if( v == null )		throw new RuntimeException( "MPR -> the native array could not be allocated" );
		return new MPR[]{ new MPR( v[0] ), new MPR( v[1] ) };
	}
	public static MPR[] modf( double r ){
		long[] v = operation8( r, 2 );
		if( v == null )		throw new RuntimeException( "MPR -> the native array could not be allocated" );
		return new MPR[]{ new MPR( v[0] ), new MPR( v[1] ) };
	}

	// Bessel functions ------------------------------------------------------------------------------------------------
	public static MPR besselj0( MPR r ){			return new MPR( operation5( r.ptr,       42 ) ); }
	public static MPR besselj0( double r ){			return new MPR( operation6( r,			 42 ) ); }
	public static MPR besselj1( MPR r ){			return new MPR( operation5( r.ptr,       43 ) ); }
	public static MPR besselj1( double r ){			return new MPR( operation6( r,			 43 ) ); }
	private static native long operation9( int order, long ptr, int ope );
	private static native long operation10( int order, double value, int ope );
	// Bessel function of the first kind, n-th order
	public static MPR besseljn( int order, MPR r ){		return new MPR( operation9( order, r.ptr, 0 ) ); }
	public static MPR besseljn( int order, double r ){	return new MPR( operation10( order, r,	  0 ) ); }
	public static MPR bessely0( MPR r ){			return new MPR( operation5( r.ptr,       44 ) ); }
	public static MPR bessely0( double r ){			return new MPR( operation6( r,			 44 ) ); }
	public static MPR bessely1( MPR r ){			return new MPR( operation5( r.ptr,       45 ) ); }
	public static MPR bessely1( double r ){			return new MPR( operation6( r,			 45 ) ); }
	// Bessel function of the second kind, n-th order
	public static MPR besselyn( int order, MPR r ){		return new MPR( operation9( order, r.ptr, 1 ) ); }
	public static MPR besselyn( int order, double r ){	return new MPR( operation10( order, r,	  1 ) ); }
	// Other functions -------------------------------------------------------------------------------------------------
	public static MPR erf( MPR r ){					return new MPR( operation5( r.ptr,       46 ) ); }
	public static MPR erf( double r ){				return new MPR( operation6( r,			 46 ) ); }
	public static MPR erfc( MPR r ){				return new MPR( operation5( r.ptr,       47 ) ); }
	public static MPR erfc( double r ){				return new MPR( operation6( r,			 47 ) ); }
	public static MPR lgamma( MPR r ){				return new MPR( operation5( r.ptr,       48 ) ); }	// logarithmic gamma function
	public static MPR lgamma( double r ){			return new MPR( operation6( r,			 48 ) ); }
	public static MPR tgamma( MPR r ){				return new MPR( operation5( r.ptr,       49 ) ); }	// true gamma function
	public static MPR tgamma( double r ){			return new MPR( operation6( r,			 49 ) ); }
	public static MPR lngamma( MPR r ){				return new MPR( operation5( r.ptr,       50 ) ); }
	public static MPR lngamma( double r ){			return new MPR( operation6( r,			 50 ) ); }
	public static MPR gamma( MPR r ){				return new MPR( operation5( r.ptr,       51 ) ); }
	public static MPR gamma( double r ){			return new MPR( operation6( r,			 51 ) ); }
	public static MPR digamma( MPR r ){				return new MPR( operation5( r.ptr,       52 ) ); }
	public static MPR digamma( double r ){			return new MPR( operation6( r,			 52 ) ); }
	public static MPR zeta( MPR r ){				return new MPR( operation5( r.ptr,       53 ) ); }	// riemann zeta
	public static MPR zeta( double r ){				return new MPR( operation6( r,			 53 ) ); }
	public static MPR ai( MPR r ){					return new MPR( operation5( r.ptr,       54 ) ); }
	public static MPR ai( double r ){				return new MPR( operation6( r,			 54 ) ); }
	public static MPR li2( MPR r ){					return new MPR( operation5( r.ptr,       55 ) ); }
	public static MPR li2( double r ){				return new MPR( operation6( r,			 55 ) ); }
	// Stat functions --------------------------------------------------------------------------------------------------
	public static MPR sum( MPR... list ){
		MPR res = ZERO;
		for( int i = 0; i < list.length; i++ )	res = res.add( list[ i ] );
		return res;
	}
	public static MPR sum( double... list ){
		MPR res = ZERO;
		for( int i = 0; i < list.length; i++ )	res = res.add( list[ i ] );
		return res;
	}
	public static MPR sum( Collection<MPR> col ){
		MPR res = ZERO;
		if( ! col.isEmpty() ){
			for( MPR r : col )	res = res.add( r );
		}
		return res;
	}
	public static MPR sum( Stream<MPR> stream, boolean parallel ){ return parallel ? stream.parallel().reduce( ZERO, ( a, b) -> a.add( b ) ) : stream.reduce( ZERO, ( a, b) -> a.add( b ) ); }
	public static MPR sum( Stream<MPR> stream ){ return sum( stream, false ); }
	public static MPR product( MPR... list ){
		MPR res = ONE;
		for( int i = 0; i < list.length; i++ )	res = res.mul( list[ i ] );
		return res;
	}
	public static MPR product( double... list ){
		MPR res = ONE;
		for( int i = 0; i < list.length; i++ )	res = res.mul( list[ i ] );
		return res;
	}
	public static MPR product( Collection<MPR> col ){
		MPR res = ONE;
		if( ! col.isEmpty() ){
			for( MPR r : col )	res = res.mul( r );
		}
		return res;
	}
	public static MPR product( Stream<MPR> stream, boolean parallel ){ return parallel ? stream.parallel().reduce( ONE, (a, b) -> a.mul( b ) ) : stream.reduce( ONE, (a, b) -> a.mul( b ) ); }
	public static MPR product( Stream<MPR> stream ){ return product( stream, false ); }
	public static MPR max( MPR... list ){
		if( list.length == 0 )	return MPR.INF_N;
		MPR res = list[0];
		for( int i = 1; i < list.length; i++ )	res = MPR.max( res, list[ i ] );
		return res;
	}
	public static MPR max( double... list ){
		if( list.length == 0 )	return MPR.INF_N;
		MPR res = MPR.valueOf( list[0] );
		for( int i = 1; i < list.length; i++ )	res = MPR.max( res, list[ i ] );
		return res;
	}
	public static MPR max( Collection<MPR> col ){
		if( col.isEmpty() )	return MPR.INF_N;
		MPR res = MPR.INF_N;
		for( MPR r : col )	res = MPR.max( res, r );
		return res;
	}
	public static MPR max( Stream<MPR> stream, boolean parallel ){ return parallel ? stream.parallel().reduce( MPR.INF_N, MPR::max ) : stream.reduce( MPR.INF_N, MPR::max ); }
	public static MPR max( Stream<MPR> stream ){ return max( stream, false ); }
	public static MPR min( MPR... list ){
		if( list.length == 0 )	return MPR.INF_P;
		MPR res = list[0];
		for( int i = 1; i < list.length; i++ )	res = MPR.min( res, list[ i ] );
		return res;
	}
	public static MPR min( double... list ){
		if( list.length == 0 )	return MPR.INF_P;
		MPR res = MPR.valueOf( list[0] );
		for( int i = 1; i < list.length; i++ )	res = MPR.min( res, list[ i ] );
		return res;
	}
	public static MPR min( Collection<MPR> col ){
		if( col.isEmpty() )	return MPR.INF_P;
		MPR res = MPR.INF_P;
		for( MPR r : col )	res = MPR.min( res, r );
		return res;
	}
	public static MPR min( Stream<MPR> stream, boolean parallel ){ return parallel ? stream.parallel().reduce( MPR.INF_P, MPR::min ) : stream.reduce( MPR.INF_P, MPR::min ); }
	public static MPR min( Stream<MPR> stream ){ return min( stream, false ); }
	public static MPR[] minMax( MPR... list ){
		if( list.length == 0 )	return new MPR[]{ MPR.INF_P, MPR.INF_N };
		MPR resMin = list[0], resMax = list[0];
		for( int i = 1; i < list.length; i++ ){
			resMin = MPR.min( resMin, list[ i ] );
			resMax = MPR.max( resMax, list[ i ] );
		}
		return new MPR[]{ resMin, resMax };
	}
	public static MPR[] minMax( double... list ){
		if( list.length == 0 )	return new MPR[]{ MPR.INF_P, MPR.INF_N };
		MPR first = MPR.valueOf( list[0] );
		MPR resMin = first, resMax = first;
		for( int i = 1; i < list.length; i++ ){
			resMin = MPR.min( resMin, list[ i ] );
			resMax = MPR.max( resMax, list[ i ] );
		}
		return new MPR[]{ resMin, resMax };
	}
	public static MPR[] minMax( Collection<MPR> col ){
		if( col.isEmpty() )	return new MPR[]{ MPR.INF_P, MPR.INF_N };
		MPR resMin = MPR.INF_P, resMax = MPR.INF_N;
		for( MPR r : col ){
			resMin = MPR.min( resMin, r );
			resMax = MPR.max( resMax, r );
		}
		return new MPR[]{ resMin, resMax };
	}
	public static MPR[] minMax( Stream<MPR> stream, boolean parallel ){
		return parallel ?
			stream.parallel().reduce( new MPR[]{ MPR.INF_P, MPR.INF_N }					// Identity (initial value)
				, (a, r) -> new MPR[]{ MPR.min( a[0], r ), MPR.max( a[1], r ) }			// Accumulator: a = array, r = Stream value
				, (c, d) -> new MPR[]{ MPR.min( c[0], d[0] ), MPR.max( c[1], d[1] ) }	// Combiner for parallelization
			)
			:
			stream.reduce( new MPR[]{ MPR.INF_P, MPR.INF_N }							// Identity (initial value)
				, (a, r) -> new MPR[]{ MPR.min( a[0], r ), MPR.max( a[1], r ) }			// Accumulator: a = array, r = Stream value
				, (c, d) -> new MPR[]{ MPR.min( c[0], d[0] ), MPR.max( c[1], d[1] ) }	// Combiner for parallelization
		);
	}
	public static MPR[] minMax( Stream<MPR> stream ){ return minMax( stream, false ); }
	public static MPR mean( MPR... list ){
		if( list.length == 0 )	return ZERO;
		MPR res = list[0];
		for( int i = 1; i < list.length; i++ )	res = res.add( list[ i ] );
		return res.div( list.length );
	}
	public static MPR mean( double... list ){
		if( list.length == 0 )	return ZERO;
		MPR res = MPR.valueOf( list[0] );
		for( int i = 1; i < list.length; i++ )	res = res.add( list[ i ] );
		return res.div( list.length );
	}
	public static MPR mean( Collection<MPR> col ){
		if( col.isEmpty() )	return ZERO;
		MPR res = ZERO;
		for( MPR r : col )	res = res.add( r );
		return res.div( col.size() );
	}
	public static MPR mean( Stream<MPR> stream ){	// TODO improve por parallel
		MPR sum = ZERO;
		long counter = 0;
		for( MPR r : (Iterable<MPR>)stream::iterator ){
			sum.add( r );
			counter++;
		}
		return counter > 0 ? sum.div( counter ) : ZERO;
	}
	public static MPR sd( boolean sample, MPR mean, MPR... list ){
		if( list.length < 2 )	return ZERO;
		MPR sum = ZERO;
		for( int i = 0; i < list.length; i++ )	sum = sum.add( list[ i ].sub( mean ).sqr() );
		return sample ? MPR.sqrt( sum.div( list.length - 1 ) ) : MPR.sqrt( sum.div( list.length ) );
	}
	public static MPR sd( MPR mean, MPR... list ){ return sd( false, mean, list ); }
	public static MPR sd( boolean sample, double mean, double... list ){
		if( list.length < 2 )	return ZERO;
		MPR sum = ZERO;
		for( int i = 0; i < list.length; i++ )	sum = sum.add( MPR.valueOf( list[ i ] ).sub( mean ).sqr() );
		return sample ? MPR.sqrt( sum.div( list.length - 1 ) ) : MPR.sqrt( sum.div( list.length ) );
	}
	public static MPR sd( double mean, double... list ){ return sd( false, mean, list ); }
	public static MPR sd( boolean sample, MPR mean, Collection<MPR> col ){
		if( col.size() < 2 )	return ZERO;
		MPR sum = ZERO;
		for( MPR r : col )	sum = sum.add( r.sub( mean ).sqr() );
		return sample ? MPR.sqrt( sum.div( col.size() - 1 ) ) : MPR.sqrt( sum.div( col.size() ) );
	}
	public static MPR sd( MPR mean, Collection<MPR> col ){ return sd( false, mean, col ); }
	public static MPR sd( boolean sample, MPR mean, Stream<MPR> stream ){	// TODO improve for parallel
		MPR sum = ZERO;
		long counter = 0;
		for( MPR r : (Iterable<MPR>)stream::iterator ){
			sum.add( r.sub( mean ).sqr() );
			counter++;
		}
		if( counter < 2 )	return ZERO;
		return sample ? MPR.sqrt( sum.div( counter - 1 ) ) : MPR.sqrt( sum.div( counter ) );
	}
	public static MPR sd( MPR mean, Stream<MPR> stream ){ return sd( false, mean, stream ); }
	public static MPR[] meanSD( boolean sample, MPR... list ){
		if( list.length == 0 )	return new MPR[]{ MPR.NAN, MPR.NAN };
		if( list.length == 1 )	return new MPR[]{ list[0], ZERO };
		MPR aux, s = ZERO, s2 = ZERO;
		for( int i = 0; i < list.length; i++ ){
			aux = list[ i ];
			s = s.add( aux );
			s2 = s2.add( aux.sqr() );
		}
		MPR mean = s.div( list.length );
		MPR variance = s2.div( (sample ? list.length - 1 : list.length) ).sub( mean.sqr() );
		return new MPR[]{ mean, MPR.sqrt( variance ) };
	}
	public static MPR[] meanSD( MPR... list ){ return meanSD( false, list ); }
	public static MPR[] meanSD( boolean sample, double... list ){
		if( list.length == 0 )	return new MPR[]{ MPR.NAN, MPR.NAN };
		if( list.length == 1 )	return new MPR[]{ MPR.valueOf( list[0] ), ZERO };
		MPR aux, s = ZERO, s2 = ZERO;
		for( int i = 0; i < list.length; i++ ){
			aux = MPR.valueOf( list[ i ] );
			s = s.add( aux );
			s2 = s2.add( aux.sqr() );
		}
		MPR mean = s.div( list.length );
		MPR variance = s2.div( (sample ? list.length - 1 : list.length) ).sub( mean.sqr() );
		return new MPR[]{ mean, MPR.sqrt( variance ) };
	}
	public static MPR[] meanSD( double... list ){ return meanSD( false, list ); }
	public static MPR[] meanSD( boolean sample, Collection<MPR> col ){
		if( col.isEmpty() )	return new MPR[]{ MPR.NAN, MPR.NAN };
		int length = col.size();
		if( length == 1 ){
			MPR inside = MPR.NAN;
			for( MPR r : col )	inside = r;
			return new MPR[]{ inside, ZERO };
		}
		MPR s = ZERO, s2 = ZERO;
		for( MPR r : col ){
			s = s.add( r );
			s2 = s2.add( r.sqr() );
		}
		MPR mean = s.div( length );
		MPR variance = s2.div( (sample ? length - 1 : length) ).sub( mean.sqr() );
		return new MPR[]{ mean, MPR.sqrt( variance ) };
	}
	public static MPR[] meanSD( Collection<MPR> col ){ return meanSD( false, col ); }
	public static class SummaryStatistics {
		private long count;
		private MPR sum, sumSquared, min, max;

		// Helpers
		private MPR mean = null, variance = null, varianceSample = null;

		public SummaryStatistics(){
			count		= 0;
			sum			= ZERO;
			sumSquared	= ZERO;
			min			= INF_P;
			max			= INF_N;
		}
		public void accept( MPR value ){
			count++;
			sum			= sum.add( value );
			sumSquared	= sumSquared.add( value.sqr() );
			min			= MPR.min( min, value );
			max			= MPR.max( max, value );
		}
		public SummaryStatistics combine( SummaryStatistics other ){
			count		+= other.count;
			sum			= sum.add( other.sum );
			sumSquared	= sumSquared.add( other.sumSquared );
			min			= MPR.min( min, other.min );
			max			= MPR.max( max, other.max );
			return this;
		}
		public long getCount(){ return count; }
		public MPR getSum(){ return sum; }
		public MPR getSumSquared(){ return sumSquared; }
		public MPR getMin(){ return min; }
		public MPR getMax(){ return max; }
		public MPR getMean(){
			if( mean == null ){
				mean = count == 0 ? ZERO : sum.div( count );
			}
			return mean;
		}
		public MPR getVariance( boolean sample ){
			if( sample ){
				if( varianceSample == null ){
					if( count < 2 )	varianceSample = ZERO;
					else{
						MPR _mean = getMean();
						varianceSample = sumSquared.div( count - 1 ).sub( _mean.sqr() );
					}
				}
				return varianceSample;
			}
			if( variance == null ){
				if( count < 2 )	variance = ZERO;
				else{
					MPR _mean = getMean();
					variance = sumSquared.div( count ).sub( _mean.sqr() );
				}
			}
			return variance;
		}
		public MPR getVariance(){ return getVariance( false ); }
		public MPR getSD( boolean sample ){ return MPR.sqrt( getVariance( sample ) ); }
		public MPR getSD(){ return MPR.sqrt( getVariance( false ) ); }
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append( "Count: " ).append( this.getCount() );
			sb.append( ", Sum: " ).append( this.getSum() );
			sb.append( ", Sum^2: " ).append( this.getSumSquared() );
			sb.append( ", Min: " ).append( this.getMin() );
			sb.append( ", Max: " ).append( this.getMax() );
			sb.append( ", Mean: " ).append( this.getMean() );
			sb.append( ", Variance: " ).append( this.getVariance() );
			sb.append( ", SD: " ).append( this.getSD() );
			return sb.toString();
		}
	}
	public static MPR[] meanSD( boolean sample, Stream<MPR> stream, boolean parallel ){
		SummaryStatistics accumulator = parallel ?
			stream.parallel().collect(
				SummaryStatistics::new
				, SummaryStatistics::accept
				, SummaryStatistics::combine
			)
			:
			stream.collect(
				SummaryStatistics::new
				, SummaryStatistics::accept
				, SummaryStatistics::combine
		);
		MPR mean	= accumulator.getMean();
		MPR sd		= accumulator.getSD( sample );
		return new MPR[]{ mean, sd };
	}
	public static MPR[] meanSD( boolean sample, Stream<MPR> stream ){ return meanSD( sample, stream, false ); }
	public static MPR[] meanSD( Stream<MPR> stream, boolean parallel ){ return meanSD( false, stream, parallel ); }
	public static MPR[] meanSD( Stream<MPR> stream ){ return meanSD( false, stream, false ); }
	public static SummaryStatistics getStatistics( Stream<MPR> stream, boolean parallel ){
		return parallel ?
			stream.parallel().collect(
				SummaryStatistics::new
				, SummaryStatistics::accept
				, SummaryStatistics::combine
			)
			:
			stream.collect(
				SummaryStatistics::new
				, SummaryStatistics::accept
				, SummaryStatistics::combine
		);
	}
	public static SummaryStatistics getStatistics( Stream<MPR> stream ){ return getStatistics( stream, false ); }
}
