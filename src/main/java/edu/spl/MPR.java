package edu.spl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Multiple Precision in Reals
 *
 * See: https://www.baeldung.com/java-destructor
 * 		https://openjdk.org/jeps/421
 *		https://www.baeldung.com/java-phantom-reference
 */
public class MPR extends Number implements Comparable<MPR> {
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
	// "Pointer" to data
	private final long ptr;
	private static native void freeMPR( long ptr );
	private void freeMPR(){
		System.out.println( "This MPR object is being deleted..." );	// TODO <<<<<<<<<<<<<<<<<<<<<
		try{
			BufferedWriter writer = new BufferedWriter( new FileWriter( "MPRoutFile.txt", true ) );
			writer.append( '\n' );
			writer.append( "This MPR object is being deleted..." );
			writer.close();
		}
		catch( Exception e ){}
		freeMPR( ptr );
	}
	//------------------------------------------------------------------------------------------------------------------
	private static class MPRPhantomReference extends PhantomReference<MPR> {
		private WeakReference<MPR> mprRef;
		public MPRPhantomReference( MPR referent, ReferenceQueue<? super MPR> q ){
			super( referent, q );
			this.mprRef = new WeakReference<>( referent );
		}
		@Override
		public void clear(){
			System.out.println( "MPR clearing ..." );	// TODO <<<<<<<<<<<<<<<<<<<<<
			MPR mpr = mprRef.get();
			if( mpr != null ){
				mpr.freeMPR();
			}
			super.clear();
		}
		/*public void finalizeResources(){
			System.out.println( "MPR clearing finalizeResources ..." );	// TODO <<<<<<<<<<<<<<<<<<<<<
			MPR mpr = mprRef.get();
			if( mpr != null ){
				mpr.freeMPR();
			}
		}*/
	}
	private static ReferenceQueue<MPR> referenceQueue = new ReferenceQueue<>();
	private MPRPhantomReference phantomReference;
	private void initPhantom(){ this.phantomReference = new MPRPhantomReference( this, referenceQueue ); }
	/*public static void cleanAllPhantomReferences(){
		Reference<?> referenceFromQueue;
		while( (referenceFromQueue = referenceQueue.poll()) != null ){
			//((MPRPhantomReference)referenceFromQueue).finalizeResources();
			referenceFromQueue.clear();
		}
	}*/
	// Constructors ----------------------------------------------------------------------------------------------------
	public MPR(){
		ptr = initConstant( 0 );	// ZERO
		initPhantom();
	}
	private MPR( int cte, boolean init ){
		ptr = initConstant( cte );
		initPhantom();
	}
	private MPR( long ptr ){
		this.ptr = ptr;
		initPhantom();
	}

	private static native String toStr( long ptr, int prec );
	@Override
	public String toString(){
		return toStr( this.ptr, DIGITS_PRECISION );
	}

	// Number methods --------------------------------------------------------------------------------------------------
	@Override
	public int intValue(){
		return 0;
	}

	@Override
	public long longValue(){
		return 0;
	}

	@Override
	public float floatValue(){
		return 0;
	}

	@Override
	public double doubleValue(){
		return 0;
	}



	@Override
	public int compareTo( MPR o ){
		return 0;	// TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
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
