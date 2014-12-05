package org.ripple.power.blockchain.list;

import java.util.ArrayList;

import org.ripple.power.utils.ByteUtils;

public class RPList extends ArrayList<RPElement> implements RPElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	byte[] rpData;

    public void setRLPData(byte[] rpData) {
        this.rpData = rpData;
    }
    
	@Override
    public byte[] getRPData() {
        return rpData;
    }
	
	public static String recursive(RPElement element) {
		if (element == null){
			throw new RuntimeException("RPElement object null");
		}
		StringBuilder sbr = new StringBuilder();
		if (element instanceof RPList) {
			RPList rpList = (RPList) element;
			sbr.append("[");			
			for (RPElement singleElement : rpList) {
				sbr.append(recursive(singleElement));
			}
			sbr.append("]");
		} else {
			String hex = ByteUtils.toHexString(((RPItem) element).getRPData());
			sbr.append(hex + ", ");
		}
		return sbr.toString();
	}



}
