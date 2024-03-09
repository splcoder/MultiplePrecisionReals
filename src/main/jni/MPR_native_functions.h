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
JNIEXPORT jstring	JNICALL Java_edu_spl_MPR_toStr( JNIEnv *env, jobject obj, jlong ptr, jint prec );
JNIEXPORT jlong		JNICALL Java_edu_spl_MPR_getAsLong( JNIEnv *env, jobject obj, jlong ptr );
JNIEXPORT jdouble	JNICALL Java_edu_spl_MPR_getAsDouble( JNIEnv *env, jobject obj, jlong ptr );

JNIEXPORT jlong		JNICALL Java_edu_spl_MPR_operation( JNIEnv *env, jobject obj, jlong lPtr, jlong rPtr, jint ope );
JNIEXPORT jlong		JNICALL Java_edu_spl_MPR_operation2( JNIEnv *env, jobject obj, jlong lPtr, jdouble r, jint ope );

#ifdef __cplusplus
}
#endif

//------------------------------------
#endif // _MPR_NATIVE_FUNCTIONS_H_