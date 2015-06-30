package org.ripple.bouncycastle.jcajce;

import javax.crypto.SecretKey;

import org.ripple.bouncycastle.crypto.CharToByteConverter;
import org.ripple.bouncycastle.crypto.PBEParametersGenerator;

public class PBKDF1Key
    implements SecretKey
{
    private final char[] password;
    private final CharToByteConverter converter;

    /**
     * Basic constructor for a password based key with generation parameters for PBKDF1.
     *
     * @param password password to use.
     * @param converter the converter to use to turn the char array into octets.
     */
    public PBKDF1Key(char[] password, CharToByteConverter converter)
    {
        this.password = new char[password.length];
        this.converter = converter;

        System.arraycopy(password, 0, this.password, 0, password.length);
    }

    public char[] getPassword()
    {
        return password;
    }

    public String getAlgorithm()
    {
        return "PBKDF1";
    }

    public String getFormat()
    {
        return "RAW";
    }

    public byte[] getEncoded()
    {
        return converter.convert(password);
    }
}
