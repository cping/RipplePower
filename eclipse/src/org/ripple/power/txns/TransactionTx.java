package org.ripple.power.txns;

import org.joda.time.DateTime;

public class TransactionTx {

    public String account;

    public IssuedCurrency currency;

    public String fee;
    
    public String mode;
    
    public String trusted;
    
    public IssuedCurrency get;

    public IssuedCurrency pay;
    
    public long flags;

    public long offersSequence;
    
    public long sequence;
    
    public DateTime date;

    public String hash;

    public String clazz;
    
    public long inLedger;
    
    public long ledgerIndex;
    
    public String counterparty;

}
