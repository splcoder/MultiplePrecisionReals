package edu.spl;

/**
 * Multiple Precision in Reals
 */
public class MPR implements Comparable<MPR> {
	static {
		//System.loadLibrary( "mpr" );
		System.load("C:\\Users\\Sergio\\Desktop\\Cursos Certificacion\\Curso Java\\Projects\\MultiplePrecisionReals\\build\\libs\\mpr\\shared\\mpr.dll");
	}

	public static native void print();

	@Override
	public int compareTo( MPR o ){
		return 0;
	}
}
