package com.auctionsniper.sniper;

/**
 * User: lmirabal
 * Date: 3/31/12
 * Time: 3:31 PM
 * <p/>
 * Notifies sniper status (Joining, Bidding, Lost, Win)
 */
public interface SniperListener {
    void sniperStateChanged(final SniperSnapshot sniperSnapshot);
}
