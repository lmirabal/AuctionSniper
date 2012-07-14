package com.auctionsniper.sniper;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.auction.AuctionHouse;
import com.auctionsniper.Item;
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
    public void joinAuction(Item item) {
        Auction auction = auctionHouse.auctionFor(item);
        final AuctionSniper sniper = new AuctionSniper(item, auction);
        auction.addAuctionEventListeners(sniper);
        collector.addSniper(sniper);
        auction.join();
    }
}
