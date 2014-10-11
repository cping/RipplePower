package org.address;

import java.io.UnsupportedEncodingException;

import org.address.database.AddressManager;
import org.address.database.BitcoinBlockToDataBase;
import org.address.utils.CoinUtils;
import org.ripple.power.config.LSystem;

import com.ripple.config.Config;
import com.ripple.core.coretypes.hash.Hash256;
import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.types.known.tx.txns.Payment;

public class Test {
	
	public static void main(String[]args) throws Exception{
		
		AddressManager address=	BitcoinBlockToDataBase.go("D:\\Bitcoin\\blocks", "F:\\bitcoin_data", 146);
		address.clearRepeatData();
	//	address.updateDataToBlock();
	}

}
