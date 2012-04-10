package com.auctionsniper.sniper;

import java.util.EventListener;

/**
 * User: lmirabal
 * Date: 4/9/12
 * Time: 6:17 PM
 */
public interface PortfolioListener extends EventListener {
    void sniperAdded(AuctionSniper auctionSniper);
}
