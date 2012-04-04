package com.auctionsniper.sniper;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.auction.AuctionEventListener;

/**
 * User: lmirabal
 * Date: 3/31/12
 * Time: 3:32 PM
 * <p/>
 * Process Auction events notifying sniper state
 */
public class AuctionSniper implements AuctionEventListener {
    //Auction the sniper is bidding to
    private Auction auction;
    private SniperListener sniperListener;

    private SniperSnapshot snapshot;

    public AuctionSniper(String itemId, Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.snapshot = SniperSnapshot.joining(itemId);
    }

    @Override
    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FromSniper:
                snapshot = snapshot.winning(price);
                break;
            case FromOtherBidder:
                final int bid = price + increment;
                auction.bid(bid);
                snapshot = snapshot.bidding(price, bid);
                break;
        }
        notifyChange();
    }

    private void notifyChange() {
        sniperListener.sniperStateChanged(snapshot);
    }
}
