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
		case -1:	return MPRF::getMemDirection( std::numeric_limits<mpreal>::epsilon() * (mpreal)std::numeric_limits<mpreal>::radix );
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
	return env->NewStringUTF( ((mpreal*)ptr)->toString( prec ).c_str() );
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

JNIEXPORT jint JNICALL
Java_edu_spl_MPR_getSign( JNIEnv *env, jobject obj, jlong ptr ){
	return sgn( *((mpreal*)ptr) );
}

int64_t exeOperation( int ope, const mpreal &left, const mpreal &right ){
	switch( ope ){
		case 0:		return MPRF::getMemDirection( left + right );
		case 1:		return MPRF::getMemDirection( left - right );
		case 2:		return MPRF::getMemDirection( left * right );
		case 3:		return MPRF::getMemDirection( left / right );
		case 4:		return MPRF::getMemDirection( hypot( left, right ) );
		case 5:		return MPRF::getMemDirection( pow( left, right ) );
		case 6:		return MPRF::getMemDirection( log( left ) / log( right ) );
		case 7:		return MPRF::getMemDirection( atan2( left, right ) );
		case 8:		return MPRF::getMemDirection( fmax( left, right ) );
		case 9:		return MPRF::getMemDirection( fmin( left, right ) );
		case 10:	return MPRF::getMemDirection( fmod( left, right ) );
		case 11:	return MPRF::getMemDirection( rem( left, right ) );
		case 12:	return MPRF::getMemDirection( mod( left, right ) );
	}
	return 0;	// nullptr (it never reachs this point)
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation( JNIEnv *env, jobject obj, jlong lPtr, jlong rPtr, jint ope ){
	return exeOperation( ope, *((mpreal*)lPtr), *((mpreal*)rPtr) );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation2( JNIEnv *env, jobject obj, jlong lPtr, jdouble right, jint ope ){
	return exeOperation( ope, *((mpreal*)lPtr), (mpreal)right );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation3( JNIEnv *env, jobject obj, jdouble left, jlong rPtr, jint ope ){
	return exeOperation( ope, (mpreal)left, *((mpreal*)rPtr) );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation4( JNIEnv *env, jobject obj, jdouble left, jdouble right, jint ope ){
	return exeOperation( ope, (mpreal)left, (mpreal)right );
}

int64_t exeOperationForOne( int ope, const mpreal &value ){
	switch( ope ){
		case 0:		return MPRF::getMemDirection( -value );
		case 1:		return MPRF::getMemDirection( sqr( value ) );
		case 2:		return MPRF::getMemDirection( fabs( value ) );
		case 3:		return MPRF::getMemDirection( floor( value ) );
		case 4:		return MPRF::getMemDirection( ceil( value ) );
		case 5:		return MPRF::getMemDirection( trunc( value ) );
		case 6:		return MPRF::getMemDirection( round( value ) );
		case 7:		return MPRF::getMemDirection( sqrt( value ) );
		case 8:		return MPRF::getMemDirection( cbrt( value ) );
		case 9:		return MPRF::getMemDirection( exp( value ) );
		case 10:	return MPRF::getMemDirection( exp2( value ) );
		case 11:	return MPRF::getMemDirection( exp10( value ) );
		case 12:	return MPRF::getMemDirection( expm1( value ) );
		case 13:	return MPRF::getMemDirection( exp( value )*value );
		case 14:	return MPRF::getMemDirection( log( value ) );
		case 15:	return MPRF::getMemDirection( log1p( value ) );
		case 16:	return MPRF::getMemDirection( log2( value ) );
		case 17:	return MPRF::getMemDirection( log10( value ) );
		case 18:	return MPRF::getMemDirection( sin( value ) );
		case 19:	return MPRF::getMemDirection( cos( value ) );
		case 20:	return MPRF::getMemDirection( tan( value ) );
		case 21:	return MPRF::getMemDirection( csc( value ) );
		case 22:	return MPRF::getMemDirection( sec( value ) );
		case 23:	return MPRF::getMemDirection( cot( value ) );
		case 24:	return MPRF::getMemDirection( asin( value ) );
		case 25:	return MPRF::getMemDirection( acos( value ) );
		case 26:	return MPRF::getMemDirection( atan( value ) );
		case 27:	return MPRF::getMemDirection( acsc( value ) );
		case 28:	return MPRF::getMemDirection( asec( value ) );
		case 29:	return MPRF::getMemDirection( acot( value ) );
		case 30:	return MPRF::getMemDirection( sinh( value ) );
		case 31:	return MPRF::getMemDirection( cosh( value ) );
		case 32:	return MPRF::getMemDirection( tanh( value ) );
		case 33:	return MPRF::getMemDirection( csch( value ) );
		case 34:	return MPRF::getMemDirection( sech( value ) );
		case 35:	return MPRF::getMemDirection( coth( value ) );
		case 36:	return MPRF::getMemDirection( asinh( value ) );
		case 37:	return MPRF::getMemDirection( acosh( value ) );
		case 38:	return MPRF::getMemDirection( atanh( value ) );
		case 39:	return MPRF::getMemDirection( acsch( value ) );
		case 40:	return MPRF::getMemDirection( asech( value ) );
		case 41:	return MPRF::getMemDirection( acoth( value ) );
		case 42:	return MPRF::getMemDirection( besselj0( value ) );
		case 43:	return MPRF::getMemDirection( besselj1( value ) );
		case 44:	return MPRF::getMemDirection( bessely0( value ) );
		case 45:	return MPRF::getMemDirection( bessely1( value ) );
		case 46:	return MPRF::getMemDirection( erf( value ) );
		case 47:	return MPRF::getMemDirection( erfc( value ) );
		case 48:	return MPRF::getMemDirection( lgamma( value ) );
		case 49:	return MPRF::getMemDirection( tgamma( value ) );
		case 50:	return MPRF::getMemDirection( lngamma( value ) );
		case 51:	return MPRF::getMemDirection( gamma( value ) );
		case 52:	return MPRF::getMemDirection( digamma( value ) );
		case 53:	return MPRF::getMemDirection( zeta( value ) );
		case 54:	return MPRF::getMemDirection( ai( value ) );
		case 55:	return MPRF::getMemDirection( li2( value ) );
	}
	return 0;	// nullptr (it never reachs this point)
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation5( JNIEnv *env, jobject obj, jlong ptr, jint ope ){
	return exeOperationForOne( ope, *((mpreal*)ptr) );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation6( JNIEnv *env, jobject obj, jdouble value, jint ope ){
	return exeOperationForOne( ope, (mpreal)value );
}

void exeOperation1Arg2Out( mpreal &out1, mpreal &out2, jint ope, const mpreal &value ){
	switch( ope ){
		case 0: sin_cos( out1, out2, value );		break;
		case 1: sinh_cosh( out1, out2, value );		break;
		case 2: out1 = modf( value, out2 );			break;
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

jlong exeOperation2Arg1Out( jint order, const mpreal &value, jint ope ){
	switch( ope ){
		case 0: return MPRF::getMemDirection( besseljn( order, value ) );
		case 1: return MPRF::getMemDirection( besselyn( order, value ) );
		case 2: return MPRF::getMemDirection( root( value, order ) );
	}
	return 0;	// nullptr
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation9( JNIEnv *env, jobject obj, jint order, jlong ptr, jint ope ){
	mpreal *value = (mpreal*)ptr;
	return exeOperation2Arg1Out( order, *value, ope );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_operation10( JNIEnv *env, jobject obj, jint order, jdouble val, jint ope ){
	mpreal value( val );
	return exeOperation2Arg1Out( order, value, ope );
}

#ifdef __cplusplus
}
#endif