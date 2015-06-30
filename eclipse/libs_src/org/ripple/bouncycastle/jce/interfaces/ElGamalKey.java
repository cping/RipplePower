package org.ripple.bouncycastle.jce.interfaces;

import javax.crypto.interfaces.DHKey;

import org.ripple.bouncycastle.jce.spec.ElGamalParameterSpec;

public interface ElGamalKey
    extends DHKey
{
    public ElGamalParameterSpec getParameters();
}
