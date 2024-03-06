#include "./MPR_native_functions.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL
Java_edu_spl_MPR_print( JNIEnv *env, jobject obj ){
	printf( "Hello there !!!" );
}

#ifdef __cplusplus
}
#endif