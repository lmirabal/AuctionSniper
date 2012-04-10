package com.auctionsniper.sniper;

import java.util.EventListener;

/**
 * User: lmirabal
 * Date: 3/31/12
 * Time: 3:31 PM
 * <p/>
 * Notifies sniper status (Joining, Bidding, Lost, Win)
 */
public interface SniperListener extends EventListener {
    void sniperStateChanged(final SniperSnapshot sniperSnapshot);
}
