#ifndef __KEY_H__
#define __KEY_H__

#include "uchar_vector.h"

#include <openssl/ec.h>
#include <openssl/bn.h>
#include <openssl/ecdsa.h>
#include <openssl/pem.h>
#include <openssl/err.h>

#include <string>

// --> seed
// <-- private root generator + public root generator
EC_KEY* GenerateRootDeterministicKey(const uint128& seed);
EC_KEY* GeneratePublicDeterministicKey(const uchar_vector& generator, int seq);
static BIGNUM* makeHash(const uchar_vector& generator, int seq, BIGNUM* order);

class CKey
{
protected:
    EC_KEY* pkey;
    bool fSet;

public:
    CKey(const uint128& passPhrase) : fSet(false)
    {
        pkey = GenerateRootDeterministicKey(passPhrase);
        fSet = true;
        assert(pkey);
    }

    CKey(const uchar_vector& generator, int n) : fSet(false)
    { // public deterministic key
        pkey = GeneratePublicDeterministicKey(generator, n);
        fSet = true;
        assert(pkey);
    }

    std::vector<unsigned char> GetPubKey() const
    {
        int nSize = i2o_ECPublicKey(pkey, NULL);
        assert(nSize<=33);
        if (!nSize)
            throw std::runtime_error("CKey::GetPubKey() : i2o_ECPublicKey failed");
        std::vector<unsigned char> vchPubKey(33, 0);
        unsigned char* pbegin = &vchPubKey[0];
        if (i2o_ECPublicKey(pkey, &pbegin) != nSize)
            throw std::runtime_error("CKey::GetPubKey() : i2o_ECPublicKey returned unexpected size");
        assert(vchPubKey.size()<=33);
        return vchPubKey;
    }

    ~CKey()
    {
        EC_KEY_free(pkey);
    }
};

static BIGNUM* makeHash(const uchar_vector& generator, int seq, BIGNUM* order)
{
    int subSeq=0;
    BIGNUM* ret=NULL;
    do
    {
        uchar_vector s = generator;

        s.push_back(static_cast<unsigned char>(seq >> 24));
        s.push_back(static_cast<unsigned char>((seq >> 16) & 0xff));
        s.push_back(static_cast<unsigned char>((seq >> 8) & 0xff));
        s.push_back(static_cast<unsigned char>(seq & 0xff));

        s.push_back(static_cast<unsigned char>(subSeq >> 24));
        s.push_back(static_cast<unsigned char>((subSeq >> 16) & 0xff));
        s.push_back(static_cast<unsigned char>((subSeq >> 8) & 0xff));
        s.push_back(static_cast<unsigned char>(subSeq & 0xff));
        subSeq++;

        uint256 root[2];
        SHA512(&(s.front()), s.size(), (unsigned char *)root);
        memset(&(s.front()), 0, s.size());
        s.clear();

        ret = BN_bin2bn((const unsigned char *) &root[0], sizeof(uint256), ret);
        if (!ret) return NULL;
    } while (BN_is_zero(ret) || (BN_cmp(ret, order)>=0));

    return ret;
}

// Take ripple address.
// --> root public generator (consumes)
// <-- root public generator in EC format
EC_KEY* GenerateRootPubKey(BIGNUM* pubGenerator)
{
    if (pubGenerator == NULL)
    {
        assert(false);
        return NULL;
    }

    EC_KEY* pkey = EC_KEY_new_by_curve_name(NID_secp256k1);
    if (!pkey)
    {
        BN_free(pubGenerator);
        return NULL;
    }
    EC_KEY_set_conv_form(pkey, POINT_CONVERSION_COMPRESSED);

    EC_POINT* pubPoint = EC_POINT_bn2point(EC_KEY_get0_group(pkey), pubGenerator, NULL, NULL);
    BN_free(pubGenerator);
    if(!pubPoint)
    {
        assert(false);
        EC_KEY_free(pkey);
        return NULL;
    }

    if(!EC_KEY_set_public_key(pkey, pubPoint))
    {
        assert(false);
        EC_POINT_free(pubPoint);
        EC_KEY_free(pkey);
        return NULL;
    }

    EC_POINT_free(pubPoint);
    return pkey;
}

EC_KEY* GeneratePublicDeterministicKey(const uchar_vector& generator, int seq)
{ // publicKey(n) = rootPublicKey EC_POINT_+ Hash(pubHash|seq)*point
    BIGNUM* bngenerator = BN_bin2bn(&generator[0], generator.size(), NULL);
    EC_KEY*         rootKey     = GenerateRootPubKey(bngenerator);
    const EC_POINT* rootPubKey  = EC_KEY_get0_public_key(rootKey);
    BN_CTX*         ctx         = BN_CTX_new();
    EC_KEY*         pkey        = EC_KEY_new_by_curve_name(NID_secp256k1);
    EC_POINT*       newPoint    = 0;
    BIGNUM*         order       = 0;
    BIGNUM*         hash        = 0;
    bool            success     = true;

    if (!ctx || !pkey)  success = false;

    if (success)
        EC_KEY_set_conv_form(pkey, POINT_CONVERSION_COMPRESSED);

    if (success) {
        newPoint    = EC_POINT_new(EC_KEY_get0_group(pkey));
        if(!newPoint)   success = false;
    }

    if (success) {
        order       = BN_new();

        if(!order || !EC_GROUP_get_order(EC_KEY_get0_group(pkey), order, ctx))
            success = false;
    }

    // Calculate the private additional key.
    if (success) {
        hash        = makeHash(generator, seq, order);
        if(!hash)   success = false;
    }

    if (success) {
        // Calculate the corresponding public key.
        EC_POINT_mul(EC_KEY_get0_group(pkey), newPoint, hash, NULL, NULL, ctx);

        // Add the master public key and set.
        EC_POINT_add(EC_KEY_get0_group(pkey), newPoint, newPoint, rootPubKey, ctx);
        EC_KEY_set_public_key(pkey, newPoint);
    }

    if (order)              BN_free(order);
    if (hash)               BN_free(hash);
    if (newPoint)           EC_POINT_free(newPoint);
    if (ctx)                BN_CTX_free(ctx);
    if (rootKey)            EC_KEY_free(rootKey);
    if (pkey && !success)   EC_KEY_free(pkey);

    return success ? pkey : NULL;
}

// --> seed
// <-- private root generator + public root generator
EC_KEY* GenerateRootDeterministicKey(const uint128& seed)
{
    BN_CTX* ctx=BN_CTX_new();
    if(!ctx) return NULL;

    EC_KEY* pkey=EC_KEY_new_by_curve_name(NID_secp256k1);
    if(!pkey)
    {
        BN_CTX_free(ctx);
        return NULL;
    }
    EC_KEY_set_conv_form(pkey, POINT_CONVERSION_COMPRESSED);

    BIGNUM* order=BN_new();
    if(!order)
    {
        BN_CTX_free(ctx);
        EC_KEY_free(pkey);
        return NULL;
    }
    if(!EC_GROUP_get_order(EC_KEY_get0_group(pkey), order, ctx))
    {
        assert(false);
        BN_free(order);
        EC_KEY_free(pkey);
        BN_CTX_free(ctx);
        return NULL;
    }

    BIGNUM *privKey=NULL;
    int seq=0;
    do
    { // private key must be non-zero and less than the curve's order
        uchar_vector s;
        s.insert(s.end(), seed.begin(), seed.end());
        s.push_back(static_cast<unsigned char>(seq >> 24));
        s.push_back(static_cast<unsigned char>((seq >> 16) & 0xff));
        s.push_back(static_cast<unsigned char>((seq >> 8) & 0xff));
        s.push_back(static_cast<unsigned char>(seq & 0xff));
        seq++;

        uint256 root[2];
        SHA512(&(s.front()), s.size(), (unsigned char *)root);
        memset(&(s.front()), 0, s.size());
        s.clear();

        privKey=BN_bin2bn((const unsigned char *) &root[0], sizeof(uint256), privKey);
        if(privKey==NULL)
        {
            EC_KEY_free(pkey);
            BN_free(order);
            BN_CTX_free(ctx);
        }
        root[0].zero();
        root[1].zero();
    } while(BN_is_zero(privKey) || (BN_cmp(privKey, order)>=0));

    BN_free(order);

    if(!EC_KEY_set_private_key(pkey, privKey))
    { // set the random point as the private key
        assert(false);
        EC_KEY_free(pkey);
        BN_clear_free(privKey);
        BN_CTX_free(ctx);
        return NULL;
    }

    EC_POINT *pubKey=EC_POINT_new(EC_KEY_get0_group(pkey));
    if(!EC_POINT_mul(EC_KEY_get0_group(pkey), pubKey, privKey, NULL, NULL, ctx))
    { // compute the corresponding public key point
        assert(false);
        BN_clear_free(privKey);
        EC_POINT_free(pubKey);
        EC_KEY_free(pkey);
        BN_CTX_free(ctx);
        return NULL;
    }
    BN_clear_free(privKey);
    if(!EC_KEY_set_public_key(pkey, pubKey))
    {
        assert(false);
        EC_POINT_free(pubKey);
        EC_KEY_free(pkey);
        BN_CTX_free(ctx);
        return NULL;
    }
    EC_POINT_free(pubKey);

    BN_CTX_free(ctx);

#ifdef EC_DEBUG
    assert(EC_KEY_check_key(pkey)==1); // CAUTION: This check is *very* expensive
#endif
    return pkey;
}

#endif
