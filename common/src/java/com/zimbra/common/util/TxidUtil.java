package com.zimbra.common.util;

import com.zimbra.common.localconfig.LC;

import com.synacor.txid.TxidFactory;

public class TxidUtil {

    public static TxidFactory txidFactory;

    /**
     * Try and load specified TxidFactory implementation; otherwise fallback to default
     */
    static {
        try {
		    txidFactory = TxidFactory.getInstance(LC.zimbra_class_txidfactory.value());
        } catch (Exception e) {
		    txidFactory = TxidFactory.getDefaultInstance();
        }
    }

    /**
     * Returns a new TXID.
     */
    public static String generateTxid() {
        return txidFactory.getTxid();
    }
}

