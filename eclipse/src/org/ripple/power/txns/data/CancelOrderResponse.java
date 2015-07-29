package org.ripple.power.txns.data;

import org.ripple.power.txns.Const;

public class CancelOrderResponse {
    
	static class CancelResult
    {
        public String engine_result;
        public int engine_result_code;
        public  String engine_result_message;
        public String tx_blob;
        public Object tx_json;

        public boolean getResultOK()
        {
            return Const.OkResultCodes.contains(engine_result); 
        }
    }
	
   public CancelResult result;
   public String status;
   public String type ;
}
