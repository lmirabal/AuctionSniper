package com.auctionsniper.ui;

import com.auctionsniper.sniper.SniperListener;
import com.auctionsniper.sniper.SniperSnapshot;

import javax.swing.*;

/**
 * User: lmirabal
 * Date: 4/4/12
 * Time: 10:14 PM
 */ /*
 * Decorator for SniperListener which fires table model updates in the event thread
 */
public class SwingThreadSniperListener implements SniperListener {

    private final SnipersTableModel snipers;

    public SwingThreadSniperListener(SnipersTableModel snipers) {
        this.snipers = snipers;
    }

    @Override
    public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                snipers.sniperStateChanged(sniperSnapshot);
            }
        });
    }
}
