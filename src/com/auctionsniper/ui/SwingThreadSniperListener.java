package com.auctionsniper.ui;

import com.auctionsniper.sniper.SniperListener;
import com.auctionsniper.sniper.SniperSnapshot;

import javax.swing.*;

/**
 * User: lmirabal
 * Date: 4/4/12
 * Time: 10:14 PM
 */ /*
 * Decorator for SniperListener which calls the listener updates in the event thread
 */
public class SwingThreadSniperListener implements SniperListener {

    private final SniperListener sniperListener;

    public SwingThreadSniperListener(SniperListener sniperListener) {
        this.sniperListener = sniperListener;
    }

    @Override
    public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                sniperListener.sniperStateChanged(sniperSnapshot);
            }
        });
    }
}
