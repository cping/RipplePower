package org.ripple.power.txns.btc;


public class BitcoinConsensus {

    public static void init() {

    } 

    public static int getVersion() {
        return -1;
    }

    public static boolean verifyScript(TransactionInput txInput, TransactionOutput txOutput)
                                        throws ScriptException {
        return ScriptParser.process(txInput, txOutput, BTCLoader.blockStore.getChainHeight());
    }
}
