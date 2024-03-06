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

#ifdef __cplusplus
}
#endif

//------------------------------------
#endif // _MPR_NATIVE_FUNCTIONS_H_