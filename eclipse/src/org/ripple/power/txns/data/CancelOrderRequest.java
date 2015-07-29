package org.ripple.power.txns.data;

import org.ripple.power.txns.Const;

public class CancelOrderRequest {
	  static class CaOR_TxJson
	    {
	       public String TransactionType = "OfferCancel";
	       public String Account;
	       public String OfferSequence;
	       public int Fee = Const.MAX_FEE;
	    }
	  
	public String command = "submit";
    
    public CaOR_TxJson tx_json;
  
    public String secret;
}
