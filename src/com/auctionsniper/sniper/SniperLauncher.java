package com.auctionsniper.sniper;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.auction.AuctionHouse;
import com.auctionsniper.ui.UserRequestListener;

/**
 * User: lmirabal
 * Date: 4/6/12
 * Time: 8:04 PM
 */
public class SniperLauncher implements UserRequestListener {

    private final AuctionHouse auctionHouse;
    private SniperCollector collector;

    public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
        this.auctionHouse = auctionHouse;
        this.collector = collector;
    }

    @Override
    public void joinAuction(String itemId) {
        Auction auction = auctionHouse.auctionFor(itemId);
        final AuctionSniper sniper = new AuctionSniper(itemId, auction);
        auction.addAuctionEventListeners(sniper);
        collector.addSniper(sniper);
        auction.join();
    }
}
