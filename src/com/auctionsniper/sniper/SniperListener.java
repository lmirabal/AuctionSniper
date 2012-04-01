package com.auctionsniper.sniper;

/**
 * User: lmirabal
 * Date: 3/31/12
 * Time: 3:31 PM
 *
 * Notifies sniper status (Joining, Bidding, Lost, Win)
 *
 */
public interface SniperListener {
    void sniperLost();
    void sniperBidding();
    void sniperWinning();
    void sniperWon();
}
