#include "stdafx.h"
#include "JCoin.h"
#include "CoinKey.h"
#include "Base58Check.h"
#include "uint256.h"
#include "key.h"
#include "RippleAddress.h"
#include <iostream>
#include <openssl/ec.h>
#include <openssl/ecdsa.h>
#include <openssl/obj_mac.h>
#include <openssl/rand.h>

#define _TAG 10
#define _RIPPLE_W 33
#define COIN_SHA256_SIZE (SHA256_DIGEST_LENGTH)

struct CoinSHA256
{
unsigned char data[COIN_SHA256_SIZE];
};

#define COIN_RIPEMD160_SIZE (RIPEMD160_DIGEST_LENGTH)

struct CoinRIPEMD160
{
unsigned char data[COIN_RIPEMD160_SIZE];
};

inline void Bitcoin_SHA256(struct CoinSHA256 *output, const void *input, size_t size)
{
	SHA256_CTX ctx;
	SHA256_Init(&ctx);
	SHA256_Update(&ctx, input, size);
	SHA256_Final(output->data, &ctx);
}

inline void Bitcoin_DoubleSHA256(struct CoinSHA256 *output, const void *input, size_t size)
{
	struct CoinSHA256 round1;
	SHA256_CTX ctx;
	SHA256_Init(&ctx);
	SHA256_Update(&ctx, input, size);
	SHA256_Final(round1.data, &ctx);
	SHA256_Init(&ctx);
	SHA256_Update(&ctx, &round1.data, COIN_SHA256_SIZE);
	SHA256_Final(output->data, &ctx);
}

inline void Bitcoin_RIPEMD160(struct CoinRIPEMD160 *output, const void *input, size_t size)
{
	RIPEMD160_CTX ctx;
	RIPEMD160_Init(&ctx);
	RIPEMD160_Update(&ctx, input, size);
	RIPEMD160_Final(output->data, &ctx);
}

inline void copy(unsigned char* dst, unsigned char* src, size_t size)
{
    if (size > 0 && sizeof(dst) >= 0 && sizeof(src) >= 0) {
        memcpy(dst, src, size);
    }
}

inline void arraycopy(unsigned char* src, int srcPos,unsigned char* dest, int destPos,
        size_t size)
{
    if (size > 0 && sizeof(dest) >= 0 && sizeof(src) >= 0) {
        copy(dest + destPos, src + srcPos, size);
    }
}

 jstring char2jstring(JNIEnv* env,const char* chars)
{
    jclass strClass = (env)->FindClass("Ljava/lang/String;");
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    jbyteArray bytes = (env)->NewByteArray(strlen(chars));
    (env)->SetByteArrayRegion(bytes, 0, strlen(chars), (jbyte*)chars);
    jstring encoding = (env)->NewStringUTF("UTF-8");
    return (jstring)(env)->NewObject(strClass, ctorID, bytes, encoding);
}
 
jstring str2jstring(JNIEnv* env, const std::string str){
	jclass strClass = env->FindClass( "java/lang/String");
	jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
	jbyteArray bytes = env->NewByteArray(str.size());
	env->SetByteArrayRegion(bytes, 0, str.size(), (jbyte*)&str.c_str()[0]);
	jstring encoding = env->NewStringUTF("UTF-8");
	return (jstring)env->NewObject(strClass, ctorID, bytes, encoding);
}
 
std::string jstring2str(JNIEnv* env, jstring jstr)
{  
	const char *cptr = env->GetStringUTFChars(jstr, 0);
	jsize len = env->GetStringUTFLength(jstr);
	std::string str(cptr,len);
	env->ReleaseStringUTFChars(jstr, cptr);
	return str;
}   

JNIEXPORT jboolean JNICALL Java_org_address_NativeSupport_findByteAddress(JNIEnv* env, jclass, jbyteArray dst, jbyteArray src){
        int len = (int)(env->GetArrayLength(dst));
        unsigned char* dstPtr = (unsigned char*)env->GetByteArrayElements(dst, 0);
		int size = (int)(env->GetArrayLength(src));
        unsigned char* srcPtr = (unsigned char*)env->GetByteArrayElements(src, 0);
		int patternOffset = 0;
	    
		int index = 0;
		char b = srcPtr[index++];
		
		int mark = 0;
		for (; index < size;) {
			if (dstPtr[patternOffset] == b) {
				patternOffset++;
				if (patternOffset == len) {
					return true;
				}
			} else {
				if (b == _TAG) {
					mark = index;
				} else {
					int skip = index - mark;
					int go = 34 - skip;
					if (go + index < size && srcPtr[go + index] == _TAG) {
						index = index + go;
					} else if ((go + index - 1) < size
							&& srcPtr[(go + index - 1)] == _TAG) {
						index = (index + go - 1);
					}
				}
				patternOffset = 0;
			}
			b = srcPtr[index++];
		}
		free(dstPtr);
		free(srcPtr);

		return false;
}



JNIEXPORT jstring JNICALL Java_org_address_NativeSupport_getByteKeys(JNIEnv* env, jclass, jbyteArray res, jboolean b){
	       
           int len = (int)(env->GetArrayLength(res));
           unsigned char* arrPtr = (unsigned char*)env->GetByteArrayElements(res, 0);

		   uchar_vector hash(arrPtr,len);
		   
		   hash = sha256(hash);
		   CoinKey _key;
		   _key.generateNewKey();
		   _key.setPrivateKey(hash,b);

		   std::string address = _key.getAddress(); 
		   std::string privateKey =  _key.getSecure();
		   std::string all = address +","+ privateKey;
		   free(arrPtr);

		   return str2jstring(env,all);
}


JNIEXPORT jstring JNICALL Java_org_address_NativeSupport_getHashKeys(JNIEnv* env, jclass, jbyteArray res, jboolean b){
	       
           int len = (int)(env->GetArrayLength(res));
           unsigned char* arrPtr = (unsigned char*)env->GetByteArrayElements(res, 0);

		   uchar_vector hash(arrPtr,len);
		   CoinKey _key;
		   _key.generateNewKey();
		   _key.setPrivateKey(hash,b);

		   std::string address = _key.getAddress(); 
		   std::string privateKey =  _key.getSecure();
		   std::string all = address +","+ privateKey;
		   free(arrPtr);

		   return str2jstring(env,all);
}


JNIEXPORT jstring JNICALL Java_org_address_NativeSupport_getRippleBase58(JNIEnv* env, jclass, jbyteArray res){
           int len = (int)(env->GetArrayLength(res));
           unsigned char* arrPtr = (unsigned char*)env->GetByteArrayElements(res, 0);
		   uchar_vector hash(arrPtr,len);
		   free(arrPtr);
		   return str2jstring(env,toBase58Check(hash,_RIPPLE_W,RIPPLE_BASE58_CHARS));
}

JNIEXPORT jbyteArray JNICALL Java_org_address_NativeSupport_getRipemd160Sha256(JNIEnv* env, jclass, jbyteArray res){
           int len = (int)(env->GetArrayLength(res));
           unsigned char* arrPtr = (unsigned char*)env->GetByteArrayElements(res, 0);
		   uchar_vector hash(arrPtr,len);
		   hash = ripemd160(sha256(hash));
		   jbyteArray dst;
		   len = hash.size();
		   unsigned char* bytes = new unsigned char[len];
		   hash.copyToArray(bytes);
		   dst = env->NewByteArray(len);
           env->SetByteArrayRegion(dst, 0, len, (jbyte*)bytes);
		   delete bytes;
		   free(arrPtr);
		   return dst;
}

void getRand(unsigned char *buf, int num)
{
    if (RAND_bytes(buf, num) != 1)
    {
        assert(false);
        throw std::runtime_error("Entropy pool not seeded");
    }
}
	

JNIEXPORT jstring JNICALL Java_org_address_NativeSupport_getRippleByteKeys(JNIEnv* env, jclass, jbyteArray res){
		 int len = (int)(env->GetArrayLength(res));

         unsigned char* arrPtr = (unsigned char*)env->GetByteArrayElements(res, 0);
	 	 uchar_vector hash(arrPtr,len);

		 uint128 v(sha512quarter(hash));
		 
         RippleAddress _naSeed;
         RippleAddress _naAccount;

		 _naSeed.setSeed(v);
         RippleAddress naGenerator = createGeneratorPublic(_naSeed);
         _naAccount.setAccountPublic(naGenerator.getAccountPublic(), 0);
		  free(arrPtr);
		return str2jstring(env,_naAccount.humanAccountID() + "," + _naSeed.humanSeed());
}


JNIEXPORT jbyteArray JNICALL Java_org_address_NativeSupport_getRippleHashKeys(JNIEnv* env, jclass, jbyteArray res){
		 int len = (int)(env->GetArrayLength(res));

         unsigned char* arrPtr = (unsigned char*)env->GetByteArrayElements(res, 0);
	 	 uchar_vector hash(arrPtr,len);

		 uint128 v(hash);
		 
         RippleAddress _naSeed;
         RippleAddress _naAccount;

		 _naSeed.setSeed(v);
         RippleAddress naGenerator = createGeneratorPublic(_naSeed);
         _naAccount.setAccountPublic(naGenerator.getAccountPublic(), 0);
		 free(arrPtr);
		 std::string result = _naAccount.humanAccountID() + "," + _naSeed.humanSeed();
		 jbyteArray bytes = env->NewByteArray(result.size());
	     env->SetByteArrayRegion(bytes, 0, result.size(), (jbyte*)&result.c_str()[0]);
		return bytes;
}

JNIEXPORT jobjectArray JNICALL Java_org_address_NativeSupport_getRippleBatchKeys(JNIEnv* env, jclass, jbyteArray res,jint max){
		 int len = (int)(env->GetArrayLength(res));
         unsigned char* arrPtr = (unsigned char*)env->GetByteArrayElements(res, 0);
	 	 uchar_vector hash(arrPtr,len);
		 CBigNum big(hash);
		 jclass objClass = env->FindClass("java/lang/String");
         jobjectArray texts= env->NewObjectArray(max, objClass, 0);
		 
         RippleAddress _naSeed;
         RippleAddress _naAccount;

		for(int i=0;i<max;i++){
			 uint128 v1(big.getvch());
			 _naSeed.setSeed(v1);
			 RippleAddress naGenerator = createGeneratorPublic(_naSeed);
			 _naAccount.setAccountPublic(naGenerator.getAccountPublic(), 0);
			 std::string result = _naAccount.humanAccountID() + "," + _naSeed.humanSeed();
			 env->SetObjectArrayElement(texts, i, str2jstring(env,result));
			 big+=1;
		 }
		free(arrPtr);
		return texts;
}


JNIEXPORT jbyteArray JNICALL Java_org_address_NativeSupport_getSha512Quarter(JNIEnv* env, jclass, jbyteArray res){
	 int len = (int)(env->GetArrayLength(res));
     unsigned char* arrPtr = (unsigned char*)env->GetByteArrayElements(res, 0);
	 uchar_vector hash(arrPtr,len);
     hash = sha512quarter(hash);

     jbyteArray dst;
		   len = hash.size();
		   unsigned char* bytes = new unsigned char[len];
		   hash.copyToArray(bytes);
		   dst = env->NewByteArray(len);
           env->SetByteArrayRegion(dst, 0, len, (jbyte*)bytes);
		   delete bytes;
		   free(arrPtr);
	 return dst; 
}

JNIEXPORT jbyteArray JNICALL Java_org_address_NativeSupport_getSha512Half(JNIEnv* env, jclass, jbyteArray res){
	 int len = (int)(env->GetArrayLength(res));
     unsigned char* arrPtr = (unsigned char*)env->GetByteArrayElements(res, 0);
	 uchar_vector hash(arrPtr,len);
     hash = sha512half(hash);

     jbyteArray dst;
		   len = hash.size();
		   unsigned char* bytes = new unsigned char[len];
		   hash.copyToArray(bytes);
		   dst = env->NewByteArray(len);
           env->SetByteArrayRegion(dst, 0, len, (jbyte*)bytes);
		   delete bytes;
		   free(arrPtr);
	 return dst; 
}


JNIEXPORT jstring JNICALL Java_org_address_NativeSupport_getSecp256k1ToPublic(JNIEnv* env, jclass, jbyteArray res){
		 
		   int len = (int)(env->GetArrayLength(res));
		   unsigned char* arrPtr = (unsigned char*)env->GetByteArrayElements(res, 0);
		   uchar_vector hash(arrPtr,len);

		   hash = ripemd160(sha256(hash));
		   
		   len = hash.size();

		    uchar_vector data;
			data.push_back(0);                                       
			data += hash;
			uchar_vector checksum = sha256_2(data);
			checksum.assign(checksum.begin(), checksum.begin() + 4);        
			data += checksum;                                               

			BigInt bn(data);

			std::string base58check = bn.getInBase(58, RIPPLE_BASE58_CHARS);           
			std::string leading0s(countLeading0s(data), RIPPLE_BASE58_CHARS[0]);        

			free(arrPtr);

	        return str2jstring(env,leading0s + base58check);
}