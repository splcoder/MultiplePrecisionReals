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
Java_edu_spl_MPR_freeMPR( JNIEnv *env, jobject obj, jlong p_dir ){
	MPRF::freeMemDirection( p_dir );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_MPR_initConstant( JNIEnv *env, jobject obj, jint cte ){
	switch( cte ){
		case 0: return MPRF::getMemDirection( 0.0 );
		case 1: return MPRF::getMemDirection( 1.0 );
	}
	return 0;	// nullptr
}

#ifdef __cplusplus
}
#endif