#ifndef _MPR_NATIVE_FUNCTIONS_H_
#define _MPR_NATIVE_FUNCTIONS_H_
//------------------------------------

#include <jni.h>
#include <stdio.h>

#include "./numbers/MPR_functions.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint		JNICALL Java_edu_spl_MPR_fixDigitsPrecision( JNIEnv *env, jobject obj, jint digits );
JNIEXPORT void		JNICALL Java_edu_spl_MPR_freeMPR( JNIEnv *env, jobject obj, jlong p_dir );
JNIEXPORT jlong		JNICALL Java_edu_spl_MPR_initConstant( JNIEnv *env, jobject obj, jint cte );
JNIEXPORT jlong		JNICALL Java_edu_spl_MPR_initWithDouble( JNIEnv *env, jobject obj, jdouble value );
JNIEXPORT jlong		JNICALL Java_edu_spl_MPR_initWithLong( JNIEnv *env, jobject obj, jlong value );
JNIEXPORT jlong		JNICALL Java_edu_spl_MPR_initWithString( JNIEnv *env, jobject obj, jstring value );
JNIEXPORT jlong		JNICALL Java_edu_spl_MPR_initWithCopy( JNIEnv *env, jobject obj, jlong ptr );
JNIEXPORT jlong		JNICALL Java_edu_spl_MPR_random( JNIEnv *env, jobject obj, jint seed );
JNIEXPORT jlong		JNICALL Java_edu_spl_MPR_getAsLong( JNIEnv *env, jobject obj, jlong ptr );
JNIEXPORT jdouble	JNICALL Java_edu_spl_MPR_getAsDouble( JNIEnv *env, jobject obj, jlong ptr );
JNIEXPORT jstring	JNICALL Java_edu_spl_MPR_toStr( JNIEnv *env, jobject obj, jlong ptr, jint prec );
JNIEXPORT jboolean	JNICALL Java_edu_spl_MPR_areEquals( JNIEnv *env, jobject obj, jlong lPtr, jlong rPtr );
JNIEXPORT jint		JNICALL Java_edu_spl_MPR_compare( JNIEnv *env, jobject obj, jlong lPtr, jlong rPtr );
JNIEXPORT jboolean	JNICALL Java_edu_spl_MPR_check( JNIEnv *env, jobject obj, jlong ptr, jint ope );

JNIEXPORT jlong		JNICALL Java_edu_spl_MPR_operation( JNIEnv *env, jobject obj, jlong lPtr, jlong rPtr, jint ope );
JNIEXPORT jlong		JNICALL Java_edu_spl_MPR_operation2( JNIEnv *env, jobject obj, jlong lPtr, jdouble r, jint ope );

#ifdef __cplusplus
}
#endif

//------------------------------------
#endif // _MPR_NATIVE_FUNCTIONS_H_