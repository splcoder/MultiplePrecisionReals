package edu.spl;

/**
 * Multiple Precision in Reals
 *
 * See: https://www.baeldung.com/java-destructor
 */
public class MPR implements AutoCloseable, Comparable<MPR> {
	static {
		//System.loadLibrary( "mpr" );
		System.load("C:\\Users\\Sergio\\Desktop\\Cursos Certificacion\\Curso Java\\Projects\\MultiplePrecisionReals\\build\\libs\\mpr\\shared\\mpr.dll");
	}

	public static final int DIGITS_PRECISION;
	private static native int fixDigitsPrecision( int digits );
	static {
		DIGITS_PRECISION = fixDigitsPrecision( 50 );	// TODO <<< Set here the digits precision required for your project
	}
	//------------------------------------------------------------------------------------------------------------------
	private static native long initConstant( int cte );
	public static final MPR ZERO = new MPR( 0, true );
	public static final MPR ONE = new MPR( 1, true );
	//------------------------------------------------------------------------------------------------------------------
	private static native void freeMPR( long dir );

	// "Pointer" to data
	private final long dir;
	@Override
	public void close() throws Exception {
		freeMPR( dir );
	}
	public MPR(){
		dir = initConstant( 0 );
	}
	private MPR( int cte, boolean init ){
		dir = initConstant( cte );
	}

	@Override
	public int compareTo( MPR o ){
		return 0;	// TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	}
}
