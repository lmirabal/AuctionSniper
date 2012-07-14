package com.auctionsniper.sniper;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.auction.AuctionEventListener;
import com.auctionsniper.Item;
import com.auctionsniper.util.Announcer;

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
    private final Item item;
    private SniperSnapshot snapshot;
    private Announcer<SniperListener> sniperListeners = Announcer.to(SniperListener.class);

    public AuctionSniper(Item item, Auction auction) {
        this.item = item;
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(item.identifier);
    }

    public void addSniperListener(SniperListener sniperListener) {
        sniperListeners.addListener(sniperListener);
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
                if(item.allowsBid(bid)){
                auction.bid(bid);
                snapshot = snapshot.bidding(price, bid);
                }else{
                    snapshot = snapshot.losing(price);
                }
                break;
        }
        notifyChange();
    }

    private void notifyChange() {
        sniperListeners.announce().sniperStateChanged(snapshot);
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }
}
