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
		case -6:	return MPRF::getMemDirection( mpreal().setNan() );
		case -5:	return MPRF::getMemDirection( mpreal().setInf() );
		case -4:	return MPRF::getMemDirection( mpreal().setInf( -1 ) );
		case -3:	return MPRF::getMemDirection( std::numeric_limits<mpreal>::max() );
        case -2:	return MPRF::getMemDirection( std::numeric_limits<mpreal>::min() );
		//case -1:
		case 0:		return MPRF::getMemDirection( 0.0 );
		case 1:		return MPRF::getMemDirection( 1.0 );
		case 2:		return MPRF::getMemDirection( mpfr::const_euler() );
		case 3:		return MPRF::getMemDirection( mpfr::exp( 1 ) );
		case 4:		return MPRF::getMemDirection( mpfr::const_pi() );
		case 5:		return MPRF::getMemDirection( mpfr::const_log2() );
		case 6:		return MPRF::getMemDirection( mpfr::const_catalan() );
	}
	return 0;	// nullptr
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_initWithDouble( JNIEnv *env, jobject obj, jdouble value ){
	return MPRF::getMemDirection( value );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_initWithLong( JNIEnv *env, jobject obj, jlong value ){
	return MPRF::getMemDirection( value );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_initWithString( JNIEnv *env, jobject obj, jstring value ){
	return MPRF::getMemDirection( env->GetStringUTFChars( value, NULL ) );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_initWithCopy( JNIEnv *env, jobject obj, jlong ptr ){
	return MPRF::getMemDirection( *((mpreal*)ptr) + 0 );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_random( JNIEnv *env, jobject obj, jint seed ){
	return MPRF::getMemDirection( mpfr::random( seed ) );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_toLong( JNIEnv *env, jobject obj, jlong ptr ){
	return (int64_t)*((mpreal*)ptr);
}

JNIEXPORT jdouble JNICALL
Java_edu_spl_MPR_toDouble( JNIEnv *env, jobject obj, jlong ptr ){
	return (double)*((mpreal*)ptr);
}

JNIEXPORT jstring JNICALL
Java_edu_spl_MPR_toStr( JNIEnv *env, jobject obj, jlong ptr, jint prec ){
	return env->NewStringUTF( ((mpreal*)ptr)->toString().c_str() );	// TODO format <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
}

JNIEXPORT jboolean JNICALL
Java_edu_spl_MPR_areEquals( JNIEnv *env, jobject obj, jlong lPtr, jlong rPtr ){
	return *((mpreal*)lPtr) == *((mpreal*)rPtr);
}

JNIEXPORT jint JNICALL
Java_edu_spl_MPR_compare( JNIEnv *env, jobject obj, jlong lPtr, jlong rPtr ){
	return sgn( *((mpreal*)lPtr) - *((mpreal*)rPtr) );
}

JNIEXPORT jboolean JNICALL
Java_edu_spl_MPR_check( JNIEnv *env, jobject obj, jlong ptr, jint ope ){
	switch( ope ){
		case 0: return isnan( *((mpreal*)ptr) );
		case 1: return isinf( *((mpreal*)ptr) );
		case 2: return isinf( *((mpreal*)ptr) ) && (sgn( *((mpreal*)ptr) ) > 0);
		case 3: return isinf( *((mpreal*)ptr) ) && (sgn( *((mpreal*)ptr) ) < 0);
		case 4: return isfinite( *((mpreal*)ptr) );
		case 5: return iszero( *((mpreal*)ptr) );
		case 6: return isint( *((mpreal*)ptr) );
		case 7: return sgn( *((mpreal*)ptr) ) < 0;
	}
}

/*	TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	const mpreal eps         =    std::numeric_limits<mpreal>::epsilon();
	const int    base        =    std::numeric_limits<mpreal>::radix;
	const mpreal prec        =    eps * base;
	const int bindigits      =    std::numeric_limits<mpreal>::digits(); // eqv. to mpfr::mpreal::get_default_prec();
	const mpreal rnd         =    std::numeric_limits<mpreal>::round_error();
	const mpreal small       =    one / maxval;
	const mpreal sfmin       =    (small > minval) ? small * (one + eps) : minval;
	const mpreal round       =    std::numeric_limits<mpreal>::round_style();
	const int    min_exp     =    std::numeric_limits<mpreal>::min_exponent;
	const mpreal underflow   =    std::numeric_limits<mpreal>::min();
	const int    max_exp     =    std::numeric_limits<mpreal>::max_exponent;
	const mpreal overflow    =    std::numeric_limits<mpreal>::max();

	cout.precision(digits);    // Show all the digits
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
Java_edu_spl_MPR_operation2( JNIEnv *env, jobject obj, jlong lPtr, jdouble right, jint ope ){
	return MPRF::operation2( lPtr, right, ope );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation3( JNIEnv *env, jobject obj, jdouble left, jlong rPtr, jint ope ){
	return MPRF::operation3( left, rPtr, ope );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation4( JNIEnv *env, jobject obj, jdouble left, jdouble right, jint ope ){
	return MPRF::operation4( left, right, ope );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation5( JNIEnv *env, jobject obj, jlong ptr, jint ope ){
	return MPRF::operation5( ptr, ope );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation6( JNIEnv *env, jobject obj, jdouble value, jint ope ){
	return MPRF::operation6( value, ope );
}

void exeOperation1Arg2Out( mpreal &out1, mpreal &out2, jint ope, const mpreal &value ){
	switch( ope ){
		case 0: sin_cos( out1, out2, value );		break;
		case 1: sinh_cosh( out1, out2, value );		break;
        default: out1 = mpreal().setNan(); out2 = mpreal().setNan();
	}
}

// Returns an array of 2 MPR values
JNIEXPORT jlongArray JNICALL
Java_edu_spl_MPR_operation7( JNIEnv *env, jobject obj, jlong ptr, jint ope ){
	mpreal out1, out2, *value = (mpreal*)ptr;
	exeOperation1Arg2Out( out1, out2, ope, *value );

	int64_t first = MPRF::getMemDirection( out1 ), second = MPRF::getMemDirection( out2 );
	jlong outCArray[] = { first, second };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_MPR_operation8( JNIEnv *env, jobject obj, jdouble val, jint ope ){
	mpreal out1, out2, value( val );
	exeOperation1Arg2Out( out1, out2, ope, value );

	int64_t first = MPRF::getMemDirection( out1 ), second = MPRF::getMemDirection( out2 );
	jlong outCArray[] = { first, second };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

#ifdef __cplusplus
}
#endif