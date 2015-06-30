package org.ripple.bouncycastle.crypto;

public interface CharToByteConverter
{
    byte[] convert(char[] password);
}
