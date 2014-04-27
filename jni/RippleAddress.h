#ifndef _RIPPLE_ADDRESS_H_
#define _RIPPLE_ADDRESS_H_

#include "base58.h"
#include "uint256.h"
#include "key.h"
#include "uchar_vector.h"


typedef enum {
    VER_NONE                = 1,
    VER_NODE_PUBLIC         = 28,
    VER_NODE_PRIVATE        = 32,
    VER_ACCOUNT_ID          = 0,
    VER_ACCOUNT_PUBLIC      = 35,
    VER_ACCOUNT_PRIVATE     = 34,
    VER_FAMILY_GENERATOR    = 41,
    VER_FAMILY_SEED         = 33,
} VersionEncoding;

class RippleAddress : public CBase58Data
{
public:
    void setSeed(uint128 hash);
    uint128 getSeed() const;
    const std::vector<unsigned char>& getAccountPublic() const;
    void setAccountPublic(const uchar_vector& generator, int seq);
    uint160 getAccountID() const;
    void setAccountID(const uint160& hash160);
    std::string humanAccountID() const;
    std::string humanSeed() const;
};

void RippleAddress::setSeed(uint128 hash)
{
    SetData(VER_FAMILY_SEED, hash.begin(), 16);
}

uint128 RippleAddress::getSeed() const
{
    return uint128(vchData);
}

static RippleAddress createGeneratorPublic(const RippleAddress& naSeed)
{
    CKey            ckSeed(naSeed.getSeed());
    RippleAddress   naNew;
    naNew.SetData(VER_FAMILY_GENERATOR, ckSeed.GetPubKey());
    return naNew;
}

const std::vector<unsigned char>& RippleAddress::getAccountPublic() const
{
    return vchData;
}

void RippleAddress::setAccountPublic(const uchar_vector& generator, int seq)
{
    CKey    pubkey(generator, seq);
    SetData(VER_ACCOUNT_PUBLIC, pubkey.GetPubKey());
}

uint160 RippleAddress::getAccountID() const
{
    switch (nVersion) {
    case VER_NONE:
        throw std::runtime_error("unset source - getAccountID");

    case VER_ACCOUNT_ID:
        return uint160(vchData);

    case VER_ACCOUNT_PUBLIC:
        // Note, we are encoding the left.
        return Hash160(vchData);

    default:
        throw std::runtime_error(str(boost::format("bad source: %d") % int(nVersion)));
    }
}

void RippleAddress::setAccountID(const uint160& hash160)
{
    SetData(VER_ACCOUNT_ID, hash160.begin(), 20);
}

std::string RippleAddress::humanAccountID() const
{
    switch (nVersion) {
    case VER_NONE:
        throw std::runtime_error("unset source - humanAccountID");

    case VER_ACCOUNT_ID:
    {
        return ToString();
    }
    
    case VER_ACCOUNT_PUBLIC:
    {
        RippleAddress   accountID;

        (void) accountID.setAccountID(getAccountID());

        return accountID.ToString();
    }

    default:
        throw std::runtime_error(str(boost::format("bad source: %d") % int(nVersion)));
    }
}

std::string RippleAddress::humanSeed() const
{
    switch (nVersion) {
    case VER_NONE:
        throw std::runtime_error("unset source - humanSeed");

    case VER_FAMILY_SEED:
        return ToString();

    default:
        throw std::runtime_error(str(boost::format("bad source: %d") % int(nVersion)));
    }
}

#endif
