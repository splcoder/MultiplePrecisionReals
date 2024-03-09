#include "./MPR_native_functions.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL
Java_edu_spl_MPR_fixDigitsPrecision( JNIEnv *env, jobject obj, jint digits ){
	// Use double if < 20 ...
	if( digits < 20 )	digits = 20;
	mpreal::set_default_prec( mpfr::digits2bits( digits ) );
	return digits;
}

JNIEXPORT void JNICALL
Java_edu_spl_MPR_freeMPR( JNIEnv *env, jobject obj, jlong ptr ){
	MPRF::freeMemDirection( ptr );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_initConstant( JNIEnv *env, jobject obj, jint cte ){
	switch( cte ){
		case 0: return MPRF::getMemDirection( 0.0 );
		case 1: return MPRF::getMemDirection( 1.0 );
	}
	return 0;	// nullptr
}

JNIEXPORT jstring JNICALL
Java_edu_spl_MPR_toStr( JNIEnv *env, jobject obj, jlong ptr, jint prec ){
	//mpreal *v = (mpreal*)ptr;
	//std::string str = v->toString();
	//return env->NewStringUTF( str.c_str() );
	return env->NewStringUTF( ((mpreal*)ptr)->toString().c_str() );	// TODO format <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_getAsLong( JNIEnv *env, jobject obj, jlong ptr ){
	return (int64_t)*((mpreal*)ptr);
}

JNIEXPORT jdouble JNICALL
Java_edu_spl_MPR_getAsDouble( JNIEnv *env, jobject obj, jlong ptr ){
	return (double)*((mpreal*)ptr);
}

/*
	// Compute all the vital characteristics of mpreal (in current precision)
	// Analogous to lamch from LAPACK
	const mpreal one         =    1.0;
	const mpreal zero        =    0.0;
	const mpreal eps         =    std::numeric_limits<mpreal>::epsilon();
	const int    base        =    std::numeric_limits<mpreal>::radix;
	const mpreal prec        =    eps * base;
	const int bindigits      =    std::numeric_limits<mpreal>::digits(); // eqv. to mpfr::mpreal::get_default_prec();
	const mpreal rnd         =    std::numeric_limits<mpreal>::round_error();
	const mpreal maxval      =    std::numeric_limits<mpreal>::max();
	const mpreal minval      =    std::numeric_limits<mpreal>::min();
	const mpreal small       =    one / maxval;
	const mpreal sfmin       =    (small > minval) ? small * (one + eps) : minval;
	const mpreal round       =    std::numeric_limits<mpreal>::round_style();
	const int    min_exp     =    std::numeric_limits<mpreal>::min_exponent;
	const mpreal underflow   =    std::numeric_limits<mpreal>::min();
	const int    max_exp     =    std::numeric_limits<mpreal>::max_exponent;
	const mpreal overflow    =    std::numeric_limits<mpreal>::max();

	// Additionally compute pi with required accuracy - just for fun :)
	const mpreal pi          =    mpfr::const_pi();

	cout.precision(digits);    // Show all the digits
	cout << "pi         =    "<<    pi          << endl;
	cout << "eps        =    "<<    eps         << endl;
	cout << "base       =    "<<    base        << endl;
	cout << "prec       =    "<<    prec        << endl;
	cout << "b.digits   =    "<<    bindigits   << endl;
	cout << "rnd        =    "<<    rnd         << endl;
	cout << "maxval     =    "<<    maxval      << endl;
	cout << "minval     =    "<<    minval      << endl;
	cout << "small      =    "<<    small       << endl;
	cout << "sfmin      =    "<<    sfmin       << endl;
	cout << "1/sfmin    =    "<<    1 / sfmin   << endl;
	cout << "round      =    "<<    round       << endl;
	cout << "max_exp    =    "<<    max_exp     << endl;
	cout << "min_exp    =    "<<    min_exp     << endl;
	cout << "underflow  =    "<<    underflow   << endl;
	cout << "overflow   =    "<<    overflow    << endl;
*/

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation( JNIEnv *env, jobject obj, jlong lPtr, jlong rPtr, jint ope ){
	return MPRF::operation( lPtr, rPtr, ope );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation2( JNIEnv *env, jobject obj, jlong lPtr, jdouble r, jint ope ){
	return MPRF::operation2( lPtr, r, ope );
}

#ifdef __cplusplus
}
#endif