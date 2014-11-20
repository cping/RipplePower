#include "SHA256.h"

typedef unsigned long long int ullint;
typedef unsigned int uint;
typedef unsigned short ushort;
typedef unsigned char uchar;

uint EndianSwap(uint n)
{
return ((n&0xFF)<<24) | ((n&0xFF00)<<8) | ((n&0xFF0000)>>8) | ((n&0xFF000000)>>24);
}

const uint K[64] =
{
    0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
    0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
    0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
    0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
    0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
    0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
    0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
    0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
};

uint rotl(uint x, uint y)
{
return (x<<y)|(x>>(32-y));
}

#define Ch(x, y, z) (z ^ (x & (y ^ z)))
#define Ma(x, y, z) ((y & z) | (x & (y | z)))

#define Tr(x,a,b,c) (rotl(x,a)^rotl(x,b)^rotl(x,c))

#define R(x) (work[x] = (rotl(work[x-2],15)^rotl(work[x-2],13)^((work[x-2])>>10)) + work[x-7] + (rotl(work[x-15],25)^rotl(work[x-15],14)^((work[x-15])>>3)) + work[x-16])
#define sharound(a,b,c,d,e,f,g,h,x,K) h+=Tr(e,7,21,26)+Ch(e,f,g)+K+x; d+=h; h+=Tr(a,10,19,30)+Ma(a,b,c);

#define sharound_s(a,b,c,d,e,f,g,h,x) h+=Tr(e,7,21,26)+Ch(e,f,g)+x; d+=h; h+=Tr(a,10,19,30)+Ma(a,b,c);

void Sha256_round(uint* s, unsigned char* data)
{
uint work[64];

uint* udata = (uint*)data;
for(uint i=0; i<16; ++i)
{
work[i] = EndianSwap(udata[i]);
}

uint A = s[0];
uint B = s[1];
uint C = s[2];
uint D = s[3];
uint E = s[4];
uint F = s[5];
uint G = s[6];
uint H = s[7];
sharound(A,B,C,D,E,F,G,H,work[0],K[0]);
sharound(H,A,B,C,D,E,F,G,work[1],K[1]);
sharound(G,H,A,B,C,D,E,F,work[2],K[2]);
sharound(F,G,H,A,B,C,D,E,work[3],K[3]);
sharound(E,F,G,H,A,B,C,D,work[4],K[4]);
sharound(D,E,F,G,H,A,B,C,work[5],K[5]);
sharound(C,D,E,F,G,H,A,B,work[6],K[6]);
sharound(B,C,D,E,F,G,H,A,work[7],K[7]);
sharound(A,B,C,D,E,F,G,H,work[8],K[8]);
sharound(H,A,B,C,D,E,F,G,work[9],K[9]);
sharound(G,H,A,B,C,D,E,F,work[10],K[10]);
sharound(F,G,H,A,B,C,D,E,work[11],K[11]);
sharound(E,F,G,H,A,B,C,D,work[12],K[12]);
sharound(D,E,F,G,H,A,B,C,work[13],K[13]);
sharound(C,D,E,F,G,H,A,B,work[14],K[14]);
sharound(B,C,D,E,F,G,H,A,work[15],K[15]);
sharound(A,B,C,D,E,F,G,H,R(16),K[16]);
sharound(H,A,B,C,D,E,F,G,R(17),K[17]);
sharound(G,H,A,B,C,D,E,F,R(18),K[18]);
sharound(F,G,H,A,B,C,D,E,R(19),K[19]);
sharound(E,F,G,H,A,B,C,D,R(20),K[20]);
sharound(D,E,F,G,H,A,B,C,R(21),K[21]);
sharound(C,D,E,F,G,H,A,B,R(22),K[22]);
sharound(B,C,D,E,F,G,H,A,R(23),K[23]);
sharound(A,B,C,D,E,F,G,H,R(24),K[24]);
sharound(H,A,B,C,D,E,F,G,R(25),K[25]);
sharound(G,H,A,B,C,D,E,F,R(26),K[26]);
sharound(F,G,H,A,B,C,D,E,R(27),K[27]);
sharound(E,F,G,H,A,B,C,D,R(28),K[28]);
sharound(D,E,F,G,H,A,B,C,R(29),K[29]);
sharound(C,D,E,F,G,H,A,B,R(30),K[30]);
sharound(B,C,D,E,F,G,H,A,R(31),K[31]);
sharound(A,B,C,D,E,F,G,H,R(32),K[32]);
sharound(H,A,B,C,D,E,F,G,R(33),K[33]);
sharound(G,H,A,B,C,D,E,F,R(34),K[34]);
sharound(F,G,H,A,B,C,D,E,R(35),K[35]);
sharound(E,F,G,H,A,B,C,D,R(36),K[36]);
sharound(D,E,F,G,H,A,B,C,R(37),K[37]);
sharound(C,D,E,F,G,H,A,B,R(38),K[38]);
sharound(B,C,D,E,F,G,H,A,R(39),K[39]);
sharound(A,B,C,D,E,F,G,H,R(40),K[40]);
sharound(H,A,B,C,D,E,F,G,R(41),K[41]);
sharound(G,H,A,B,C,D,E,F,R(42),K[42]);
sharound(F,G,H,A,B,C,D,E,R(43),K[43]);
sharound(E,F,G,H,A,B,C,D,R(44),K[44]);
sharound(D,E,F,G,H,A,B,C,R(45),K[45]);
sharound(C,D,E,F,G,H,A,B,R(46),K[46]);
sharound(B,C,D,E,F,G,H,A,R(47),K[47]);
sharound(A,B,C,D,E,F,G,H,R(48),K[48]);
sharound(H,A,B,C,D,E,F,G,R(49),K[49]);
sharound(G,H,A,B,C,D,E,F,R(50),K[50]);
sharound(F,G,H,A,B,C,D,E,R(51),K[51]);
sharound(E,F,G,H,A,B,C,D,R(52),K[52]);
sharound(D,E,F,G,H,A,B,C,R(53),K[53]);
sharound(C,D,E,F,G,H,A,B,R(54),K[54]);
sharound(B,C,D,E,F,G,H,A,R(55),K[55]);
sharound(A,B,C,D,E,F,G,H,R(56),K[56]);
sharound(H,A,B,C,D,E,F,G,R(57),K[57]);
sharound(G,H,A,B,C,D,E,F,R(58),K[58]);
sharound(F,G,H,A,B,C,D,E,R(59),K[59]);
sharound(E,F,G,H,A,B,C,D,R(60),K[60]);
sharound(D,E,F,G,H,A,B,C,R(61),K[61]);
sharound(C,D,E,F,G,H,A,B,R(62),K[62]);
sharound(B,C,D,E,F,G,H,A,R(63),K[63]);

s[0] += A;
s[1] += B;
s[2] +=	C;
s[3] += D;
s[4] += E;
s[5] += F;
s[6] += G;
s[7] += H;
}

const uint P[64] =
{
0xc28a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19c0174,
0x649b69c1, 0xf9be478a, 0x0fe1edc6, 0x240ca60c, 0x4fe9346f, 0x4d1c84ab, 0x61b94f1e, 0xf6f993db,
0xe8465162, 0xad13066f, 0xb0214c0d, 0x695a0283, 0xa0323379, 0x2bd376e9, 0xe1d0537c, 0x03a244a0,
0xfc13a4a5, 0xfafda43e, 0x56bea8bb, 0x445ec9b6, 0x39907315, 0x8c0d4e9f, 0xc832dccc, 0xdaffb65b,
0x1fed4f61, 0x2f646808, 0x1ff32294, 0x2634ccd7, 0xb0ebdefa, 0xd6fc592b, 0xa63c5c8f, 0xbe9fbab9,
0x0158082c, 0x68969712, 0x51e1d7e1, 0x5cf12d0d, 0xc4be2155, 0x7d7c8a34, 0x611f2c60, 0x036324af,
0xa4f08d87, 0x9e3e8435, 0x2c6dae30, 0x11921afc, 0xb76d720e, 0x245f3661, 0xc3a65ecb, 0x43b9e908
};

void Sha256_round_padding(uint* s)
{
uint A = s[0];
uint B = s[1];
uint C = s[2];
uint D = s[3];
uint E = s[4];
uint F = s[5];
uint G = s[6];
uint H = s[7];
sharound_s(A,B,C,D,E,F,G,H,P[0]);
sharound_s(H,A,B,C,D,E,F,G,P[1]);
sharound_s(G,H,A,B,C,D,E,F,P[2]);
sharound_s(F,G,H,A,B,C,D,E,P[3]);
sharound_s(E,F,G,H,A,B,C,D,P[4]);
sharound_s(D,E,F,G,H,A,B,C,P[5]);
sharound_s(C,D,E,F,G,H,A,B,P[6]);
sharound_s(B,C,D,E,F,G,H,A,P[7]);
sharound_s(A,B,C,D,E,F,G,H,P[8]);
sharound_s(H,A,B,C,D,E,F,G,P[9]);
sharound_s(G,H,A,B,C,D,E,F,P[10]);
sharound_s(F,G,H,A,B,C,D,E,P[11]);
sharound_s(E,F,G,H,A,B,C,D,P[12]);
sharound_s(D,E,F,G,H,A,B,C,P[13]);
sharound_s(C,D,E,F,G,H,A,B,P[14]);
sharound_s(B,C,D,E,F,G,H,A,P[15]);
sharound_s(A,B,C,D,E,F,G,H,P[16]);
sharound_s(H,A,B,C,D,E,F,G,P[17]);
sharound_s(G,H,A,B,C,D,E,F,P[18]);
sharound_s(F,G,H,A,B,C,D,E,P[19]);
sharound_s(E,F,G,H,A,B,C,D,P[20]);
sharound_s(D,E,F,G,H,A,B,C,P[21]);
sharound_s(C,D,E,F,G,H,A,B,P[22]);
sharound_s(B,C,D,E,F,G,H,A,P[23]);
sharound_s(A,B,C,D,E,F,G,H,P[24]);
sharound_s(H,A,B,C,D,E,F,G,P[25]);
sharound_s(G,H,A,B,C,D,E,F,P[26]);
sharound_s(F,G,H,A,B,C,D,E,P[27]);
sharound_s(E,F,G,H,A,B,C,D,P[28]);
sharound_s(D,E,F,G,H,A,B,C,P[29]);
sharound_s(C,D,E,F,G,H,A,B,P[30]);
sharound_s(B,C,D,E,F,G,H,A,P[31]);
sharound_s(A,B,C,D,E,F,G,H,P[32]);
sharound_s(H,A,B,C,D,E,F,G,P[33]);
sharound_s(G,H,A,B,C,D,E,F,P[34]);
sharound_s(F,G,H,A,B,C,D,E,P[35]);
sharound_s(E,F,G,H,A,B,C,D,P[36]);
sharound_s(D,E,F,G,H,A,B,C,P[37]);
sharound_s(C,D,E,F,G,H,A,B,P[38]);
sharound_s(B,C,D,E,F,G,H,A,P[39]);
sharound_s(A,B,C,D,E,F,G,H,P[40]);
sharound_s(H,A,B,C,D,E,F,G,P[41]);
sharound_s(G,H,A,B,C,D,E,F,P[42]);
sharound_s(F,G,H,A,B,C,D,E,P[43]);
sharound_s(E,F,G,H,A,B,C,D,P[44]);
sharound_s(D,E,F,G,H,A,B,C,P[45]);
sharound_s(C,D,E,F,G,H,A,B,P[46]);
sharound_s(B,C,D,E,F,G,H,A,P[47]);
sharound_s(A,B,C,D,E,F,G,H,P[48]);
sharound_s(H,A,B,C,D,E,F,G,P[49]);
sharound_s(G,H,A,B,C,D,E,F,P[50]);
sharound_s(F,G,H,A,B,C,D,E,P[51]);
sharound_s(E,F,G,H,A,B,C,D,P[52]);
sharound_s(D,E,F,G,H,A,B,C,P[53]);
sharound_s(C,D,E,F,G,H,A,B,P[54]);
sharound_s(B,C,D,E,F,G,H,A,P[55]);
sharound_s(A,B,C,D,E,F,G,H,P[56]);
sharound_s(H,A,B,C,D,E,F,G,P[57]);
sharound_s(G,H,A,B,C,D,E,F,P[58]);
sharound_s(F,G,H,A,B,C,D,E,P[59]);
sharound_s(E,F,G,H,A,B,C,D,P[60]);
sharound_s(D,E,F,G,H,A,B,C,P[61]);
sharound_s(C,D,E,F,G,H,A,B,P[62]);
sharound_s(B,C,D,E,F,G,H,A,P[63]);

s[0] += A;
s[1] += B;
s[2] +=	C;
s[3] += D;
s[4] += E;
s[5] += F;
s[6] += G;
s[7] += H;
}


//assumes input is 512 bytes
void Sha256(unsigned char* in, unsigned char* out)
{
uint s[8]= {0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19};
Sha256_round(s, in);
Sha256_round(s, in+64);
Sha256_round(s, in+128);
Sha256_round(s, in+192);
Sha256_round(s, in+256);
Sha256_round(s, in+320);
Sha256_round(s, in+384);
Sha256_round(s, in+448);
Sha256_round_padding(s);

uint* outi = (uint*)out;
for(uint i=0; i<8; ++i)
outi[i] = EndianSwap(s[i]);
}