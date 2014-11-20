#include <jni.h>
#ifndef _Included_org_ripple_power_NativeSupport
#define _Included_org_ripple_power_NativeSupport
#ifdef __cplusplus
extern "C" {
#endif

		JNIEXPORT jstring JNICALL Java_org_ripple_power_NativeSupport_getByteKeys(JNIEnv* env, jclass, jbyteArray, jboolean);

		JNIEXPORT jstring JNICALL Java_org_ripple_power_NativeSupport_getHashKeys(JNIEnv* env, jclass, jbyteArray, jboolean);

		JNIEXPORT jboolean JNICALL Java_org_ripple_power_NativeSupport_findByteAddress(JNIEnv* env, jclass, jbyteArray, jbyteArray);

		JNIEXPORT jstring JNICALL Java_org_ripple_power_NativeSupport_getRippleBase58(JNIEnv* env, jclass, jbyteArray);

		JNIEXPORT jbyteArray JNICALL Java_org_ripple_power_NativeSupport_getRipemd160Sha256(JNIEnv* env, jclass, jbyteArray);

		JNIEXPORT jstring JNICALL Java_org_ripple_power_NativeSupport_getRippleByteKeys(JNIEnv* env, jclass, jbyteArray);
			
        JNIEXPORT jbyteArray JNICALL Java_org_ripple_power_NativeSupport_getRippleHashKeys(JNIEnv* env, jclass, jbyteArray);

		JNIEXPORT jstring JNICALL Java_org_ripple_power_NativeSupport_getSecp256k1ToPublic(JNIEnv* env, jclass, jbyteArray);

		JNIEXPORT jbyteArray JNICALL Java_org_ripple_power_NativeSupport_getSha512Quarter(JNIEnv* env, jclass, jbyteArray);

		JNIEXPORT jbyteArray JNICALL Java_org_ripple_power_NativeSupport_getSha512Half(JNIEnv* env, jclass, jbyteArray);

		JNIEXPORT jobjectArray JNICALL Java_org_ripple_power_NativeSupport_getRippleBatchKeys(JNIEnv* env, jclass, jbyteArray,jint);

        JNIEXPORT jbyteArray JNICALL Java_org_ripple_power_NativeSupport_getNxtHashKeys(JNIEnv* env, jclass, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif